package com.example.hanh.ava_android;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GMapV2Direction extends AsyncTask<String, Void, Document> {


    private final static String TAG = GMapV2Direction.class.getSimpleName();
    private Handler handler;
    private LatLng  start, end;

    public GMapV2Direction(Handler handler, LatLng start, LatLng end) {
        this.start = start;
        this.end = end;
        this.handler = handler;
    }
    public static final String MODE_DRIVING = "driving";
    public static final String MODE_WALKING = "walking";

    @Override
    protected Document doInBackground(String... strings) {
        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + start.latitude + "," + start.longitude
                + "&destination=" + end.latitude + "," + end.longitude
                + "&sensor=false&units=metric&mode=driving";
        try {
            URL urlString = new URL(url);
            Log.d("url", url);
            HttpURLConnection connection = (HttpURLConnection)urlString.openConnection();
            InputStream in = connection.getInputStream();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(in);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Document document) {
        super.onPostExecute(document);

        if (document != null) {
            Log.d(TAG, "---- GMapV2Direction OK ----");
            Message message = new Message();
            message.obj = document;
            handler.dispatchMessage(message);
        } else {
            Log.d(TAG, "---- GMapV2Direction ERROR ----");
        }
    }


}
