package com.example.exchangeview;

import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

public class DataLoader {

    public interface DataLoaderListener {
        void onDataLoaded(List<String> currencyRates);

        void onDataLoadError(String errorMessage);
    }

    private final WeakReference<MainActivity> activityReference;
    private final ArrayAdapter<String> adapter;
    private final DataLoaderListener listener;

    public DataLoader(MainActivity activity, ArrayAdapter<String> adapter, DataLoaderListener listener) {
        this.activityReference = new WeakReference<>(activity);
        this.adapter = adapter;
        this.listener = listener;
    }

    public void loadData() {
        String url = "http://www.floatrates.com/daily/usd.xml";
        downloadDataUsingRetrofit(url);
    }

    private void downloadDataUsingRetrofit(String urlString) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(urlString)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);
                Call<String> call = apiService.getData();
                Response<String> response = call.execute();

                if (response.isSuccessful()) {
                    String result = response.body();
                    handleResult(result);
                } else {
                    handleFailure("Error downloading data: " + response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
                handleFailure("Error downloading data: " + e.getMessage());
            }
        });
    }

    private void handleResult(String result) {
        MainActivity activity = activityReference.get();
        if (activity != null) {
            Parser parser = new Parser();
            List<String> currencyRates = parser.parseXmlData(result);

            new Handler(Looper.getMainLooper()).post(() -> {
                adapter.clear();
                adapter.addAll(currencyRates);
                adapter.notifyDataSetChanged();
                activity.hideProgressBar();

                if (listener != null) {
                    listener.onDataLoaded(currencyRates);
                }
            });
        }
    }

    private void handleFailure(String errorMessage) {
        MainActivity activity = activityReference.get();
        if (activity != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                activity.hideProgressBar();

                if (listener != null) {
                    listener.onDataLoadError(errorMessage);
                }
            });
        }
    }

    public interface ApiService {
        @GET("/")
        Call<String> getData();
    }
}
