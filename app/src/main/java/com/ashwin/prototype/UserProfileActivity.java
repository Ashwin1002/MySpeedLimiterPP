
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {

    TextInputLayout fullName,email,phoneNo, password, username_profile;

    Button update;

    TextView fullNameLabel, usernameLabel;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference, UserRef;

    //Menu Variable Initialised
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    //RecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fullName = findViewById(R.id.full_name_profile);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_no_profile);
        password = findViewById(R.id.password_profile);
        fullNameLabel = findViewById(R.id.fullname_field);
        usernameLabel = findViewById(R.id.username_field);
        username_profile = findViewById(R.id.username_profile);
        update = findViewById(R.id.update_profile);


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

        navigationView.setCheckedItem(R.id.nav_profile);

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Rider").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullnamelabel = snapshot.child("name").getValue().toString();
                fullNameLabel.setText(fullnamelabel);
                String usernamelabel = snapshot.child("username").getValue().toString();
                usernameLabel.setText(usernamelabel);
                fullName.getEditText().setText(fullnamelabel);
                String email12 = snapshot.child("email").getValue().toString();
                email.getEditText().setText(email12);
                String phone = snapshot.child("phoneno").getValue().toString();
                phoneNo.getEditText().setText(phone);
                String username_profile1 = snapshot.child("username").getValue().toString();
                username_profile.getEditText().setText(username_profile1);
                String pass = snapshot.child("password").getValue().toString();
                password.getEditText().setText(pass);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UserProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fullName.getEditText().getText().toString();
                String email1 = email.getEditText().getText().toString();
                String phone = phoneNo.getEditText().getText().toString();
                String username = username_profile.getEditText().getText().toString();
                String password1 = password.getEditText().getText().toString();
                HashMap hashMap = new HashMap();
                hashMap.put("name", name);
                hashMap.put("email", email1);
                hashMap.put("phoneno", phone);
                hashMap.put("username", username);
                hashMap.put("password", password1);
                
                DatabaseReference upRef = databaseReference;

                databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(UserProfileActivity.this, "Profile updated Successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                        startActivity(intent);
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
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_convert:
                Intent intent5 = new Intent();
                intent5.setClass(UserProfileActivity.this, SettingsActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_directions:
                Intent intent9 = new Intent(UserProfileActivity.this, DirectionActivity.class);
                startActivity(intent9);
                break;

            case R.id.nav_log:
                Intent intent3 = new Intent(UserProfileActivity.this, TravelLogActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_location:
                Intent intent6 = new Intent(UserProfileActivity.this, UserLocationActivity.class);
                startActivity(intent6);
                break;

            case R.id.nav_profile:
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent4 = new Intent(UserProfileActivity.this, UserSelect.class);
                startActivity(intent4);
                Toast.makeText(UserProfileActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                finish();
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
    
}