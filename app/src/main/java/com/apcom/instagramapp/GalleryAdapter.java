package com.apcom.instagramapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by apcom on 30.12.2015.
 */
public class GalleryAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<GalleryItem> imageUrls;

    public GalleryAdapter(Context context, ArrayList<GalleryItem> imageUrls) {
        super(context, R.layout.gridview, imageUrls);
        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.gridview, parent, false);
        }

        Picasso.with(context).load((imageUrls.get(position)).getImageUrl()).fit().into((ImageView)convertView);
        return convertView;
    }
}
