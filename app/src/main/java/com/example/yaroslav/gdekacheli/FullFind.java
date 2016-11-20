package com.example.yaroslav.gdekacheli;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FullFind extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    ArrayList<String[]> array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullfind);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_sample);
        MapFragment mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_find, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(FullFind.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sample, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }else{
            Toast.makeText(this, "Навигация отключена. Проверьте разрешения приложения.", Toast.LENGTH_SHORT).show();
        }
        Intent intent = getIntent();
        // Logged TODO
        if(intent.getStringExtra("name") != null){
            String name =  intent.getStringExtra("name");
            Toast.makeText(this, "Здравствуй, " + name, Toast.LENGTH_SHORT).show();
        }
        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(FullFind.this, "Ваши координаты: " + latLng.latitude + " И  " + latLng.longitude, Toast.LENGTH_SHORT).show();
            }
        });*/
        if (ContextCompat.checkSelfPermission(FullFind.this, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
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
                new getcords().execute();
            }else{
                Toast.makeText(FullFind.this, "Ваше подключение к сети интернет нестабильно. " +
                        "Попробуйте открыть приложение ещё раз.", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(FullFind.this, "Проверьте ваше подключение к сети интернет.", Toast.LENGTH_SHORT).show();
        }
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng some = marker.getPosition();
                Intent intent = new Intent(FullFind.this, FullView.class);
                int zindex = (int) marker.getZIndex();
                intent.putExtra("info" , array.get(zindex-1)[0]);
                startActivity(intent);
            }
        });
    }

    class getcords extends AsyncTask<ArrayList<String[]>, Void, ArrayList<String[]>> {
        @Override
        protected ArrayList<String[]> doInBackground(ArrayList<String[]>... params) {
            JSONParser parser = new JSONParser();
            try {
                URL oracle = new URL("http://keklol.ru/gdekacheli/getcoords.php");
                URLConnection uc = oracle.openConnection();
                BufferedReader input = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine = input.readLine();
                ArrayList<String[]> arr = new ArrayList<String[]>();
                JSONArray a = (JSONArray) parser.parse(inputLine);
                for (Object o : a) {
                    JSONObject users = (JSONObject) o;
                    String[] mass = {users.get("id").toString(), users.get("title").toString(),
                            users.get("latitude").toString(), users.get("longitude").toString()};
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
            for (int i = 0; i < array.size(); i++) {
                Log.d("checkpoint", "one");
                for (int j = 0; j < array.get(i).length; j++) {
                    Log.d("checkpoint", "two");
                    String[] mass = array.get(i);
                    double latitude = Double.parseDouble(mass[2]);
                    double longitude = Double.parseDouble(mass[3]);
                    LatLng coords = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(coords).title(mass[1]).snippet("Узнать больше(клик)").zIndex(Float.parseFloat(mass[0])));
                }

                Log.d("internet", "turned on");
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_login:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
