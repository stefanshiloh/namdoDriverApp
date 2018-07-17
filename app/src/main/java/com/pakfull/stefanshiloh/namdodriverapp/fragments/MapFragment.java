package com.pakfull.stefanshiloh.namdodriverapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pakfull.stefanshiloh.namdodriverapp.CountTimeUp;
import com.pakfull.stefanshiloh.namdodriverapp.GetDirection;
import com.pakfull.stefanshiloh.namdodriverapp.R;
import com.pakfull.stefanshiloh.namdodriverapp.activities.MainActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class MapFragment extends Fragment implements OnMapReadyCallback{


    public GoogleMap mMap;
    private MarkerOptions userMarker;
    private LatLng myLocation;

    Object dataTransfer[] = new Object[5];

    private CardView bookView;
    private Button startRide;
    private Button endRide;

    private TextView txtDistance;
    private TextView txtWaiting;
    private TextView txtFare;

    private int waitingTime = 0;




    public MapFragment() {
        // Required empty public constructor
    }


    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view;

        view =  inflater.inflate(R.layout.fragment_map, container, false);
        bookView = (CardView)view.findViewById(R.id.bookView);

        txtDistance = (TextView)view.findViewById(R.id.goneDistance);
        txtWaiting = (TextView)view.findViewById(R.id.waitingTime);
        txtFare = (TextView)view.findViewById(R.id.Fare);

        startRide = (Button)view.findViewById(R.id.startRide);
        endRide = (Button)view.findViewById(R.id.endRide);

        bookView.setVisibility(View.GONE);
        txtDistance.setVisibility(View.GONE);
        txtWaiting.setVisibility(View.GONE);
        txtFare.setVisibility(View.GONE);



        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment destination  = (PlaceAutocompleteFragment)getActivity().getFragmentManager().findFragmentById(R.id.destination);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("LK")
                .build();

        destination.setFilter(typeFilter);

        destination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {


            }

            @Override
            public void onError(Status status) {

            }
        });


        startRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRide.setVisibility(View.GONE);
                txtDistance.setVisibility(View.VISIBLE);
                txtWaiting.setVisibility(View.VISIBLE);
                txtFare.setVisibility(View.VISIBLE);

                MainActivity maain = new MainActivity();
                maain.setrideStart(true);
                Log.v("MAPIE", "CALLED MAIN FUNCTION SET: ....");



            }
        });


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
    }


    public void setDistance(float meters){
        txtDistance.setText(String.valueOf(meters));
    }
    public void setWaiting(int seconds){
        txtWaiting.setText(String.valueOf(seconds));
    }
    public void setFareAmount(Double fare){
        txtFare.setText(fare.toString());
    }





    public void WaitingTime(boolean value){


        CountTimeUp timer = new CountTimeUp(1000) {
            public void onTick(int second) {
                waitingTime += 8;
                txtWaiting.setText(String.valueOf(waitingTime));
            }
        };


        if(value){
            timer.start();

        }else{
            timer.cancel();
        }




    }





    public void setUserMarket(LatLng latlong){
        if(userMarker == null){
            userMarker = new MarkerOptions().position(latlong);
            userMarker.title("Client Position");
            userMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mMap.addMarker(userMarker);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong,17));

    }


   public void setDestination(LatLng latlong){

       Log.v("MAPIE", "setDestination: ....");

       bookView.setVisibility(View.VISIBLE);

       setDestinationLocation(latlong.latitude, latlong.longitude);

       LatLngBounds.Builder builder = new LatLngBounds.Builder();

       builder.include(latlong);
       builder.include(myLocation);
       LatLngBounds bounds = builder.build();
       int width = getResources().getDisplayMetrics().widthPixels;
       int height = getResources().getDisplayMetrics().heightPixels;
       int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
       CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

       mMap.animateCamera(cu);

    }

    public void setDestinationLocation(double end_latitude, double end_longitude){

        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+myLocation.latitude+","+myLocation.longitude);
        googleDirectionsUrl.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionsUrl.append("&key="+"AIzaSyDt0wn5AZ70NSJ4iHXrrLedcQdTCDtuaRM");

        String  url =  googleDirectionsUrl.toString();
        Log.v("MAPIE", url);

        //  String  url = getDirectionsUrl();
        GetDirection getDirectionsData = new GetDirection();
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = new LatLng(end_latitude, end_longitude);
        getDirectionsData.execute(dataTransfer);




    }

    public void setMyLocation(LatLng latLng){
        myLocation = latLng;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
    }





}
