package com.example.hanh.ava_android.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter{

    private Context context;
    private View view;

    public CustomInfoWindow(Context context) {
        context = context;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
