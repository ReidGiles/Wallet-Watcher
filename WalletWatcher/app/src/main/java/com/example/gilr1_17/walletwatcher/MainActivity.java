package com.example.gilr1_17.walletwatcher;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    private static final String TAG = "MainActivity";
    private ImageView avatar;
    private TextView name;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        avatar = findViewById(R.id.imgAvatar);
        name = this.findViewById(R.id.txtName);
        title = this.findViewById(R.id.txtWelcomeBack);

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
    }

    public void onClick(View view)
    {
    }

    // https://stackoverflow.com/questions/35648913/how-to-set-menu-to-toolbar-in-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    /**
     * Defines options menu selection response
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Open settings activity
                startActivity(new Intent(MainActivity.this, Settings.class));

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    /**
     * Updates UI based on account status
     * @param account current google sign in account
     */
    private void updateUI(GoogleSignInAccount account) {
        // https://stackoverflow.com/questions/44491418/can-not-resolve-updateui-firebase

        if (account != null)
        {
            Toast.makeText(getApplicationContext(),"Signed in",Toast.LENGTH_SHORT).show();
            title.setText("Welcome back,");
            name.setText(account.getGivenName());
            if (account.getPhotoUrl() != null)
            {
                String photo = account.getPhotoUrl().toString();
                Glide.with(getApplicationContext()).load(photo).into(avatar);
            }
            else
            {
                String photo = "sample/avatars[11]";
                Glide.with(getApplicationContext()).load(photo).into(avatar);
            }
        }
        else
        {
            //startActivity(new Intent(MainActivity.this, LogIn.class));
            title.setText("Offline mode");
            name.setText("");
            String photo = "sample/avatars[11]";
            Glide.with(getApplicationContext()).load(photo).into(avatar);
        }
    }

    /**
     * Opens the AddRecord activity
     * @param button
     */
    public void onAddRecordButtonClicked(View button)
    {
        startActivity(new Intent(MainActivity.this, AddRecord.class));
    }

    /**
     * Opens the ViewRecords activity
     * @param button
     */
    public void onViewRecordsButtonClicked(View button)
    {
        startActivity(new Intent(MainActivity.this, ViewRecords.class));
    }
}