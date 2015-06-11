package com.example.matija_pc.carewell;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

/**
 * Created by Matija-PC on 9.6.2015..
 */
public class HttpMethods {

    public static JSONObject[] postMethod(String url, List<NameValuePair> nameValuePairs) {
        InputStream inputStream = null;
        String json = "{";
        JSONObject[] listOfJsonObjects = null;
        try {

            //make JSON string from name-value pairs
            for (int i = 0; i < nameValuePairs.size(); i++) {
                NameValuePair nvp = nameValuePairs.get(i);
                json = json + "\"" + nvp.getName() + "\"" + ":" + "\"" + nvp.getValue() + "\"";
                //if this is the last pair, close bracket
                if (i == nameValuePairs.size()-1) {
                    json = json + "}";
                }
                //else, add comma
                else {
                    json = json + ",";
                }
            }

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(json);
            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            listOfJsonObjects = JsonParser.parseJson(inputStream);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return listOfJsonObjects;
    }

    public static JSONObject[] getMethod(String url) {

        InputStream inputStream = null;
        JSONObject[] listOfJsonObjects = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(URI.create(url));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() != 200) return null;
            HttpEntity httpEntity = httpResponse.getEntity();
            inputStream = httpEntity.getContent();
            listOfJsonObjects = JsonParser.parseJson(inputStream);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return listOfJsonObjects;
    }
}
