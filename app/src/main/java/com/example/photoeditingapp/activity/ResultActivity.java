package com.example.photoeditingapp.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.photoeditingapp.R;
import com.example.photoeditingapp.entity.SharedImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


// Vu Xuan Hoang 21110770
public class ResultActivity extends AppCompatActivity {

    FloatingActionButton uploadButton;
    ImageView uploadImage;
    EditText uploadCaption;
    ProgressBar progressBar;
    private Uri imageUri;

    final private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("SharedImages");
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        uploadButton = findViewById(R.id.upload_button);
        uploadImage = findViewById(R.id.uploaded_image);
        uploadCaption = findViewById(R.id.upload_caption);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(ResultActivity.this, "Please select an image", Toast.LENGTH_LONG).show();
                }
            }
        });

        ImageButton save_image_btn = findViewById(R.id.save_image);
        // If image received from Photo editor, it have been saved already
        if (getIntent().getData() != null) {
            uploadImage.setImageURI(getIntent().getData());
            imageUri = getIntent().getData();

            save_image_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Image have saved to your gallery", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // If image received from camera, save it to photo gallery
            Bitmap photo = getIntent().getParcelableExtra("captured_photo");
            uploadImage.setImageBitmap(photo);
            imageUri = bitmapToUri(photo);

            save_image_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveImageToGallery(photo);
                }
            });
        }

        ImageButton back_btn = findViewById(R.id.back_button);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public Uri bitmapToUri(Bitmap bitmap) {
        // Get the context
        Context context = getApplicationContext();

        // Create a file to save the bitmap
        File bitmapFile = new File(context.getCacheDir(), "image.jpg");

        try {
            // Write the bitmap to the file
            FileOutputStream fos = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // Create a Uri from the file path
            return Uri.fromFile(bitmapFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Process to save image to photo gallery of the phone
    private void saveImageToGallery(Bitmap imageBitmap) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());

        String fileName = "IMG_" + timeStamp + ".jpg";
        File imageFile = new File(storageDir, fileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imageFile));
            sendBroadcast(mediaScanIntent);

            Toast.makeText(this, "Image saved Successfully", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Upload image to firebase
    private void uploadToFirebase(Uri imageUri) {
        String cation = uploadCaption.getText().toString();
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

        imageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        SharedImage data = new SharedImage(uri.toString(), cation);
                        String key = databaseReference.push().getKey();
                        databaseReference.child(key).setValue(data);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(ResultActivity.this, "Image uploaded successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ResultActivity.this, PhotoGalleryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ResultActivity.this, "Fail to share image", Toast.LENGTH_LONG).show();
            }
        });
    }

    // get extension of selected file
    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}