package com.example.carspotter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnWiki;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnWiki = (Button) findViewById(R.id.btnWiki);
    }

    public void onBtnWiki_Clicked(View caller){
        Intent intent =new Intent(this, BrandSelectActivity.class);
        startActivity(intent);
    }
}