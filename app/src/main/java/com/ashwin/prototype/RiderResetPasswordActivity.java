package com.ashwin.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RiderResetPasswordActivity extends AppCompatActivity {

    EditText userPassword, userConfPassword;
    Button resetBtn;
    FirebaseUser user;

    TextView fullNameLabel, usernameLabel;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference, UserRef;

    //Menu Variable Initialised
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_reset_password);

        userPassword = findViewById(R.id.reset_password);
        userConfPassword = findViewById(R.id.reset_confirm_password);

        user = FirebaseAuth.getInstance().getCurrentUser();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        View navView = navigationView.inflateHeaderView(R.layout.header);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        navigationView.setCheckedItem(R.id.nav_reset);

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

        resetBtn = findViewById(R.id.reset_btn);

        String passwordVal = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{6,}" +               //at least 6 characters
                "$";

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userPassword.getText().toString().isEmpty()) {
                    userPassword.setError("Required Field");
                    return;
                }

                if (userConfPassword.getText().toString().isEmpty()) {
                    userConfPassword.setError("Required Field");
                    return;
                }

                if (!userPassword.getText().toString().equals(userConfPassword.getText().toString())){
                    userConfPassword.setError("password do not match");
                    return;
                }
                if (!userPassword.getText().toString().matches(passwordVal)){
                    userPassword.setError("Password is weak, Please input a strong password.");
                }

                user.updatePassword(userPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RiderResetPasswordActivity.this,"Password Updated Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RiderResetPasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
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

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent(RiderResetPasswordActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_convert:
                Intent intent5 = new Intent();
                intent5.setClass(RiderResetPasswordActivity.this, SettingsActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_directions:
                Intent intent9 = new Intent(RiderResetPasswordActivity.this, DirectionActivity.class);
                startActivity(intent9);
                break;

            case R.id.nav_log:
                Intent intent3 = new Intent(RiderResetPasswordActivity.this, TravelLogActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_location:
                Intent intent6 = new Intent(RiderResetPasswordActivity.this, UserLocationActivity.class);
                startActivity(intent6);
                break;

            case R.id.nav_profile:
                Intent intent1 = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(intent1);
                break;

            case  R.id.nav_reset:

                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent4 = new Intent(RiderResetPasswordActivity.this, UserSelect.class);
                startActivity(intent4);
                Toast.makeText(RiderResetPasswordActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
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