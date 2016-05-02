package com.example.nandan.nodeandroid;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button btnGet,btnPost;
    EditText editText;
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPost = (Button)findViewById(R.id.btn);
        btnGet = (Button)findViewById(R.id.btnGet);
        editText = (EditText)findViewById(R.id.editText);
        txt = (TextView)findViewById(R.id.txt);

       final JSONObject jObj = new JSONObject();
        try {
            jObj.put("name",editText.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Post().execute(jObj);
            }
        });
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new Get().execute();
            }
        });
    }


    public class Get extends AsyncTask<Void,Void,String>
    {
        String res;
        @Override
        protected String doInBackground(Void... params)
        {
            try {
                res =  get("http://192.168.0.145:8080/get");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        txt.setText(s);

        }


    private String get(String myurl)  throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Status code", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is,500);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }

        }

    }

        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }


    }

    public class Post extends AsyncTask<JSONObject,Void,JSONObject>
    {
        JSONObject resJson;
        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            resJson = post(params[0]);
            return resJson;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                if(jsonObject!=null)
                txt.setText(jsonObject.get("response").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private JSONObject post(JSONObject param) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            JSONObject res;

            try {
                URL url = new URL("http://192.168.0.145:8080/post");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(param.toString());
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }

                res = new JSONObject();
                res.put("response",buffer.toString());
                return  res;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
