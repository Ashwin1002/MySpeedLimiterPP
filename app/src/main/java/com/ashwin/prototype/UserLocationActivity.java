package com.ashwin.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserLocationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Initialise variable
    Button btlocation;
    TextView textView1, textView2, textView3, textView4, textView5;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseDatabase rootnode;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference savereference, UserRef;

    //Menu Variable Initialised
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

        //Assign variable
        btlocation = findViewById(R.id.bt_location);
        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);
        textView3 = findViewById(R.id.text_view3);
        textView4 = findViewById(R.id.text_view4);
        textView5 = findViewById(R.id.text_view5);



        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        View navView = navigationView.inflateHeaderView(R.layout.header);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_location);

        TextView headername = navView.findViewById(R.id.header_name);
        TextView headeremail = navView.findViewById(R.id.header_email);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        UserRef = FirebaseDatabase.getInstance().getReference("Rider").child(firebaseUser.getUid());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullnamelabel = snapshot.child("name").getValue().toString();
                headername.setText(fullnamelabel);
                String email12 = snapshot.child("email").getValue().toString();
                headeremail.setText(email12);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Initialise fusedlocationproviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check permission
                if (ActivityCompat.checkSelfPermission(UserLocationActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //when permission granted
                    getLocation();
                } else {
                    //when permission denied
                    ActivityCompat.requestPermissions(UserLocationActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialise Location
                Location location = task.getResult();
                if (location != null) {
                    //Initialise geoCoder
                    Geocoder geocoder = new Geocoder(UserLocationActivity.this,
                            Locale.getDefault());
                    //initialise address list
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude()
                                ,1
                        );

                        //set latitude on textview
                        textView1.setText(Html.fromHtml(
                                ""
                                + addresses.get(0).getLatitude()
                        ));
                        //set longitude on textview
                        textView2.setText(Html.fromHtml(
                                ""
                                        + addresses.get(0).getLongitude()
                        ));
                        //Set country name
                        textView3.setText(Html.fromHtml(
                                ""
                                        + addresses.get(0).getCountryName()
                        ));
                        textView4.setText(Html.fromHtml(
                                ""
                                        + addresses.get(0).getLocality()
                        ));
                        //set address
                        textView5.setText(Html.fromHtml(
                                ""
                                        + addresses.get(0).getAddressLine(0)
                        ));

                        String latitude = textView1.getText().toString();
                        String longitude = textView2.getText().toString();
                        String country = textView3.getText().toString();
                        String locality = textView4.getText().toString();
                        String addressline = textView5.getText().toString();

                        rootnode = FirebaseDatabase.getInstance();
                        firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser rUser = firebaseAuth.getCurrentUser();
                        String userid = rUser.getUid();
                        savereference = rootnode.getReference().child("LocationDetails").child(userid);

                        assert rUser != null;
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("userId", userid);
                        hashMap.put("Latitude", latitude);
                        hashMap.put("Longitude", longitude);
                        hashMap.put("Country", country);
                        hashMap.put("Locality", locality);
                        hashMap.put("Addressline", addressline);

                        savereference.setValue(hashMap);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
                Intent intent = new Intent(UserLocationActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_convert:
                Intent intent5 = new Intent();
                intent5.setClass(UserLocationActivity.this, SettingsActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_directions:
                Intent intent9 = new Intent(UserLocationActivity.this, DirectionActivity.class);
                startActivity(intent9);
                break;

            case R.id.nav_log:
                Intent intent6 = new Intent(UserLocationActivity.this, TravelLogActivity.class);
                startActivity(intent6);
                break;

            case R.id.nav_location:

                break;

            case R.id.nav_profile:
                Intent intent3 = new Intent(UserLocationActivity.this, UserProfileActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent4 = new Intent(UserLocationActivity.this, UserSelect.class);
                startActivity(intent4);
                Toast.makeText(UserLocationActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                finish();
                break;


            case R.id.nav_share:
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String sub = "Your Subject";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
                Toast.makeText(this, "Shared Successfully!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_rate:
                Toast.makeText(this, "Thank You for Rating Us", Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}