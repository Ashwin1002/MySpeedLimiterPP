package com.ashwin.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParentDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView dashboarduser, parentname, parentemail;

    Button seelog, AddUserBtn, notify_panel, traceuser, ReportBtn;

    public static final String PARENT_ID = "parentid";
    public static final String PARENT_KEY = "parentname";

    DatabaseReference databaseReference, UserRef;
    List<ParentHelperClass> parentHelperClass;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        dashboarduser = findViewById(R.id.dashboarduser);
        parentname =findViewById(R.id.parentname);
        parentemail = findViewById(R.id.parentemail);
        seelog = findViewById(R.id.seelog);
        AddUserBtn = findViewById(R.id.AddUserBtn);
        notify_panel = findViewById(R.id.notify_panel);
        traceuser = findViewById(R.id.traceuser);
        ReportBtn = findViewById(R.id.ReportBtn);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        parentHelperClass = new ArrayList<>();

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);

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
        String parentId = firebaseUser.getUid();


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Parent").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ParentHelperClass userHelperClass = snapshot.getValue(ParentHelperClass.class);
                assert userHelperClass != null;
                dashboarduser.setText("Hi, " +userHelperClass.getUsername()+"!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ParentDashboard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        seelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6 = new Intent(ParentDashboard.this, RiderLogViewActivity.class);
                intent6.putExtra("parentId", parentId);
                startActivity(intent6);
            }
        });

        AddUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adduserint = new Intent(getApplicationContext(), AddUserActivity.class);
                startActivity(adduserint);
            }
        });

        notify_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notifyint = new Intent(getApplicationContext(), NotificationUserActivity.class);
                startActivity(notifyint);
            }
        });

        traceuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent traceint = new Intent(getApplicationContext(), RiderListView.class);
                startActivity(traceint);
            }
        });

        ReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportint = new Intent(getApplicationContext(), BarGraph.class);
                startActivity(reportint);
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
                Intent intent = new Intent(ParentDashboard.this, NotificationUserActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_log:
                Intent intent2 = new Intent(ParentDashboard.this, RiderLogViewActivity.class);
                startActivity(intent2);
                break;

            case R.id.nav_trace:
                Intent intent4 = new Intent(ParentDashboard.this, RiderListView.class);
                startActivity(intent4);
                break;

            case R.id.add_user:
                Intent intent6 = new Intent(ParentDashboard.this, AddUserActivity.class);
                startActivity(intent6);
                break;

            case R.id.see_report:
                Intent intent7 = new Intent(ParentDashboard.this, BarGraph.class);
                startActivity(intent7);
                break;

            case R.id.nav_profile:
                Intent intent3 = new Intent(ParentDashboard.this, ParentProfileActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent5 = new Intent(ParentDashboard.this, UserSelect.class);
                startActivity(intent5);
                finish();
                Toast.makeText(ParentDashboard.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
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