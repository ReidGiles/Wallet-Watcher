package com.example.gilr1_17.walletwatcher;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        avatar = findViewById(R.id.imgAvatar);

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
    }

    public void onClick(View view)
    {
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);;
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account) {
        // https://stackoverflow.com/questions/44491418/can-not-resolve-updateui-firebase

        if (account != null)
        {
            Toast.makeText(getApplicationContext(),"Signed in",Toast.LENGTH_SHORT).show();
            TextView name = this.findViewById(R.id.txtName);
            name.setText(account.getGivenName());
            String photo = account.getPhotoUrl().toString();

            Glide.with(getApplicationContext()).load(photo).into(avatar);
        }
        else
        {
            //startActivity(new Intent(MainActivity.this, LogIn.class));
            Toast toast=Toast.makeText(getApplicationContext(),"Viewing records unavailable offline",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void onAddRecordButtonClicked(View button)
    {
        startActivity(new Intent(MainActivity.this, AddRecord.class));
    }
    public void onViewRecordsButtonClicked(View button)
    {
        startActivity(new Intent(MainActivity.this, ViewRecords.class));
    }

    public void onSignInButtonClicked()
    {
        startActivity(new Intent(MainActivity.this, LogIn.class));
    }
}