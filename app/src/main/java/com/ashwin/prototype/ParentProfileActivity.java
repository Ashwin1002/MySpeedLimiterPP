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

public class ParentProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextInputLayout fullName,email,phoneNo, password, username_profile;

    Button update;

    TextView fullNameLabel, usernameLabel;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference, UserRef;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_profile);

        fullName = findViewById(R.id.full_name_profile);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_no_profile);
        password = findViewById(R.id.password_profile);
        fullNameLabel = findViewById(R.id.fullname_field);
        usernameLabel = findViewById(R.id.username_field);
        username_profile = findViewById(R.id.user_name);
        update = findViewById(R.id.updatePBt);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_profile);

        View navView = navigationView.inflateHeaderView(R.layout.parentheader);

        TextView headername = navView.findViewById(R.id.parentname);
        TextView headeremail = navView.findViewById(R.id.parentemail);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        UserRef = FirebaseDatabase.getInstance().getReference("Parent").child(firebaseUser.getUid());
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Parent").child(firebaseUser.getUid());
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

                Toast.makeText(ParentProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(ParentProfileActivity.this, "Profile updated Successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ParentProfileActivity.this, ParentDashboard.class);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_notifications:
                Intent intent = new Intent(ParentProfileActivity.this, NotificationUserActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_log:
                Intent intent2 = new Intent(ParentProfileActivity.this, RiderLogViewActivity.class);
                startActivity(intent2);
                break;

            case R.id.nav_trace:
                Intent intent4 = new Intent(ParentProfileActivity.this, RiderListView.class);
                startActivity(intent4);
                break;

            case R.id.add_user:
                Intent intent6 = new Intent(ParentProfileActivity.this, AddUserActivity.class);
                startActivity(intent6);
                break;

            case R.id.see_report:
                Intent intent7 = new Intent(ParentProfileActivity.this, BarGraph.class);
                startActivity(intent7);
                break;

            case R.id.nav_profile:
                Intent intent3 = new Intent(ParentProfileActivity.this, ParentDashboard.class);
                startActivity(intent3);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent5 = new Intent(ParentProfileActivity.this, UserSelect.class);
                startActivity(intent5);
                finish();
                Toast.makeText(ParentProfileActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
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