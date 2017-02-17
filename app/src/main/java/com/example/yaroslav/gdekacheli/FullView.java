package com.example.yaroslav.gdekacheli;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;

public class FullView extends AppCompatActivity {
    ArrayList<String[]> array;
    String id;
    String title;
    public String titleMaker;
    public String descMaker;
    public double longitude;
    public double latitude;
    Slider slider;
    public float rating;
    ArrayList<Drawable> img;
    public ArrayList<byte[]> byteImg;
    RatingBar RB;
    TextView tv;
    View mFullProgress;
    ViewPager sliderView;
    View mFullViewForm;
    MenuItem itemSync;
    MenuItem itemDel;
    boolean isGlob;
    boolean tokenSuccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_full_view);
        sliderView = (ViewPager)findViewById(R.id.slider);
        img = new ArrayList<>();
        byteImg = new ArrayList<>();
        tv = (TextView) findViewById(R.id.test);
        mFullProgress = findViewById(R.id.full_view_progress);
        mFullViewForm = findViewById(R.id.full_view_form);
        Intent intent = getIntent();
        RB = (RatingBar) findViewById(R.id.ratingBarView);
        id = intent.getStringExtra("info");
        Log.d("id", id);
        title = intent.getStringExtra("name");
        isGlob = intent.getBooleanExtra("glob", true);
        RB.setFocusable(false);
        RB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        if(InfoHolder.getStatus() && isGlob) {
            //вызов асинктаска
            if (ContextCompat.checkSelfPermission(FullView.this, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                Context context = getApplicationContext();
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean isAvailable = false;
                try {
                    if (cm != null) {
                        NetworkInfo ni = cm.getActiveNetworkInfo();
                        if (ni != null) {
                            isAvailable = ni.isConnectedOrConnecting();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (isAvailable) {
                    showProgress(true);
                    new FullView.getItem().execute();
                } else {
                    Toast.makeText(FullView.this, "Ваше подключение к сети интернет нестабильно. " +
                            "Попробуйте открыть приложение ещё раз.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(FullView.this, "Проверьте ваше подключение к сети интернет.", Toast.LENGTH_SHORT).show();
            }
        }else{
            getLocCoords();
        }
        //<вызов асинктаска
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(title);
        Log.d("title2", title);
    }

    public void getLocCoords(){
        MiniSqlHelper db = new MiniSqlHelper(this);
        Cursor cursor = db.getLocCoords(id);
        cursor.moveToFirst();
        descMaker = cursor.getString(cursor.getColumnIndex("DESC"));
        titleMaker = cursor.getString(cursor.getColumnIndex("TITLE"));
        rating = Float.parseFloat(cursor.getString(cursor.getColumnIndex("RATING")));
        latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex("LATITUDE")));
        longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex("LONGITUDE")));
        tv.setText(cursor.getString(cursor.getColumnIndex("DESC")));
        RB.setRating(Float.parseFloat(cursor.getString(cursor.getColumnIndex("RATING"))));
        title = cursor.getString(cursor.getColumnIndex("TITLE"));
        cursor.close();
        cursor = db.getLocImg(id);
        boolean a = cursor.moveToFirst();
        while(a) {
            byte[] arr = cursor.getBlob(cursor.getColumnIndex("IMG"));
            byteImg.add(arr);
            img.add(new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(arr , 0, arr.length)));
            a = cursor.moveToNext();
        }
        slider = new Slider(FullView.this, img);
        sliderView.setAdapter(slider);
        cursor.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full_view, menu);
        itemSync = menu.findItem(R.id.sync);
        itemDel = menu.findItem(R.id.delete);
        if(!isGlob ) {
            itemDel.setVisible(true);
            if(InfoHolder.getStatus()) {
                itemSync.setVisible(true);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentBack = new Intent(this, FullFind.class);
                startActivity(intentBack);
                overridePendingTransition(R.anim.outprev, R.anim.inprev);
                finish();
                return true;
            case R.id.sync:
                if(InfoHolder.getToken() != null){
                    AddMarker addMaker = new AddMarker(titleMaker, descMaker, longitude, latitude, rating);
                    addMaker.execute();
                }else {
                    Toast.makeText(this, "Для совершения данной операции необходимо войти", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.delete:
                if(InfoHolder.getToken() != null && InfoHolder.getStatus() && isGlob) {
                    DelMarker delMaker = new DelMarker(id);
                    delMaker.execute();
                }else if(!isGlob){
                    MiniSqlHelper db = new MiniSqlHelper(this);
                    db.delLocCoords(id);
                    db.delLocImg(id);
                    Intent intent = new Intent(this, FullFind.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.outprev, R.anim.inprev);
                    Log.d("local", "delete");
                    finish();
                }else {
                    Toast.makeText(this, "Для совершения данной операции необходимо войти", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFullViewForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mFullViewForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFullViewForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mFullProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mFullProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFullProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mFullProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mFullViewForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, FullFind.class);
        startActivity(intent);
        overridePendingTransition(R.anim.outprev, R.anim.inprev);
        finish();
    }

    class getItem extends AsyncTask<ArrayList<String[]>, Void, ArrayList<String[]>> {
        @Override
        protected ArrayList<String[]> doInBackground(ArrayList<String[]>... params) {
            JSONParser parser = new JSONParser();
            try {
                URL oracle = new URL("http://gdekacheli.ru/getcoords.php?id="+id);
                URLConnection uc = oracle.openConnection();
                BufferedReader input = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine = input.readLine();
                ArrayList<String[]> arr = new ArrayList<>();
                JSONArray a = (JSONArray) parser.parse(inputLine);
                for (Object o : a) {
                    JSONObject users = (JSONObject) o;
                    String[] mass = {users.get("id").toString(), users.get("title").toString(), users.get("descr").toString(), users.get("rating").toString(), users.get("owner").toString()};
                    arr.add(mass);
                    array = arr;
                }
                input.close();
                URL oraclePhoto = new URL("http://gdekacheli.ru/getphoto.php?id="+arr.get(0)[0]);
                URLConnection ucPhoto = oraclePhoto.openConnection();
                BufferedReader inputPhoto = new BufferedReader(new InputStreamReader(ucPhoto.getInputStream()));
                String inputLinePhoto = inputPhoto.readLine();
                JSONArray b = (JSONArray) parser.parse(inputLinePhoto);
                for(Object q : b){
                    JSONObject photo = (JSONObject) q;
                    String link = photo.get("filename").toString();
                    InputStream is = (InputStream) new URL("http://gdekacheli.ru/"+link).getContent();
                    Drawable test = Drawable.createFromStream(is, "123");
                    img.add(test);
                    is.close();
                }
                inputPhoto.close();
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
            showProgress(false);
            try {
                tv.setText(array.get(0)[2]);
                slider = new Slider(FullView.this, img);
                sliderView.setAdapter(slider);
                RB.setRating(Float.parseFloat(array.get(0)[3]));
                String owner = array.get(0)[4];
                if(InfoHolder.getName() != null){
                    if(InfoHolder.getName().equals(owner)) {
                        itemDel.setVisible(true);
                    }
                }
            }catch(NullPointerException e){
                Toast.makeText(getApplicationContext(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
            }

        }
    }


    public class AddMarker extends AsyncTask<Void, Void, Boolean> {
        private String titleMaker;
        private String descMaker;
        private double longitude;
        private double latitude;
        private float rating;
        private String name = InfoHolder.getName();
        private String token = InfoHolder.getToken();
        AddMarker(String titleMarker, String descMarker, double longitude, double latitude, float rating){
            this.titleMaker = titleMarker;
            this.descMaker = descMarker;
            this.longitude = longitude;
            this.latitude = latitude;
            this.rating = rating;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String filename = null;
                String link = "http://gdekacheli.ru/sendcoords.php";
                byte data[];
                String myParams = "title="+titleMaker+"&descr="+descMaker+"&longitude="+longitude+"&latitude="+latitude+"&token="+token+"&name="+name
                        +"&rating="+rating+"&num="+byteImg.size();
                InputStream is;
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Length", "" + Integer.toString(myParams.getBytes().length));
                OutputStream os = conn.getOutputStream();
                data = myParams.getBytes("UTF-8");
                os.write(data);
                conn.connect();
                is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while((line = br.readLine()) != null) {
                    Log.d("output text", line);
                    if (line.equals("false")) {
                        break;
                    } else if(line.equals("token")){
                        token = br.readLine();
                        if (!token.isEmpty()){
                            tokenSuccess = true;
                        }
                    } else if(line.equals("filename")){
                        filename = br.readLine();
                        Log.d("interesting", filename);
                    }
                }
                if(filename != null) {
                    for (int i = 0; i < byteImg.size(); i++) {
                        InputStream is2;
                        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://gdekacheli.ru/file.php?filename=" + filename + i).openConnection();
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setRequestMethod("POST");
                        OutputStream os2 = httpURLConnection.getOutputStream();
                        os2.write(byteImg.get(i));
                        httpURLConnection.connect();
                        is2 = httpURLConnection.getInputStream();
                        BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
                        String line2;
                        while ((line2 = br2.readLine()) != null) {
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            showProgress(false);
            if(tokenSuccess){
                InfoHolder.setName(name);
                InfoHolder.setToken(token);
                InfoHolder.setLatitude(0);
                InfoHolder.setLongitude(0);
                MiniSqlHelper db = new MiniSqlHelper(FullView.this);
                db.delLocCoords(id);
                db.delLocImg(id);
                Intent intent = new Intent(FullView.this, FullFind.class);
                startActivity(intent);
                overridePendingTransition(R.anim.outprev, R.anim.inprev);
                finish();
            }

        }
    }
    public class DelMarker extends AsyncTask<Void, Void, Boolean> {
        private String id;
        private String name = InfoHolder.getName();
        private String token = InfoHolder.getToken();
        DelMarker(String id){
            this.id = id;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String link = "http://gdekacheli.ru/delcoords.php";
                byte data[];
                String myParams = "id="+id+"&name="+name+"&token="+token;
                InputStream is;
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Length", "" + Integer.toString(myParams.getBytes().length));
                OutputStream os = conn.getOutputStream();
                data = myParams.getBytes("UTF-8");
                os.write(data);
                conn.connect();
                is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while((line = br.readLine()) != null) {
                    Log.d("output text", line);
                    if (line.equals("false")) {
                        break;
                    } else if(line.equals("token")){
                        token = br.readLine();
                        if (!token.isEmpty()){
                            tokenSuccess = true;
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            showProgress(false);
            if(tokenSuccess){
                InfoHolder.setName(name);
                InfoHolder.setToken(token);
                InfoHolder.setLatitude(0);
                InfoHolder.setLongitude(0);
                Intent intent = new Intent(FullView.this, FullFind.class);
                startActivity(intent);
                overridePendingTransition(R.anim.outprev, R.anim.inprev);
                finish();
            }
        }
    }

}
