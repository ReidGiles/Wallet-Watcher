package com.example.gilr1_17.walletwatcher;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ViewRecords extends AppCompatActivity {

    private static final String TAG = "ViewRecords";

    FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    RelativeLayout relativeLayout;
    List<QueryDocumentSnapshot> activeDocuments;
    List<CardView> activeCards;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_records);

        db = FirebaseFirestore.getInstance();
        relativeLayout = findViewById(R.id.relativeLayout);
        activeDocuments = new ArrayList<QueryDocumentSnapshot>();
        activeCards = new ArrayList<CardView>();
        id = 0;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Toolbar toolBar = findViewById(R.id.toolbar);
        toolBar.setTitle("View Records");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    View.OnClickListener cardButtons = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (CardView cardView : activeCards)
            {
                if (cardView.getId() == view.getId())
                {
                    cardView.setBackgroundColor(Color.RED);
                    for (QueryDocumentSnapshot d : activeDocuments)
                    {
                        if (d.getId() == cardView.getContentDescription())
                        {
                            deleteRecord("records", d.getId());
                        }
                    }
                    updateUI();
                }
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI();
    }

    // https://stackoverflow.com/questions/35648913/how-to-set-menu-to-toolbar-in-android
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateUI() {
        // https://stackoverflow.com/questions/44491418/can-not-resolve-updateui-firebase

        if (account != null)
        {
            populate();
        }
        else
        {
        }
    }

    private void deleteRecord(String collection, String document)
    {
        db.collection(collection).document(document)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Document deleted",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Error deleting document",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populate()
    {
        db.collection("records")
                .whereEqualTo("ownerID", account.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int cardMargin = 0;
                            relativeLayout.removeAllViews();
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                boolean duplicate = false;

                                CardView cardView = new CardView(ViewRecords.this);
                                cardView.setLayoutParams(new CardView.LayoutParams(
                                        CardView.LayoutParams.MATCH_PARENT,
                                        400));
                                cardView.setId(id + 1);

                                // https://stackoverflow.com/questions/44223471/setting-margin-programmatically-to-cardview
                                ViewGroup.MarginLayoutParams cardViewParams = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
                                cardViewParams.setMargins(30, cardMargin, 30, 30);

                                relativeLayout.addView(cardView);
                                cardView.setContentDescription(document.getId());
                                activeCards.add(cardView);

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

                                Button delete = new Button(ViewRecords.this);
                                delete.setText("Delete");
                                params.setMargins(700,0,0,0);
                                delete.setLayoutParams(params);
                                delete.setOnClickListener(cardButtons);
                                cardView.addView(delete);

                                for (QueryDocumentSnapshot d : activeDocuments)
                                    if (d == document)
                                    {
                                        duplicate = true;
                                        delete.setId( (activeDocuments.indexOf(d)) + 1 );
                                    }
                                if (!duplicate)
                                {
                                    activeDocuments.add(document);
                                    delete.setId(id + 1);
                                }

                                cardMargin += 450;
                                id += 1;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}