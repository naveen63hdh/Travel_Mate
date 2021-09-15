package com.example.pavinaveen.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.pavinaveen.map.receivers.AlarmReceiver;
import com.example.pavinaveen.map.receivers.InternetBroadCast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.app.AlarmManager.RTC_WAKEUP;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation, currLoc, destLoc,stopLoc;
    Marker mCurrLocationMarker, mDestLocationMarker,mStopLocationMarker;
    boolean flag = true;


    //Notification
//    NotificationCompat.Builder builder;
//    NotificationManager manager;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    boolean ring = true;
    private GoogleMap mMap;

    //Firebase and Autocomplete Declarations

    FirebaseDatabase mFirebaseInstance;
    DatabaseReference myRef;

    AutoCompleteTextView autoCompleteTextView;
    CustomAdapter adapter = null;
    ArrayList<Places> places;
    String[] ar;

//    RadioButton mileRadio,stopRadio;
    ImageButton clrBtn;
    int radioNo;//  1 for Milestone and 2 for Previous Stops

    int mileDist;

    TextView distTxt;

    //sets though which basis alarm should ring
    boolean isMilestone,isStop;
    private boolean canSet;


    private boolean cancelUpdate;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mileDist=0;
        isMilestone=false;
        isStop=false;
        cancelUpdate=false;


        distTxt=findViewById(R.id.distTxt);
        //checks whether the permission is granted or not
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        //checks gps is enabled or not
        final LocationManager locManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGps();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Changes Gps Button Direction
        View mapView = mapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(1) != null) {
            // Get the button view
            @SuppressLint("ResourceType") View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
        //Firebase and Autocomplete initialization

        places = new ArrayList<>();
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setEnabled(true);
        autoCompleteTextView.setEms(15);

        //Radio Buttons
//        mileRadio=findViewById(R.id.radioButton3);
//        stopRadio=findViewById(R.id.radioButton2);

        //Image Button
        clrBtn=findViewById(R.id.clrBtn);
        clrBtn.setVisibility(View.GONE);


        //Alarm Manager Initialization
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Firebase Initialization

        mFirebaseInstance = FirebaseDatabase.getInstance();
        myRef = mFirebaseInstance.getReference("Places");

        //Checks Network is on or off
        BroadcastReceiver internetBroadcast = new InternetBroadCast();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(internetBroadcast, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        //Fetching from firebase and storing in autocomplete's adapter

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i = 0;
                ar = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    ar[i] = child.getKey();

                    //Updates to Customer on every single changes
                    places.add(new Places(ar[i]));
                    adapter = new CustomAdapter(MapsActivity.this, places);
                    autoCompleteTextView.setAdapter(adapter);
                    i++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });





        //On Click for Radio Button
