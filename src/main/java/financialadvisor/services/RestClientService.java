package financialadvisor.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RestClientService {
    public static String getDataByAPI(String url) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }


    public static void main(String[] args) throws IOException {
        String output = getDataByAPI("https://api.tase.co.il/api/ChartData/ChartData/?ct=1&ot=1&lang=1&cf=0&cp=7&cv=0&cl=0&cgt=1&oid=604611");
        System.out.println(output);
    }
}
