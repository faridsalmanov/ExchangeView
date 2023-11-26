package com.example.exchangeview;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DataLoader.DataLoaderListener {

    private ListView currencyListView;
    private EditText filterEditText;
    private ArrayAdapter<String> adapter;
    private ProgressBar progressBar;
    private DataLoader dataLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currencyListView = findViewById(R.id.currencyListView);
        filterEditText = findViewById(R.id.filterEditText);
        progressBar = findViewById(R.id.progressBar);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        currencyListView.setAdapter(adapter);

        dataLoader = new DataLoader(this, adapter, this);

        // Set up text change listener for filtering
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        loadData();
    }

    private void loadData() {

        progressBar.setVisibility(View.VISIBLE);

        dataLoader.loadData();
    }

    @Override
    public void onDataLoaded(List<String> currencyRates) {

        adapter.clear();
        adapter.addAll(currencyRates);
        adapter.notifyDataSetChanged();

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDataLoadError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
    }


    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
