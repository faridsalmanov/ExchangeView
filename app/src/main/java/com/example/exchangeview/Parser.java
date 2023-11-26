package com.example.exchangeview;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Parser {


    public List<String> parseXmlData(String xmlData) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Use ByteArrayInputStream with UTF-8 encoding
            InputStream is = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));

            Document document = builder.parse(is);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("item");
            int length = nodeList.getLength();

            List<String> currencyRates = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String currencyName = element.getElementsByTagName("targetName")
                            .item(0)
                            .getTextContent();

                    String exchangeRate = element.getElementsByTagName("exchangeRate")
                            .item(0)
                            .getTextContent();

                    currencyRates.add(currencyName + " - " + exchangeRate);
                }
            }

            return currencyRates;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public List<String> parseJsonData(String jsonData) {
        try {
            List<String> currencyRates = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject ratesObject = jsonObject.getJSONObject("rates");


            JSONArray currencyCodes = ratesObject.names();

            if (currencyCodes != null) {
                for (int i = 0; i < currencyCodes.length(); i++) {
                    String currencyCode = currencyCodes.getString(i);
                    double exchangeRate = ratesObject.getDouble(currencyCode);
                    currencyRates.add(currencyCode + " - " + exchangeRate);
                }
            }

            return currencyRates;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
