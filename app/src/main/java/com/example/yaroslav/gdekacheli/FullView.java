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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class FullView extends AppCompatActivity {
    ArrayList<String[]> array;
    String id;
    String title;
    public String titleMaker;
    public String descMaker;
    public double longitude;
    public double latitude;
    public float rating;
    byte[] b;
    Drawable img = null;
    RatingBar RB;
    ImageView iv;
    TextView tv;
    View mFullProgress;
    View mFullViewForm;
    boolean locCoords = false;
    boolean isGlob;
    boolean tokenSuccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_full_view);
        tv = (TextView) findViewById(R.id.test);
        iv = (ImageView)findViewById(R.id.imageView);
        mFullProgress = findViewById(R.id.full_view_progress);
        mFullViewForm = findViewById(R.id.full_view_form);
        Intent intent = getIntent();
        RB = (RatingBar) findViewById(R.id.ratingBarView);
        id = intent.getStringExtra("info");
        Log.d("id", id);
        title = intent.getStringExtra("name");
        isGlob = intent.getBooleanExtra("glob", true);
        Log.d("WTF1", isGlob+"");
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
            locCoords = true;
            MiniSqlHelper db = new MiniSqlHelper(this);
            Cursor cursor = db.getLocCoords(id);
            cursor.moveToFirst();
            descMaker = cursor.getString(cursor.getColumnIndex("DESC"));
            titleMaker = cursor.getString(cursor.getColumnIndex("TITLE"));
            rating = Float.parseFloat(cursor.getString(cursor.getColumnIndex("RATING")));
            b = cursor.getBlob(cursor.getColumnIndex("IMG"));
            latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex("LATITUDE")));
            longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex("LONGITUDE")));
            tv.setText(cursor.getString(cursor.getColumnIndex("DESC")));
            iv.setImageDrawable(new BitmapDrawable(getResources(),BitmapFactory.decodeByteArray(cursor.getBlob(cursor.getColumnIndex("IMG")), 0, cursor.getBlob(cursor.getColumnIndex("IMG")).length)));
            RB.setRating(Float.parseFloat(cursor.getString(cursor.getColumnIndex("RATING"))));
            title = cursor.getString(cursor.getColumnIndex("TITLE"));
        }
        //<вызов асинктаска
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(title);
        Log.d("title2", title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(locCoords) {
            getMenuInflater().inflate(R.menu.menu_full_view, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentBack = new Intent(this, FullFind.class);
                startActivity(intentBack);
                return true;
            case R.id.sync:
                if(InfoHolder.getToken() != null){
                    AddMarker addMaker = new AddMarker(titleMaker, descMaker, longitude, latitude, rating);
                    addMaker.execute();
                }else{
                    Toast.makeText(this, "Для совершения данной операции необходимо войти", Toast.LENGTH_SHORT).show();
                }
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
                    String[] mass = {users.get("id").toString(), users.get("title").toString(), users.get("descr").toString(), users.get("img").toString(), users.get("rating").toString()};
                    if (!(mass[3]).isEmpty()){
                        //Достаем картинку
                        InputStream is = (InputStream) new URL("http://gdekacheli.ru/"+mass[3]).getContent();
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
            showProgress(false);
            try {
                tv.setText(array.get(0)[2]);
                iv.setImageDrawable(img);
                RB.setRating(Float.parseFloat(array.get(0)[4]));
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
                        +"&rating="+rating;
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
                    //TODO switch
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
                    InputStream is2;
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://gdekacheli.ru/file.php?filename=" + filename).openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("POST");
                    OutputStream os2 = httpURLConnection.getOutputStream();
                    os2.write(b);
                    httpURLConnection.connect();
                    is2 = httpURLConnection.getInputStream();
                    BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
                    String line2;
                    while ((line2 = br2.readLine()) != null) {
                        //TODO reading lines
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
                MiniSqlHelper db = new MiniSqlHelper(FullView.this);
                db.delLocCoords(id);
                Intent intent = new Intent(FullView.this, FullFind.class);
                InfoHolder.setName(name);
                InfoHolder.setToken(token);
                InfoHolder.setLatitude(0);
                InfoHolder.setLongitude(0);
                startActivity(intent);
            }

        }
    }

}
