package com.pakfull.stefanshiloh.namdodriverapp.activities;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pakfull.stefanshiloh.namdodriverapp.R;
import com.pakfull.stefanshiloh.namdodriverapp.fragments.MapFragment;
import com.pakfull.stefanshiloh.namdodriverapp.fragments.SettingFragment;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {


    private MapFragment mapFragment;
    private SettingFragment settingFragment;

    private boolean notBooked = true;
    private  boolean rideStart = false;
    private Location myLocation;
    private Location previousLocation;
    private float distanceInMeters = 0;


    LocationManager locationManager;
    Criteria criteria;
    String provider;
    Location location;


    private static final String TAG_SETTING_FRAGMENT = "TAG_SETTING_FRAGMENT";
    private static final String TAG_MAP_FRAGMENT = "TAG_MAP_FRAGMENT";


    final int PERMISSION_LOCATION = 111;
    private GoogleApiClient mGoogleApiClient;

    FragmentManager fragmentManager = getSupportFragmentManager();


    final String BASE_SITE = "https://boiling-garden-70286.herokuapp.com";
    final String NAME = "stefanshiloh";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();



        mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.container_main);
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_main, mapFragment,TAG_MAP_FRAGMENT)
                    .commit();
        }


        Log.v("MAPIE","STARTING UP" );
        Log.v("MAPIE","notBooked"+notBooked );

        if(notBooked){
            String url = BASE_SITE + "/add/drivers/"+NAME;

            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("MAPIE",response.toString() );

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("MAPIE", error.toString());
                }
            });
            Volley.newRequestQueue(this).add(jsonRequest);
        }

            final Handler handler = new Handler();
            Timer timer = new Timer();
            TimerTask backtask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                //To task in this. Can do network operation Also
                                Log.d("MAPIE","Check Run...." );

                                if(notBooked) {
                                    String url = BASE_SITE + "/driver/amibooked/" + NAME;
                                    Log.d("MAPIE", url);


                                    final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            Log.v("MAPIE", "GOT IT");
                                            Log.v("MAPIE", response.toString());

                                            JSONObject client = response.optJSONObject("client");
                                            Log.v("MAPIE", "1: " + client.toString());
                                            JSONObject loc = client.optJSONObject("loc");
                                            Log.v("MAPIE", "2: " + loc.toString());


                                            String Longi = loc.optString("Long");
                                            String Latit = loc.optString("Lati");

                                            LatLng clientLating = new LatLng(Double.parseDouble(Latit), Double.parseDouble(Longi));

                                            mapFragment.setUserMarket(clientLating);

                                            //drawing polines
                                           mapFragment.setDestination(clientLating);


                                        TextView name  = (TextView)mapFragment.getView().findViewById(R.id.name);
                                        name.setText(client.optString("name"));
                                        previousLocation = myLocation;

                                            notBooked = false;


                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.v("MAPIE", "NOT BOOOKED: "+error.toString());
                                        }
                                    });
                                    Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);


                                }else{
                                    Log.d("MAPIE","BOOKED" );
                                }
                                //     checkForBookings.execute(dataTransfer);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                            }
                        }
                    });
                }
            };
            timer.schedule(backtask , 0, 5000); //execute in every 20000 ms*/

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {




        } else if (id == R.id.nav_setting) {




        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void setrideStart(boolean value){
        Log.v("BOY", "Starting RIDE FUNCTION CALLED");

        rideStart = value;
        previousLocation = myLocation;

        Log.v("MAPIEE", "VALUE START: "+value);
        Log.v("BOY", "RIDE START: "+rideStart);
        Log.v("MAPIEE", "previousLocation: "+previousLocation);
    }

    public boolean getIsRideStart(){
        return rideStart;
    }



    //Google api

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
            Log.v("MAPIE", "Reqesting Permission");

        }else{
            Log.v("MAPIE", "Starting Location Starting Service on Connect");
            startLocationServices();

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {

        myLocation = location;



        Log.v("MANO","Long: "+ location.getLongitude() + "- Lati:" + location.getLatitude());
        Log.v("MANO","Speed: "+ location.getSpeed());
        Log.v("MANO","Accuracy: "+ location.getAccuracy());
        Log.v("MANO","Time: "+ location.getTime());



      //  Toast.makeText(this, "CHANGING LOCATION.. RIDE START: " + get, Toast.LENGTH_SHORT).show();    // Showing the distance in meter
        Log.v("BOY", "RIDE START IN LOCATIOM: "+ getIsRideStart());
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        String url = BASE_SITE + "/drivers/location/"+NAME+"/"+location.getLongitude()+"/"+location.getLatitude();

     //   Toast.makeText(this, "ADDING DRIVER LOCATION TO SERVER" +  location.getLongitude() + "- Lati:" + location.getLatitude(), Toast.LENGTH_SHORT).show();    // Showing the distance in meter


        // LOGING
        Log.v("MAPIE","ADDING DRIVER LOCATION TO SERVER");
        Log.v("MAPIE","Long: "+ location.getLongitude() + "- Lati:" + location.getLatitude());
        Log.v("MAPIE",url );

        mapFragment.setMyLocation(new LatLng( location.getLatitude(),location.getLongitude()));

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("MAPIE",response.toString() );



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("MAPIE", error.toString());
            }
        });
        Volley.newRequestQueue(this).add(jsonRequest);

        Log.v("MAPIEE", String.valueOf(rideStart));

        if(notBooked == false && getIsRideStart()){

            Log.v("MAPIEE", " previousLocation: "+ previousLocation.toString());


            float nDistance = previousLocation.distanceTo(location);

            Log.v("MYAPPLE", " nDistance: "+ nDistance);


            if(nDistance > 10){
                mapFragment.WaitingTime(false);
                distanceInMeters += nDistance;
                previousLocation = location;
                mapFragment.setDistance(distanceInMeters);
                Toast.makeText(this, "Distance" + distanceInMeters, Toast.LENGTH_SHORT).show();

            }else{
                mapFragment.WaitingTime(true);

            }

            Log.v("MAPIEE", " DISTANCE"+ String.valueOf(distanceInMeters));




        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_LOCATION : {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startLocationServices();
                    Log.v("MAPIE", "Permission Granted - starting services");
                }else{
                    //show a dialog cannot deline
                    Log.v("MAPIE", "Permission Not Granted - stoped services");
                }
            }
        }

    }

    public void startLocationServices(){
        Log.v("MAPIE", "Starting Service Location Called");
        try{
            LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            req.setInterval(10000);
            req.setFastestInterval(8000);

             LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, req, this);



            Log.v("MAPIE", "Requesting ...");
        }catch (SecurityException exception){
            Log.v("MAPIE", exception.toString());
        }

    }






}
