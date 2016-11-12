package com.example.yaroslav.gdekacheli;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class FullView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);
        TextView tv = (TextView) findViewById(R.id.test);
        Intent intent = getIntent();
        String[] array = intent.getStringArrayExtra("info");
        tv.setText(array[1]);

    }
}