//        mileRadio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                radioNo=1;
//
//            }
//        });
//
//        stopRadio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                radioNo=2;
//
//            }
//        });


        //if an items is selected form autocomplete
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myRef.child(autoCompleteTextView.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        autoCompleteTextView.setEms(12);
                        autoCompleteTextView.setEnabled(false);
                        clrBtn.setVisibility(View.VISIBLE);

                        int i=0;
                        double lattitude=0,longitude=0;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            if(i==0)
                                lattitude= (double) child.getValue();
                            else
                                longitude= (double) child.getValue();
                            i++;

                        }

                        destMarker(new LatLng(lattitude, longitude));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be displayed , do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert=builder.create();
        alert.show();
    }


    public void resultOfMileStone(int dist){
        radioNo=1;
        mileDist=dist;
//        if(unit.equals("m"))
//            mileDist=dist/1000;
//        else


    }


    public void resultOfStopAC(String place,double lattitude,double longitude){
        radioNo=2;
        stopMarker(new LatLng(lattitude,longitude));
    }

    public void buttonClicked(boolean canSet) {

        if(radioNo==1)
        {
            isMilestone=true;
            isStop=false;
        }
        else if(radioNo==2)
        {
            isMilestone=false;
            isStop=true;
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
//        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        }
    }

    void destMarker(LatLng destLatLng) {

        destLoc = new Location("");
        destLoc.setLatitude(destLatLng.latitude);
        destLoc.setLongitude(destLatLng.longitude);



        if (mDestLocationMarker != null) {
            //if already destination marker exists , simply remove it
            mDestLocationMarker.remove();
        }

        //Place the marker at the Destination Location and set it draggable
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(destLatLng);
        markerOptions.title("Destination Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mDestLocationMarker = mMap.addMarker(markerOptions);
        mDestLocationMarker.setDraggable(true);


        notifyMe();

    }

    private void notifyMe() {

        float destdist=currLoc.distanceTo(destLoc)/1000;

        distTxt.setText(String.valueOf(destdist));
        //Live Notification based on distance
        //Initializes distance btw current and destination marker
//        builder = new NotificationCompat.Builder(MapsActivity.this)
//                .setSmallIcon(R.drawable.ic_places)
//                .setContentTitle("Live Distance ")
//                .setContentText("Distance : " + currLoc.distanceTo(destLoc) / 1000 + "KM")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//
//
//        // Add as notification
//        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//
//            NotificationChannel channel = new NotificationChannel("channelID","name", NotificationManager.IMPORTANCE_HIGH);
////            channel.enableLights(true);
////            channel.setLightColor(Color.RED);
//            channel.enableVibration(false);
//            channel.setSound(null,null);
//
////            channel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
//            assert manager!=null;
//            builder.setChannelId("channelID");
////            channel.setDescription("Description");
//            manager.createNotificationChannel(channel);
//        }
//
//
//        manager.notify(0, builder.build());

    }


    private void stopMarker(LatLng stopLatLng){

        if (mStopLocationMarker != null) {
            //if already destination marker exists , simply remove it
            mStopLocationMarker.remove();
        }

        stopLoc=new Location("");
        stopLoc.setLatitude(stopLatLng.latitude);
        stopLoc.setLongitude(stopLatLng.longitude);

        //Orange Marker for stop by location
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(stopLatLng);
        markerOptions.title("Stopping Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mStopLocationMarker = mMap.addMarker(markerOptions);
        mStopLocationMarker.setDraggable(false);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {


        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {

            //sets my location as default location with a blue dot line

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                //Geocoder set of codes used to decrypt the latlang in to locations
                Geocoder geocoder;
                List<Address> addresses;
                String address = "", locality = "";
                geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    address = addresses.get(0).getAddressLine(0);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, "Can't get Address", Toast.LENGTH_SHORT).show();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                autoCompleteTextView.setEms(12);
                autoCompleteTextView.setEnabled(false);
                autoCompleteTextView.setText(address);
                clrBtn.setVisibility(View.VISIBLE);


                //sets the destination marker at the marked location
                destMarker(latLng);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                destMarker(marker.getPosition());
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {

        //Code for blue dot icon to improve accuracy

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //gets current location
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        float distcal;

        mLastLocation = location;
        currLoc = location;
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
            mCurrLocationMarker.setPosition(myLatLng);
            if (destLoc != null) {

                distcal = currLoc.distanceTo(destLoc) / 1000;
                distTxt.setText(String.valueOf(distcal));
//                Toast.makeText(MapsActivity.this, "Distance : " + currLoc.distanceTo(destLoc) / 1000, Toast.LENGTH_SHORT).show();
                //updates the distance btw current marker and dest marker
//                builder.setContentText("Distance : " + distcal + "KM");
//                manager.notify(0, builder.build());

                if(isMilestone) {

                    if (distcal < mileDist && ring) {
                        alarmMe();
                        ring = false;
                    }
                }
                else if (isStop)
                {
                    distcal = currLoc.distanceTo(stopLoc) / 1000;
//                Toast.makeText(MapsActivity.this, "Distance : " + currLoc.distanceTo(destLoc) / 1000, Toast.LENGTH_SHORT).show();

                    if (distcal*1000 < 500 && ring) {
                        alarmMe();
                        ring = false;
                    }
                }
            }
        }


        //Place current location marker

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLatLng);
        markerOptions.title("Current Position");
        markerOptions.zIndex(20);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        if (flag) {
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            flag = false;
        }

        //uncomment to remove the location updates
        if (mGoogleApiClient != null&&cancelUpdate)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }

    }

    private void alarmMe() {

            cancelUpdate=true;
//            manager.cancelAll();
            Calendar cal = Calendar.getInstance();
            cal.setTime(cal.getTime());
            cal.add(Calendar.SECOND, 5);
            Toast.makeText(MapsActivity.this, "Alarm will ring at" + cal.getTime(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AlarmReceiver.class);
            sendBroadcast(intent);
//        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//        alarmManager.setRepeating(RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);


    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    if (mGoogleApiClient == null) {
                        buildGoogleApiClient();
                    }
                    mMap.setMyLocationEnabled(true);
                }

            } else {

                // permission denied, Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void txtClear(View view) {

        autoCompleteTextView.setText("");
        autoCompleteTextView.setEms(15);
        autoCompleteTextView.setEnabled(true);
        clrBtn.setVisibility(View.GONE);
        mDestLocationMarker.remove();
        distTxt.setText("0.0 km");
//        manager.cancelAll();

    }

    public void setAlarm(View view) {
//        if(radioNo==0) {
//            Toast.makeText(this, "Select either Milestone or Previous Stops", Toast.LENGTH_SHORT).show();
//        }
//        else if(radioNo==1) {
//            CustomMilestoneDialog customLoginDialog = new CustomMilestoneDialog();
//            customLoginDialog.show(getSupportFragmentManager(), "milestone_dialog");
//        }
//        else if(radioNo==2) {
//            CustomStopDialog customStopDialog=new CustomStopDialog();
//            customStopDialog.show(getSupportFragmentManager(),"stop_dialog");
//        }

        BottomSheet bottomSheet = new BottomSheet();

        Bundle bundle=new Bundle();
        bundle.putString("destLoc", autoCompleteTextView.getText().toString());
        bottomSheet.setArguments(bundle);
        bottomSheet.show(MapsActivity.this.getSupportFragmentManager(),"example_bottom_sheet");

    }


}
