package com.ashwin.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserLocationActivity extends AppCompatActivity {

    //Initialise variable
    Button btlocation;
    TextView textView1, textView2, textView3, textView4, textView5;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseDatabase rootnode;
    FirebaseAuth firebaseAuth;
    DatabaseReference savereference;

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
}