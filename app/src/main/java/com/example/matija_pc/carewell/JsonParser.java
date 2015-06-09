package com.example.matija_pc.carewell;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Matija-PC on 8.6.2015..
 * Parses input stream to list of JSON objects
 */
public class JsonParser {

    public static JSONObject[] parseJson(InputStream inputStream) {

        String result = "";
        JSONObject[] listOfJsonObjects = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            inputStream.close();
            result = stringBuilder.toString();
            Log.i("MainActivity", result);

            if (result.charAt(0) != '[') {
                result = "[" + result + "]";
            }

            JSONArray jsonArray = new JSONArray(result);
            listOfJsonObjects = new JSONObject[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                listOfJsonObjects[i] = jsonArray.getJSONObject(i);
            }

        } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return  listOfJsonObjects;
    }
}
