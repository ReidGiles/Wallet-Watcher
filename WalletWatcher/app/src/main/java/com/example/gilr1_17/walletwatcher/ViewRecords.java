package com.example.gilr1_17.walletwatcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ViewRecords extends AppCompatActivity {

    private static final String TAG = "ViewRecords";

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    RelativeLayout relativeLayout;
    List<QueryDocumentSnapshot> activeDocuments;
    List<CardView> activeCards;
    List<Bitmap> activeImages;
    int id;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_records);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        db = FirebaseFirestore.getInstance();
        relativeLayout = findViewById(R.id.relativeLayout);
        activeDocuments = new ArrayList<QueryDocumentSnapshot>();
        activeCards = new ArrayList<CardView>();
        activeImages = new ArrayList<Bitmap>();
        id = 0;
        imageView = new ImageView(ViewRecords.this);

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

    View.OnClickListener deleteButtons = new View.OnClickListener()
    {
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

    View.OnClickListener expandButtons = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            for (Bitmap b : activeImages)
            {

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
        startActivity(new Intent(ViewRecords.this, MainActivity.class));
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

                                ImageButton delete = new ImageButton(ViewRecords.this);
                                delete.setImageResource(R.drawable.baseline_delete_forever_black_18dp);
                                params.setMargins(850,0,0,0);
                                delete.setLayoutParams(params);
                                delete.setOnClickListener(deleteButtons);
                                cardView.addView(delete);

                                if (document.getString("receipt") != null)
                                {
                                    displayImage(cardView, document);

                                    ImageButton viewReceipt = new ImageButton(ViewRecords.this);
                                    viewReceipt.setImageResource(R.drawable.baseline_open_in_new_black_18dp);
                                    params.setMargins(850,150,0,0);
                                    viewReceipt.setLayoutParams(params);
                                    viewReceipt.setOnClickListener(expandButtons);
                                    cardView.addView(viewReceipt);
                                }

                                for (QueryDocumentSnapshot d : activeDocuments)
                                {
                                    if (d == document) {
                                        duplicate = true;
                                        delete.setId((activeDocuments.indexOf(d)) + 1);
                                    }
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

    private void displayImage(final CardView cardView, QueryDocumentSnapshot document)
    {
        StorageReference receiptRef = storage.getReferenceFromUrl(document.getString("receipt"));

        final long ONE_MEGABYTE = 1024 * 1024;
        receiptRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Log.d(TAG, "Image success");
                imageView = new ImageView(ViewRecords.this);
                //https://stackoverflow.com/questions/3545493/display-byte-to-imageview-in-android#
                //---------------------------------------------------------------------------------
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                activeImages.add(bitmap);
                imageView.setImageBitmap(bitmap);
                //---------------------------------------------------------------------------------
                cardView.addView(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, "Image failure");
            }
        });
    }
}