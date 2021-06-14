package com.ashwin.prototype;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private TextView tvSpeed, tvUnit, tvAccuracy, tvHeading, tvMaxSpeed, txtdistance, tvSpeedLimit, tvAvgSpeed, tvOverSpeed, tvlatlon;
    private static final String[] unit = {"km/h", "mph", "meter/sec", "knots"};
    private static final int REQUEST_CODE_IMAGE = 101;
    private int unitType;
    private NotificationCompat.Builder mbuilder;
    private NotificationManager mnotice;
    private double maxSpeed = -100.0;
    double distance = 0, time1;
    double avg = 0;
    private double limitSpeed;

    private MainActivity activity;
    Context context;
    private int countSpeed =0, counts;

    private SharedPreferences prefs;

    long start,finish,time;//variables that will help me count time in milliseconds
    boolean above;

    FirebaseDatabase rootNode;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private  String currentuserid;

    DatabaseReference reference, countreference, savereference, distreference, avgreference;
    LimitClass limitClass;


    //Menu Variable Initialised
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //firebase initialised
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Menu Start
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);
        //menu closed

        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvMaxSpeed = (TextView) findViewById(R.id.tvMaxSpeed);
        txtdistance = (TextView) findViewById(R.id.txtdistance);
        tvUnit = (TextView) findViewById(R.id.tvUnitc);
        tvAvgSpeed = (TextView)findViewById(R.id.tvAvgSpeed);

        tvAccuracy = (TextView) findViewById(R.id.tvAccuracy);
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        tvSpeedLimit = (TextView) findViewById(R.id.tvSpeedLimit);
        tvOverSpeed = (TextView) findViewById(R.id.tvOverSpeed);
        tvlatlon = (TextView) findViewById(R.id.tvLatitude);

        double lat1, lon1;

        activity = this;
        counts = 0;

        above = false;
        //for handling notification
        mbuilder = new NotificationCompat.Builder(this);
        mnotice = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        unitType = Integer.parseInt(prefs.getString("unit", "1"));
        tvUnit.setText(unit[unitType - 1]);

        if (savedInstanceState != null) {
            maxSpeed = savedInstanceState.getDouble("maxspeed", -100.0);
            limitSpeed = savedInstanceState.getDouble("limitspeed", -100.0);

        }

        if (!this.isLocationEnabled(this)) {


            //show dialog if Location Services is not enabled


            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.gps_not_found_title);  // GPS not found
            builder.setMessage(R.string.gps_not_found_message); // Want to enable?
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    activity.startActivity(intent);
                }
            });

            //if no - bring user to selecting Static Location Activity
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(activity, "Please enable Location-based service / GPS", Toast.LENGTH_LONG).show();


                }


            });
            builder.create().show();


        }

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_APN_SETTINGS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //requesting permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_APN_SETTINGS}, 1);
        } else {
            //permission is granted and you can change APN settings
        }


        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "My wakelook");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        new SpeedTask(this).execute("string");


        tvSpeed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

                return false;
            }


        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //premission granted by user
                } else {
                    //permission denied by user
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putDouble("maxspeed", maxSpeed);


    }



    protected void onRestoreInstanceState(Bundle bundle) {

        super.onRestoreInstanceState(bundle);

        maxSpeed = bundle.getDouble("maxspeed", -100.0);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        unitType = Integer.parseInt(prefs.getString("unit", "1"));
        maxSpeed = prefs.getFloat("maxspeed", -100.0f);
//        limitSpeed = Integer.parseInt(tvSpeedLimit.getText().toString());

        tvUnit.setText(unit[unitType - 1]);

        if (maxSpeed > 0) {

            float multiplier = 3.6f;

            switch (unitType) {
                case 1:
                    multiplier = 3.6f;
                    break;
                case 2:
                    multiplier = 2.25f;
                    break;
                case 3:
                    multiplier = 1.0f;
                    break;

                case 4:
                    multiplier = 1.943856f;
                    break;

            }
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(0);

            tvMaxSpeed.setText(numberFormat.format(maxSpeed * multiplier));

        }

        removeNotification();


    }


    protected void onStop() {
        super.onStop();


        displayNotification();


    }

    protected void onPause() {
        super.onPause();

        float tempMaxpeed = 0.0f;
        try {

            tempMaxpeed = Float.parseFloat(tvMaxSpeed.getText().toString());


        } catch (java.lang.NumberFormatException nfe) {

            tempMaxpeed = 0.0f;

        }

//        float tempLimitspeed = 0.0f;
//        try {
//            tempLimitspeed = Float.parseFloat(tvSpeedLimit.getText().toString());
//        } catch (java.lang.NumberFormatException nfe) {
//            tempLimitspeed = 0.0f;
//        }

        prefs.edit().putFloat("maxSpeed", tempMaxpeed);
//        prefs.edit().putFloat("limitSpeed", tempLimitspeed);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        // code of other listview which you want to clear
    }

    private void displayNotification() {

        mbuilder.setSmallIcon(R.drawable.applogo);
        mbuilder.setContentTitle("Vehicle Speed Limiter is running...");
        mbuilder.setContentText("Click to view");

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder = TaskStackBuilder.create(this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder.addParentStack(MainActivity.class);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder.addNextIntent(resultIntent);
        }

        PendingIntent resultPendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        mbuilder.setContentIntent(resultPendingIntent);


        mnotice.notify(1337, mbuilder.build());


    }

    private void removeNotification() {
        mnotice.cancel(1337);
    }




    private class SpeedTask extends AsyncTask<String, Void, String> {
        final MainActivity activity;
        float speed = 0.0f;
        double lat1, lat2;
        double lon1, lon2;
        int counts;
        LocationManager locationManager;


        public SpeedTask(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);


            return null;

        }

        protected void onPostExecute(String result) {
            tvUnit.setText(unit[unitType - 1]);
//            limitSpeed = Integer.parseInt(tvSpeedLimit.getText().toString());

            LocationListener listener = new LocationListener() {
                float filtSpeed;
                float localspeed;

                @Override
                public void onLocationChanged(Location location) {

                    if(counts==0) {//If the counter is 0 which means it is the first time that the location has changed
                        start = System.nanoTime();//a timer starts
                        lat1 = location.getLatitude();//the coordinates are saved
                        lon1 = location.getLongitude();
                        distance = 0;//I initialize the distance
                        //alt1 = location.getAltitude();//this would also save the altitude
                        counts +=1;
                    }

                    else if (counts == 5) {//if the location has changed 5 times (I choose 5 times nad not 0 in order for the calculations to be less)
                        finish = System.nanoTime();//I finish the timer
                        lat2 = location.getLatitude();
                        lon2 = location.getLongitude();
                        time = finish - start;//I save the time in nanoseconds
                        time1 = (double)time / 1_000_000_000.0;//I convert time in seconds
                        distance = distance + measureDistance(lat1, lat2, lon1, lon2);
                        speed = location.getSpeed();
                        float multiplier = 3.6f;

                        switch (unitType) {
                            case 1:
                                multiplier = 3.6f;
                                break;
                            case 2:
                                multiplier = 2.25f;
                                break;
                            case 3:
                                multiplier = 1.0f;
                                break;

                            case 4:
                                multiplier = 1.943856f;
                                break;

                        }

                        if (maxSpeed < speed) {
                            maxSpeed = speed;
                        }

                        localspeed = speed * multiplier;

                        filtSpeed = filter(filtSpeed, localspeed, 2);


                        NumberFormat numberFormat = NumberFormat.getNumberInstance();
                        numberFormat.setMaximumFractionDigits(0);

                        //speed=(float) location.getLatitude();
                        Log.d("com.ashwin.prototype", "Speed " + localspeed + "latitude: " + lat2 + " longitude: " + lon2);

                        //shows the current speed of the user
                        tvSpeed.setText(numberFormat.format(filtSpeed));


                        //show the max speed of the vehicle
                        tvMaxSpeed.setText(numberFormat.format(maxSpeed * multiplier));

                        //calling check speed method to compare the speed of the vehicle and speed limit set
                        checkSpeed(lat1, lon1, (int) speed);

                        countViolation();

                        String lat3 = String.format("%.3f", lat2);
                        String lon3 = String.format("%.3f", lon2);

                        tvlatlon.setText(lat3 + "," + lon3);


                        getTime(distance, maxSpeed);


                        // getavgSpeed(distance);

                        if (location.hasAltitude()) {
                            tvAccuracy.setText(numberFormat.format(location.getAccuracy()) + " m");
                        } else {
                            tvAccuracy.setText("NIL");
                        }

                        numberFormat.setMaximumFractionDigits(0);


                        if (location.hasBearing()) {

                            double bearing = location.getBearing();
                            String strBearing = "NIL";
                            if (bearing < 20.0) {
                                strBearing = "North";
                            } else if (bearing < 65.0) {
                                strBearing = "North-East";
                            } else if (bearing < 110.0) {
                                strBearing = "East";
                            } else if (bearing < 155.0) {
                                strBearing = "South-East";
                            } else if (bearing < 200.0) {
                                strBearing = "South";
                            } else if (bearing < 250.0) {
                                strBearing = "South-West";
                            } else if (bearing < 290.0) {
                                strBearing = "West";
                            } else if (bearing < 345.0) {
                                strBearing = "North-West";
                            } else if (bearing < 361.0) {
                                strBearing = "North";
                            }

                            tvHeading.setText(strBearing);
                        } else {
                            tvHeading.setText("NIL");
                        }

                        NumberFormat nf = NumberFormat.getInstance();

                        nf.setMaximumFractionDigits(4);


                        start = 0;//restart time and counter
                        finish = 0;
                        counts=0;
                    }

                    else{
                        lat2 = location.getLatitude();
                        lon2 = location.getLongitude();
                        distance = distance + measureDistance(lat1,lat2,lon1,lon2);//i calculate the distance travelled
                        lat1 = lat2;
                        lon1 = lon2;
                        counts +=1;
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProviderEnabled(String provider) {
                    tvSpeed.setText("STDBY");
                    tvMaxSpeed.setText("NIL");
                    tvSpeedLimit.setText("SL");
                    tvHeading.setText("HEADING");
                    tvAccuracy.setText("ACCURACY");

                }

                @Override
                public void onProviderDisabled(String provider) {
                    tvSpeed.setText("NOFIX");
                    tvMaxSpeed.setText("NOGPS");
                    tvSpeedLimit.setText("NOLIMIT");
                    tvHeading.setText("HEADING");
                    tvAccuracy.setText("ACCURACY");
                }

            };


            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);


        }

        /**
         * Simple recursive filter
         *
         * @param prev Previous value of filter
         * @param curr New input value into filter
         * @return New filtered value
         */
        private float filter(final float prev, final float curr, final int ratio) {
            // If first time through, initialise digital filter with current values
            if (Float.isNaN(prev))
                return curr;
            // If current value is invalid, return previous filtered value
            if (Float.isNaN(curr))
                return prev;
            // Calculate new filtered value
            return (float) (curr / ratio + prev * (1.0 - 1.0 / ratio));
        }
    }


    public void checkSpeed(Double latitude, Double longitude, int speed) {//custom method that checks if we exceeded speed limit
        speed = Integer.parseInt(tvMaxSpeed.getText().toString());
        //limitSpeed = Integer.parseInt(tvSpeedLimit.getText().toString());
        int limit = Integer.parseInt(tvSpeedLimit.getText().toString());

        if (speed > limit && above == false) {
            above = true;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
             try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                Date parsedDate = dateFormat.parse(timeStamp);
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                Toast.makeText(this, "You exceeded speed limit at " + timestamp, Toast.LENGTH_LONG).show();
                final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                final long[] pattern = {2000, 1000};
                vibrator.vibrate(pattern, -1);// 0 mean repeat forever, -1 not repeat

                 String violation = "Speed limit was violated at " + timeStamp +"." + speed + "km/hr was achieved when speed limit was " + limit;

                //inseting into firebase
                 rootNode = FirebaseDatabase.getInstance();
                 firebaseAuth = FirebaseAuth.getInstance();
                 FirebaseUser  rUser = firebaseAuth.getCurrentUser();
                 String userId = rUser.getUid();
                 String email = rUser.getEmail();
                 countreference = rootNode.getReference().child("SpeedCrossed").child(userId);

                 assert rUser != null;
                 HashMap<String, String> hashMap = new HashMap<>();
                 hashMap.put("userId", userId);
                 hashMap.put("violation", violation);
                 hashMap.put("email", email);

               countreference.push().setValue(hashMap);
//                reference.setValue(overSpeedHelperClass);



             } catch (Exception e) {
            }



        } else if (speed < limit && above == true)
            above = false;

    }

    private void countViolation() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        countreference = FirebaseDatabase.getInstance().getReference().child("SpeedCrossed");
        countreference.child(currentuserid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countSpeed = (int) snapshot.getChildrenCount();
                tvOverSpeed.setText(Integer.toString(countSpeed));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isLocationEnabled(Context mContext) {


        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public double measureDistance(Double lat1, Double lat2, Double lon1, Double lon2) {//custom method that calculates distance between two points
        //Haversine Formula for calculating distance betwwen two location
        /**
         * This is the implementation Haversine Distance Algorithm between two places
         * R = earth’s radius (mean radius = 6,371km)
        Δlat = lat2− lat1
        Δlong = long2− long1
        a = sin²(Δlat/2) + cos(lat1).cos(lat2).sin²(Δlong/2)
        c = 2.atan2(√a, √(1−a))
        d = R.c
         *
         */
        final int R = 6371; // Radius of the earth

        double dLat = toRadians(lat2 - lat1);
        double dLon = toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000 *0.001; // convert to meters
        String dist = String.format("%.2f", distance);


        //inseting into firebase
        rootNode = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser  rUser = firebaseAuth.getCurrentUser();
        String userId = rUser.getUid();
        distreference = rootNode.getReference().child("Distance").child(userId);
        assert rUser != null;
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userid", userId);
        hashMap.put("distance", dist);

        distreference.setValue(hashMap);

        distreference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String Tdis = snapshot.child("distance").getValue().toString();
                    txtdistance.setText(Tdis);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return distance;
    }
    public double toRadians(double deg) {
        return deg * (Math.PI / 180);
    }

    public  double getTime(Double dist, Double maxSpeed){
        double time = dist/maxSpeed;

        double avg = dist / time;
        String avgsp = String.format("%.2f", avg);

        //inseting into firebase
        rootNode = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser  rUser = firebaseAuth.getCurrentUser();
        String userId = rUser.getUid();
        avgreference = rootNode.getReference().child("AverageSpeed").child(userId);
        assert rUser != null;
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userid", userId);
        hashMap.put("AvgSpeed", avgsp);

        avgreference.setValue(hashMap);


        avgreference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Tavg = snapshot.child("AvgSpeed").getValue().toString();
                tvAvgSpeed.setText(Tavg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return time;
    }


    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_convert:
                Intent intent5 = new Intent();
                intent5.setClass(this, SettingsActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_directions:
                Intent intent = new Intent(MainActivity.this, DirectionActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_log:
                Intent intent2 = new Intent(MainActivity.this, TravelLogActivity.class);
                startActivity(intent2);
                break;

            case R.id.nav_location:
                Intent intent6 = new Intent(MainActivity.this, UserLocationActivity.class);
                startActivity(intent6);
                break;

            case R.id.nav_profile:
                Intent intent3 = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent4 = new Intent(MainActivity.this, UserSelect.class);
                startActivity(intent4);
                Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                break;


            case R.id.nav_share:
                Toast.makeText(this, "Shared Successfully!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_rate:
                Toast.makeText(this, "Thank You for Rating Us", Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }




    public void btn_showDialog(View view) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.speed_dialog, null);

        final EditText txt_limit = (EditText) mView.findViewById(R.id.txt_limit);
        Button btn_cancel = (Button) mView.findViewById(R.id.btn_cancel);
        Button btn_ok = (Button) mView.findViewById(R.id.btn_ok);

        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int limit = Integer.parseInt(txt_limit.getText().toString());
                String lmt = String.valueOf(limit);

                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser rUser = firebaseAuth.getCurrentUser();
                String userid = rUser.getUid();;
                reference = FirebaseDatabase.getInstance().getReference("SpeedLimit").child(userid);

                assert rUser != null;
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("userid", userid);
                hashMap.put("limit", lmt);

                reference.setValue(hashMap);

                getdata();
                alertDialog.dismiss();

            }




        });

        alertDialog.show();

    }
    private void getdata() {
        // calling add value event listener method
        // for getting the values from database.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // this method is call to get the realtime
                // updates in the data.
                // this method is called when the data is
                // changed in our Firebase console.
                // below line is for getting the data from
                // snapshot of our database.
                String speedlimit = snapshot.child("limit").getValue().toString();
                tvSpeedLimit.setText(speedlimit);
                // after getting the value we are setting
                // our value to our text view in below line.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void btn_saveDialog(View view) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.save_dialog, null);

        final EditText tripTitle = (EditText) mView.findViewById(R.id.tripTitle);
        final EditText tripDes = (EditText) mView.findViewById(R.id.tripDes);

        final TextView txtdistance1 = (TextView) mView.findViewById(R.id.txtdistance1);
        final TextView tvMaxSpeed1 = (TextView) mView.findViewById(R.id.tvMaxSpeed2);
        final TextView tvAvgSpeed1 = (TextView) mView.findViewById(R.id.tvAvgSpeed3);
        final TextView tvOverSpeed1 = (TextView) mView.findViewById(R.id.tvOverSpeed4);

        txtdistance = txtdistance1;
        tvMaxSpeed = tvMaxSpeed1;
        tvAvgSpeed = tvAvgSpeed1;
        tvlatlon = tvOverSpeed1;
        Button btn_cancel1 = (Button) mView.findViewById(R.id.btn_cancel1);
        Button btn_save = (Button) mView.findViewById(R.id.btn_save);

        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        btn_cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Trip Saved!", Toast.LENGTH_SHORT).show();
                String tripT = tripTitle.getText().toString();
                String tripD = tripDes.getText().toString();
                String dist = txtdistance1.getText().toString();
                String max = tvMaxSpeed1.getText().toString();
                String avg = tvAvgSpeed1.getText().toString();
                String loc = tvOverSpeed1.getText().toString();

                rootNode = FirebaseDatabase.getInstance();
                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser rUser = firebaseAuth.getCurrentUser();
                String userId = rUser.getUid();
                savereference  = rootNode.getReference("TravelLog").child(userId);

                assert rUser != null;
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("userId", userId);
                hashMap.put("tripT", tripT);
                hashMap.put("tripD", tripD);
                hashMap.put("dist", dist);
                hashMap.put("max", max);
                hashMap.put("avg", avg);
                hashMap.put("loc", loc);

                savereference.push().setValue(hashMap);

            alertDialog.dismiss();

            }
        });

        alertDialog.show();
    }

    public void btn_resetDialog(View view){

    }



}