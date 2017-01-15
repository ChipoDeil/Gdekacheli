package com.example.yaroslav.gdekacheli;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Welcome extends AppCompatActivity {

    Button dummy;
    MenuItem itemOnline;
    MenuItem itemOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_welcome);
        dummy = (Button)findViewById(R.id.dummy_button);
        dummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Welcome.this, FullFind.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        itemOnline = menu.findItem(R.id.online_status);
        itemOffline = menu.findItem(R.id.offline_status);
        if(InfoHolder.getStatus()){
            itemOnline.setVisible(true);
            itemOffline.setVisible(false);
        }else{
            itemOnline.setVisible(false);
            itemOffline.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(InfoHolder.getStatus()){
            InfoHolder.setStatus(false);
            itemOnline.setVisible(false);
            itemOffline.setVisible(true);
        }else{
            InfoHolder.setStatus(true);
            itemOnline.setVisible(true);
            itemOffline.setVisible(false);
        }
        return super.onOptionsItemSelected(item);
    }
}
