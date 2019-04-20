package com.example.gilr1_17.walletwatcher;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class ViewRecords extends AppCompatActivity {

    private static final String TAG = "ViewRecords";

    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_records);

        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    // https://stackoverflow.com/questions/35648913/how-to-set-menu-to-toolbar-in-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    private void updateUI(GoogleSignInAccount account) {
        // https://stackoverflow.com/questions/44491418/can-not-resolve-updateui-firebase

        if (account != null)
        {
            db.collection("records")
                    .whereEqualTo("ownerID", account.getId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int cardMargin = 0;
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());

                                    //_rtnText1.append(document.getData().toString() + "\n\n");

                                    //Log.d(TAG,"String value: " + document.getString("name"));

                                    RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
                                    CardView cardView = new CardView(ViewRecords.this);
                                    cardView.setLayoutParams(new CardView.LayoutParams(
                                            CardView.LayoutParams.MATCH_PARENT,
                                            400));

                                    // https://stackoverflow.com/questions/44223471/setting-margin-programmatically-to-cardview
                                    ViewGroup.MarginLayoutParams cardViewParams = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
                                    cardViewParams.setMargins(30, cardMargin, 30, 30);

                                    relativeLayout.addView(cardView);

                                    TextView name = new TextView(ViewRecords.this);
                                    name.setText("Name: " + document.getString("name"));
                                    //name.setLayoutParams(params);
                                    cardView.addView(name);

                                    TextView category = new TextView(ViewRecords.this);
                                    category.setText("Category: " + document.getString("category"));
                                    params.setMargins(0,50,0,0);
                                    category.setLayoutParams(params);
                                    cardView.addView(category);

                                    TextView cost = new TextView(ViewRecords.this);
                                    cost.setText("Cost: " + document.getString("cost"));
                                    params.setMargins(0,100,0,0);
                                    cost.setLayoutParams(params);
                                    cardView.addView(cost);

                                    TextView description = new TextView(ViewRecords.this);
                                    description.setText("Description: " + document.getString("description"));
                                    params.setMargins(0,150,0,0);
                                    description.setLayoutParams(params);
                                    cardView.addView(description);

                                    cardMargin += 450;
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else
        {
        }
    }
}