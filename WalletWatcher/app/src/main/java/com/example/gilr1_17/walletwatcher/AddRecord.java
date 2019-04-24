package com.example.gilr1_17.walletwatcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AddRecord extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;
    GoogleSignInClient mGoogleSignInClient;
    private ImageView _imageView;
    private EditText _name;
    private EditText _category;
    private EditText _cost;
    private EditText _description;
    private boolean photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        _imageView = this.findViewById(R.id.imgCamera);
        _name = this.findViewById(R.id.TransactionNameInput);
        _category = this.findViewById(R.id.TransactionCategoryInput);
        _cost = this.findViewById(R.id.TransactionCostInput);
        _description = this.findViewById(R.id.TransactionDescriptionInput);
        photo = false;

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
        String receipt;

        final Map<String, Object> recordData = new HashMap<>();
        recordData.put("name", _name.getText().toString());
        recordData.put("category", _category.getText().toString());
        recordData.put("cost", _cost.getText().toString());
        recordData.put("description", _description.getText().toString());
        recordData.put("ownerID", account.getId());

        // Create a reference to 'images/receipt.jpg'
        String fileName = java.util.UUID.randomUUID().toString();
        final StorageReference receiptImagesRef = storageRef.child("images/receipts/" + fileName + ".jpg");

        // If photo has been taken, upload data with photo
        if (photo) {
            // https://firebase.google.com/docs/storage/android/upload-files
            // Get the data from an ImageView as bytes
            _imageView.setDrawingCacheEnabled(true);
            _imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) _imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = receiptImagesRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d("AddRecord", "Image failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("AddRecord", "Image success");
                    receiptImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            Log.d("AddRecord", "URI success");
                            recordData.put("receipt", downloadUrl.toString());
                            db.collection("records").add(recordData);
                        }
                    });
                }
            });
        }
        // Else upload data without photo
        else db.collection("records").add(recordData);
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
            photo = true;
        }
    }
}