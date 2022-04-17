package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private class GetData extends AsyncTask<String, String, String>{

        protected void onPreExecute(){
                super.onPreExecute();
                result_info.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine())!= null){
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(connection != null)
                    connection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                JSONObject weather = new JSONObject(result);
                result_info.setText("Температура: "+ weather.getJSONObject("main").getDouble("temp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private EditText text_box;
    private Button btn_load;
    private TextView result_info;
    VideoView videoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_box = findViewById(R.id.text_box);
        btn_load = findViewById(R.id.btn_load);
        result_info = findViewById(R.id.result_info);
        videoPlayer = findViewById(R.id.videoView);
        Uri myVideoUri= Uri.parse( "android.resource://" + getPackageName() + "/" + R.raw.scanner);
        videoPlayer.setVideoURI(myVideoUri);
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoPlayer.start();

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String key = "&appid=627a6f5fdb35bd933bd0d0c596f48985&lang=ru";
                String city = text_box.getText().toString().trim();
                if(city.equals("")){
                    Toast.makeText(MainActivity.this, R.string.no_input,Toast.LENGTH_LONG).show();
                }
                else{
                    String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+key;
                    new GetData().execute(url);
                }
            }
        });

    }
}