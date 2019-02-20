package com.example.gilr1_17.walletwatcher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onAddRecordButtonClicked(View button)
    {
        startActivity(new Intent(MainActivity.this, AddRecord.class));
    }
    public void onViewRecordsButtonClicked(View button)
    {
        startActivity(new Intent(MainActivity.this, ViewRecords.class));
    }
}