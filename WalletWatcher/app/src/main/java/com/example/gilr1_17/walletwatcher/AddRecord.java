package com.example.gilr1_17.walletwatcher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddRecord extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
    }

    public void obBtnCameraClicked(View button)
    {
        startActivity(new Intent(AddRecord.this, MainActivity.class));
    }
}