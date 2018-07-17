package com.pakfull.stefanshiloh.namdodriverapp;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pakfull.stefanshiloh.namdodriverapp.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class CheckForBookings extends AsyncTask<Object, String, HashMap<String, String>> {


    final String BASE_SITE = "https://boiling-garden-70286.herokuapp.com";
    final String NAME = "stefanshiloh";

    GoogleMap mMap;

    @Override
    protected HashMap<String, String> doInBackground(Object... objects) {

        mMap = (GoogleMap)objects[0];




        return null;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> s) {

        if (s != null) {
            Log.v("MAPIE","CLINET FOUND....");
            MarkerOptions userMarker = new MarkerOptions();
            userMarker.position(new LatLng(Double.parseDouble(s.get("Long")) , Double.parseDouble(s.get("Lati"))));
            userMarker.title("Current Position");
            userMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

            mMap.addMarker(userMarker);

        }


    }
}