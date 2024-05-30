package com.example.photoeditingapp.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
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

// Vu Xuan Hoang 21110770
public class PhotoEditorActivity extends AppCompatActivity {
    int IMAGE_REQUEST_CODE = 100;
    int PHOTO_EDITOR = 200;
    int OPEN_CAMERA = 300;
    int UPLOAD_IMAGE = 400;
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
        setContentView(R.layout.activity_photo_editor);

        ImageButton edit_image_btn = findViewById(R.id.edit_image_btn);
        edit_image_btn.setOnClickListener(new View.OnClickListener() {

            // Call and open image gallery from the phone and pick up
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });

        // open camera to capture
        ImageButton camera_btn = findViewById(R.id.camera_btn);
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent_open_camera, OPEN_CAMERA);
            }
        });

        uploadButton = findViewById(R.id.upload_button);
        uploadImage = findViewById(R.id.uploaded_image);
        uploadCaption = findViewById(R.id.upload_caption);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, UPLOAD_IMAGE);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(PhotoEditorActivity.this, "Please select an image", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // function upload selected image to firebase also it added to photo shared pool
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
                        Toast.makeText(PhotoEditorActivity.this, "Image uploaded successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PhotoEditorActivity.this, PhotoGalleryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                // progress bar will show while image uploading

                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(PhotoEditorActivity.this, "Fail to share image", Toast.LENGTH_LONG).show();
            }
        });
    }

    // get extension of selected file
    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    // Capture response when I chosen picture from phone gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If a picture picked it will send that picture to DsPhotoEditor
        // DsPhotoEditor is a simple library for edit photo
        // Because of out-dated library and it not get supported anymore so, it only some features can use
        if(requestCode == IMAGE_REQUEST_CODE && resultCode != RESULT_CANCELED){
            if(data.getData() != null){
                Uri imageUri = data.getData();

                Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
                dsPhotoEditorIntent.setData(imageUri);

                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "PhotoEditor");

                // Hide tools not supported anymore and cause crash if use it
                int[] toolsToHide = {
                        DsPhotoEditorActivity.TOOL_ORIENTATION,
                        DsPhotoEditorActivity.TOOL_FILTER,
                        DsPhotoEditorActivity.TOOL_PIXELATE,
                        DsPhotoEditorActivity.TOOL_WARMTH,
                        DsPhotoEditorActivity.TOOL_SHARPNESS,
                        DsPhotoEditorActivity.TOOL_SATURATION,
                        DsPhotoEditorActivity.TOOL_VIGNETTE,
                        DsPhotoEditorActivity.TOOL_CONTRAST,
                        DsPhotoEditorActivity.TOOL_ROUND,
                        DsPhotoEditorActivity.TOOL_EXPOSURE,
                        DsPhotoEditorActivity.TOOL_FRAME
                };
                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
                startActivityForResult(dsPhotoEditorIntent, PHOTO_EDITOR);
            }
        }

        // Send edited picture to result activity for review
        if(requestCode == PHOTO_EDITOR && resultCode != RESULT_CANCELED) {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.setData(data.getData());
            startActivity(intent);
        }

        // Open camera and then load it to Image View
        if(requestCode == OPEN_CAMERA && data.getExtras().get("data") != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("captured_photo", photo);
            startActivity(intent);
        }

        // After chosen image from photo gallery in the phone, the image uploaded to ImageView
        if (requestCode == UPLOAD_IMAGE && resultCode == RESULT_OK){
            imageUri = data.getData();
            uploadImage.setImageURI(imageUri);
        } else {
            Toast.makeText(PhotoEditorActivity.this, "No image selected", Toast.LENGTH_LONG).show();
        }
    }
}