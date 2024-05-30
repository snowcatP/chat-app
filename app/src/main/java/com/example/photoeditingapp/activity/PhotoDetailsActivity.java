package com.example.photoeditingapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.photoeditingapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


// Vu Xuan Hoang 21110770
public class PhotoDetailsActivity extends AppCompatActivity {
    int PHOTO_EDITOR = 200;
    Uri imageUri;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.image_preview);
        EditText imageCaption = findViewById(R.id.upload_caption);


        // Get URL of image and caption
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");
        String caption = intent.getStringExtra("caption");
        Glide.with(PhotoDetailsActivity.this).load(imageUrl).into(imageView);
        imageCaption.setText(caption);

        imageUri = Uri.parse(imageUrl);

        ImageButton saveButton = findViewById(R.id.save_image);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save clicked image to photo gallery
                saveImageToGallery(((BitmapDrawable)imageView.getDrawable()).getBitmap());
            }
        });


        // Call Photo editor activity
        ImageButton editImageButton = findViewById(R.id.edit_image_btn);
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dsPhotoEditorIntent = new Intent(PhotoDetailsActivity.this, DsPhotoEditorActivity.class);
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
        });


        // End the activity
        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // Save image to photo gallery
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


    // Send edited photo to result activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PHOTO_EDITOR && resultCode != RESULT_CANCELED) {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.setData(data.getData());
            startActivity(intent);
        }
    }
}