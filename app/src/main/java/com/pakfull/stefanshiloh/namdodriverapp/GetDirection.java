package com.pakfull.stefanshiloh.namdodriverapp;


import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;


public class GetDirection extends AsyncTask<Object,String,String> {

        GoogleMap mMap;
        String url;
        String googleDirectionsData;
        String duration, distance;
        LatLng latLng;




        @Override
        protected String doInBackground(Object... objects) {
            mMap = (GoogleMap)objects[0];
            url = (String)objects[1];
            latLng = (LatLng)objects[2];



            DownloadUrl downloadUrl = new DownloadUrl();
            try {
                googleDirectionsData = downloadUrl.readUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return googleDirectionsData;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v("MAPIE", "Post Execute: ....");
            String[] directionsList;
            DataParser parser = new DataParser();
            directionsList = parser.parseDirections(s);
            displayDirection(directionsList);

        }




        public void displayDirection(String[] directionsList)
        {
            Log.v("MAPIE", "Display Direction: ....");


            int count = directionsList.length;
            for(int i = 0;i<count;i++)
            {
                PolylineOptions options = new PolylineOptions();
                options.color(Color.BLUE);
                options.width(20);
                options.addAll(PolyUtil.decode(directionsList[i]));
                mMap.addPolyline(options);
            }
        }








    }