package com.example.yaroslav.gdekacheli;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Add extends AppCompatActivity {
    static final int ACTION_TAKE_PHOTO = 0;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    double longitude;
    boolean tokenSuccess;
    double latitude;
    EditText title;
    String titleMarker;
    String mCurrentPhotoPath;
    ImageView photoHolder;
    String descMarker;
    ImageButton image;
    EditText desc;
    RatingBar RB;
    boolean photo = false;
    float rating;
    AddMarker adding = null;
    byte[] b;
    String token = "";
    String name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        title = (EditText)findViewById(R.id.titleMarker);
        desc = (EditText)findViewById(R.id.desc);
        RB = (RatingBar)findViewById(R.id.ratingMarker);
        photoHolder = (ImageView)findViewById(R.id.photoHolder);
        RB.setRating(5);
        image = (ImageButton)findViewById(R.id.takePhoto);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentHome = new Intent(this, FullFind.class);
                startActivity(intentHome);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void attemptLogin(){
        if (adding != null) {
            return;
        }
        title.setError(null);
        desc.setError(null);

        titleMarker = title.getText().toString();
        descMarker = desc.getText().toString();

        boolean cancel = false;

        if (!titleCorrect(titleMarker)){
            title.setError("Название слишком короткое");
            cancel = true;
        }else if(titleMarker.isEmpty()){
            title.setError("Данное поле обязательно к заполнению");
            cancel = true;
        }

        if (!descCorrect(descMarker)){
            desc.setError("Описание слишком короткое");
            cancel = true;
        }else if(descMarker.isEmpty()){
            desc.setError("Данное поле обязательно к заполнению");
            cancel = true;
        }
        try {
            token = TokenHolder.getToken();
            name = TokenHolder.getName();
            latitude = TokenHolder.getLatitude();
            longitude = TokenHolder.getLongitude();
        }catch(NullPointerException e){
            Toast.makeText(this, "Время вашей сессии истекло, зайдите ещё раз", Toast.LENGTH_SHORT).show();
            cancel = true;
        }
        rating = RB.getRating();
        if (!photo){
            cancel = true;
            Toast.makeText(this, "Вы не сделали фотографию!", Toast.LENGTH_SHORT).show();
        }else{
            photoHolder.setDrawingCacheEnabled(true);
            photoHolder.buildDrawingCache();
            Bitmap bm = photoHolder.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            b = stream.toByteArray();
        }

        if(!cancel){
            adding = new AddMarker();
            adding.execute();
        }
    }

    private boolean titleCorrect(String title){
        return title.length() > 6;
    }

    private boolean descCorrect(String title){
        return title.length() > 20;
    }

    public class AddMarker extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String filename = null;
                String link = "http://gdekacheli.ru/sendcoords.php";
                byte data[];
                String myParams = "title="+titleMarker+"&descr="+descMarker+"&longitude="+longitude+"&latitude="+latitude+"&token="+token+"&name="+name
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
            if(tokenSuccess){
                Intent intent = new Intent(Add.this, FullFind.class);
                TokenHolder.setName(name);
                TokenHolder.setToken(token);
                TokenHolder.setLatitude(0);
                TokenHolder.setLongitude(0);
                startActivity(intent);
            }

        }
    }


    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_TAKE_PHOTO && resultCode == RESULT_OK){
            handleBigCameraPhoto();
        }
    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch(actionCode) {
            case ACTION_TAKE_PHOTO:
                File f;
                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    mCurrentPhotoPath = null;
                }
                break;
            default:
                break;
        }
        startActivityForResult(takePictureIntent, actionCode);
    }
    private File setUpPhotoFile() throws IOException {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
    }
    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }
    private String getAlbumName() {
        return getString(R.string.album_name);
    }
    private void handleBigCameraPhoto() {
        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }
    }
    private void setPic() {
            int targetW = photoHolder.getWidth();
            int targetH = photoHolder.getHeight();
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int scaleFactor = 1;
            if ((targetW > 0) || (targetH > 0)) {
                scaleFactor = Math.min(photoW/targetW, photoH/targetH);
            }
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            photoHolder.setImageBitmap(bitmap);
            photo = true;

    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
