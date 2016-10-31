package com.example.yaroslav.gdekacheli;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Find extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    ArrayList<String[]> array = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);

        }else{
            Toast.makeText(this, "Навигация отключена. Проверьте разрешения приложения.", Toast.LENGTH_SHORT).show();
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(Find.this, "Ваши координаты: " + latLng.latitude + " И  " + latLng.longitude, Toast.LENGTH_SHORT).show();
            }
        });
        new getcords().execute();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng some = marker.getPosition();
                Toast.makeText(Find.this, "" + some.latitude, Toast.LENGTH_SHORT).show();
                //переход на активность с доп информацией
                Toast.makeText(Find.this, "" + marker.getZIndex(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    class getcords extends AsyncTask<ArrayList<String[]>, Void, ArrayList<String[]>>{
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
                for (Object o : a){
                    JSONObject users = (JSONObject) o;
                    String[] mass = {users.get("id").toString(), users.get("title").toString(), users.get("desc").toString(), users.get("img").toString(),
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
            }
        }
        @Override
        protected void onPostExecute(ArrayList<String[]> strings) {
            super.onPostExecute(strings);
            if (ContextCompat.checkSelfPermission(Find.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
                for (int i = 0; i < array.size(); i++){
                    Log.d("checkpoint", "one");
                    for (int j = 0; j < array.get(i).length; j++){
                        Log.d("checkpoint", "two");
                        String[] mass = array.get(i);
                        double latitude = Double.parseDouble(mass[4]);
                        double longitude = Double.parseDouble(mass[5]);
                        LatLng coords = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(coords).title(mass[1]).snippet("Узнать больше(клик)").zIndex(Float.parseFloat(mass[0])));
                    }
                }
                Log.d("internet", "turned on");
            }else{
                Toast.makeText(Find.this, "Проверьте ваше подключение к сети интернет.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
