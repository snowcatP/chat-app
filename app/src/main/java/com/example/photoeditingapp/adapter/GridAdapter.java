package com.example.photoeditingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.photoeditingapp.R;
import com.example.photoeditingapp.entity.SharedImage;

import java.util.ArrayList;


// Vu Xuan Hoang 21110770
// Grid adapter used for custom items and then show it to GridView

public class GridAdapter extends BaseAdapter {

    private ArrayList<SharedImage> dataList;
    private Context context;
    LayoutInflater layoutInflater;

    public GridAdapter(ArrayList<SharedImage> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item, null);
        }
        ImageView gridImage = convertView.findViewById(R.id.grid_image);
        TextView gridCaption = convertView.findViewById(R.id.grid_caption);


        // Use Glide library for load image
        // Glide is third-party program can easy load image from provided uri and load it to ImageView
        Glide.with(context).load(dataList.get(position).getImageURL()).into(gridImage);
        gridCaption.setText(dataList.get(position).getCaption());

        return convertView;
    }
}
