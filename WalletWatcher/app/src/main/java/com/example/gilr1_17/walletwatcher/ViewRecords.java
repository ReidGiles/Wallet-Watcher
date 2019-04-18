package com.example.gilr1_17.walletwatcher;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
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
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
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
                                int margin = 200;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, document.getId() + " => " + document.getData());

                                    //_rtnText1.append(document.getData().toString() + "\n\n");

                                    //Log.d(TAG,"String value: " + document.getString("name"));

                                    RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
                                    CardView cardView = new CardView(ViewRecords.this);
                                    cardView.setLayoutParams(new CardView.LayoutParams(
                                            CardView.LayoutParams.MATCH_PARENT,
                                            100));
                                    relativeLayout.addView(cardView);

                                    // https://stackoverflow.com/questions/44223471/setting-margin-programmatically-to-cardview
                                    ViewGroup.MarginLayoutParams cardViewParams = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
                                    cardViewParams.setMargins(30, margin, 30, 30);
                                    cardView.requestLayout();
                                    margin += 150;
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