package com.example.gilr1_17.walletwatcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddRecord extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;
    ImageView _imageView;
    private EditText _name;
    private EditText _category;
    private EditText _cost;
    private EditText _description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        _imageView = this.findViewById(R.id.imgCamera);
        _name = this.findViewById(R.id.TransactionNameInput);
        _category = this.findViewById(R.id.TransactionCategoryInput);
        _cost = this.findViewById(R.id.TransactionCostInput);
        _description = this.findViewById(R.id.TransactionDescriptionInput);

        db = FirebaseFirestore.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void onBtnSubmitClicked(View button)
    {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        Map<String, Object> recordData = new HashMap<>();
        recordData.put("name", _name.getText().toString());
        recordData.put("category", _category.getText().toString());
        recordData.put("cost", _cost.getText().toString());
        recordData.put("description", _description.getText().toString());
        recordData.put("ownerID", account.getId());

        //if (_name.getText().toString() != "" && _category.getText().toString() != "" && _cost.getText().toString() != "")

        db.collection("records").add(recordData);
    }

    public void obBtnCameraClicked(View button)
    {
        dispatchTakePictureIntent();
        //startActivity(new Intent(AddRecord.this, MainActivity.class));
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            _imageView.setImageBitmap(imageBitmap);
        }
    }
}