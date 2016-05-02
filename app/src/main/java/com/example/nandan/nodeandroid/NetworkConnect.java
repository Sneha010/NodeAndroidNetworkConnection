package com.example.nandan.nodeandroid;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nandan on 2/5/16.
 */
public class NetworkConnect extends AsyncTask<String, Void, JSONObject> {

    private static String TAG = "NetworkConnect:";
    JSONObject mFactJSON=null;

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject jsonResponse = null;
        int responseCode;

        try {
            URL requestUrl = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.connect();
            responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Status Code: " + responseCode);
            } else {
                InputStream stream = connection.getInputStream();
                Reader reader = new InputStreamReader(stream);
                int contentLength = connection.getContentLength();
                if (contentLength > 0) {
                    char[] buffer = new char[contentLength ];
                    reader.read(buffer);
                    Log.d(TAG, buffer.toString());
                    jsonResponse = new JSONObject(String.valueOf(buffer));
                } else {
                    String jsonData = isToString(stream);
                    jsonResponse = new JSONObject(jsonData);
                }

                Log.d(TAG, jsonResponse.toString(2));
                connection.disconnect();
            }

        }
        catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        }


        return jsonResponse;
    }

    protected void onPostExecute(JSONObject data) {
        mFactJSON = data;
    }

    protected String isToString(InputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[65536];

        try {
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
        } catch (IOException e) {
            Log.e(TAG, "Exception caught: ", e);
        }

        return buffer.toString();
    }
}
