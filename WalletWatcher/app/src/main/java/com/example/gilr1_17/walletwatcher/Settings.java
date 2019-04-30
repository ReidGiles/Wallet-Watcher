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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 2;
    private static final String TAG = "Settiings";

    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;
    Button _accountName;
    Button _logInOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        _accountName = findViewById(R.id.btnAccountName);
        _logInOut = findViewById(R.id.btnLogInOut);

        this.findViewById(R.id.btnLogInOut).setOnClickListener(this);

        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Toolbar toolBar = findViewById(R.id.toolbar);
        toolBar.setTitle("Settings");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    /**
     * Updates UI based on current account
     * @param account current signed in account
     */
    private void updateUI(GoogleSignInAccount account) {
        // https://stackoverflow.com/questions/44491418/can-not-resolve-updateui-firebase

        if (account != null)
        {
            _accountName.setText("   " + account.getEmail());
            _logInOut.setText("   LOG OUT");
        }
        else
        {
            _accountName.setText("   Offline mode active");
            _logInOut.setText("   LOG IN");
        }
    }

    public void onClick(View view)
    {
        if (view.getId() == R.id.btnLogInOut)
        {
            onLogInOutButtonClicked();
        }
    }
    //https://stackoverflow.com/questions/26651602/display-back-arrow-on-toolbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // https://stackoverflow.com/questions/35648913/how-to-set-menu-to-toolbar-in-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Open settings activity
                startActivity(new Intent(Settings.this, Settings.class));

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void onLogInOutButtonClicked()
    {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)
        {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });
        }
        else
        {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

        account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Create a new user with a first, middle, and last name
            Map<String, Object> user = new HashMap<>();
            user.put("first", account.getGivenName());
            user.put("last", account.getFamilyName());
            user.put("email", account.getEmail());
            user.put("ownerID", account.getId());

            db.collection("users").document(account.getId()).set(user);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
}
