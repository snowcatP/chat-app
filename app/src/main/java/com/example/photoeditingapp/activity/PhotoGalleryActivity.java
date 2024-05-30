package com.example.photoeditingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.photoeditingapp.R;
import com.example.photoeditingapp.adapter.GridAdapter;
import com.example.photoeditingapp.entity.SharedImage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


// Vu Xuan Hoang 21110770
public class PhotoGalleryActivity extends AppCompatActivity {

    GridView gridView;
    ArrayList<SharedImage> dataList;
    GridAdapter gridAdapter;

    // get database reference
    final private DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("SharedImages");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo_gallery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gridView = findViewById(R.id.grid_view);
        dataList = new ArrayList<>();
        gridAdapter = new GridAdapter(dataList, this);
        gridView.setAdapter(gridAdapter);


        // Get single item from database and then load it
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    SharedImage data = dataSnapshot.getValue(SharedImage.class);
                    dataList.add(data);
                }
                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // click to an item will cause new activity
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedImage clickedImage = dataList.get(position);

                Intent intent = new Intent(PhotoGalleryActivity.this, PhotoDetailsActivity.class);
                intent.putExtra("imageUrl", clickedImage.getImageURL());
                intent.putExtra("caption", clickedImage.getCaption());
                startActivity(intent);
            }
        });
    }
}