package com.example.yaroslav.gdekacheli;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

public class Add extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    EditText title;
    String titleMarker;
    ImageView photoHolder;
    String decsMarker;
    ImageButton image;
    EditText desc;
    RatingBar RB;
    float rating;
    addMarkerAsync adding = null;
    String token = "";
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
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photoHolder.setImageBitmap(imageBitmap);
        }
    }

    public void attemptLogin(){
        if (adding != null) {
            return;
        }
        title.setError(null);
        desc.setError(null);

        titleMarker = title.getText().toString();
        decsMarker = desc.getText().toString();

        boolean cancel = false;

        if (!titleCorrect(titleMarker)){
            title.setError("Название слишком короткое");
            cancel = true;
        }else if(titleMarker.isEmpty()){
            title.setError("Данное поле обязательно к заполнению");
            cancel = true;
        }

        if (!descCorrect(decsMarker)){
            desc.setError("Описание слишком короткое");
            cancel = true;
        }else if(decsMarker.isEmpty()){
            desc.setError("Данное поле обязательно к заполнению");
            cancel = true;
        }
        Intent intent = getIntent();
        try {
            token = intent.getStringExtra("token");
        }catch(NullPointerException e){
            Toast.makeText(this, "Время вашей сессии истекло, зайдите ещё раз", Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        if (cancel){

        }else{
            //TODO ASYNC EXECUTE
        }
    }

    private boolean titleCorrect(String title){
        return title.length() > 6;
    }

    private boolean descCorrect(String title){
        return title.length() > 20;
    }

    class addMarkerAsync{
        //TODO adding
    }

}
