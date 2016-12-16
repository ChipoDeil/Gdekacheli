package com.example.yaroslav.gdekacheli;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FullView extends AppCompatActivity {
    ArrayList<String[]> array;
    String id;
    Drawable img = null;
    ImageView iv;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);
        tv = (TextView) findViewById(R.id.test);
        iv = (ImageView)findViewById(R.id.imageView);
        Intent intent = getIntent();
        id = intent.getStringExtra("info");
        //вызов асинктаска
        if (ContextCompat.checkSelfPermission(FullView.this, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
            Context context = getApplicationContext();
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean isAvailable = false;
            try{
                if (cm != null){
                    Log.d("cm", "!null");
                    NetworkInfo ni = cm.getActiveNetworkInfo();
                    if (ni != null){
                        Log.d("ni", "!null");
                        isAvailable = ni.isConnectedOrConnecting();
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            Log.d("internet", "on");
            if (isAvailable) {
                new FullView.getcords().execute();
            }else{
                Toast.makeText(FullView.this, "Ваше подключение к сети интернет нестабильно. " +
                        "Попробуйте открыть приложение ещё раз.", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(FullView.this, "Проверьте ваше подключение к сети интернет.", Toast.LENGTH_SHORT).show();
        }
        //<вызов асинктаска
    }
    class getcords extends AsyncTask<ArrayList<String[]>, Void, ArrayList<String[]>> {
        @Override
        protected ArrayList<String[]> doInBackground(ArrayList<String[]>... params) {
            JSONParser parser = new JSONParser();
            try {
                URL oracle = new URL("http://keklol.ru/gdekacheli/getcoords.php?id="+id);
                URLConnection uc = oracle.openConnection();
                BufferedReader input = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine = input.readLine();
                ArrayList<String[]> arr = new ArrayList<String[]>();
                JSONArray a = (JSONArray) parser.parse(inputLine);
                for (Object o : a) {
                    JSONObject users = (JSONObject) o;
                    String[] mass = {users.get("id").toString(), users.get("title").toString(), users.get("descr").toString(), users.get("img").toString()};
                    if (!(mass[3]).isEmpty()){
                        //Достаем картинку
                        InputStream is = (InputStream) new URL("http://keklol.ru/gdekacheli/"+mass[3]).getContent();
                        img = Drawable.createFromStream(is, "123");
                    }
                    arr.add(mass);
                    array = arr;
                }
                input.close();
                return arr;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("error", "null1");
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("error", "null2");
                return null;
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("error", "null3");
                return null;
            } catch (org.json.simple.parser.ParseException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<String[]> strings) {
            super.onPostExecute(strings);
            try {
                tv.setText(array.get(0)[2]);
                iv.setImageDrawable(img);
            }catch(NullPointerException e){

            }

        }
    }
}
