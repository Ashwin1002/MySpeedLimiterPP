package com.ashwin.prototype;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ashwin.prototype.modelclass.CountViolation;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarGraph extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    List<CountViolation> userlist = new ArrayList<>();
    DatabaseReference RootRef, mPostReference;
    RecyclerView recyclerView;
    ListView listViewArtists;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Query postQuery, query;
    ValueEventListener valueEventListener;

    private String countSpeed;
    String rRider, userID, name;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private DatabaseReference UserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);

        listViewArtists = (ListView) findViewById(R.id.listViewArtists);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference().child("SpeedCount");
        String userId = firebaseUser.getUid();
        postQuery = RootRef.child(userId);
        userlist = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.see_report);

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


        BarChart barChart = findViewById(R.id.bargraph);
        mPostReference = FirebaseDatabase.getInstance().getReference().child("SpeedCount");
        query = mPostReference.orderByChild("parentID").equalTo(userId);

        query.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> visitors = new ArrayList<>();
                float i = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    i = i + 1;
                    countSpeed = ds.child("violation_count").getValue().toString();
                    int counter = Integer.parseInt(countSpeed);
                    name = String.valueOf(ds.child("name").getValue());
                    visitors.add(new BarEntry(i, counter));
                    ArrayList<String> riders = new ArrayList<>();
                    riders.add(name);
                    BarDataSet barDataSet = new BarDataSet(visitors, "Riders");
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);

                    BarData barData = new BarData( barDataSet);

                    barChart.setFitBars(true);
                    barChart.setData(barData);
                    barChart.getDescription().setText("Comparing Speed Violation");
                    barChart.animateY(2000);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        userlist.clear();
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot postsnapshot : snapshot.getChildren()) {
                    CountViolation violation = postsnapshot.getValue(CountViolation.class);
                    userlist.add(violation);
                }
                UserSpeedCountList adapter = new UserSpeedCountList(BarGraph.this, userlist);
                listViewArtists.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                Intent intent7 = new Intent(BarGraph.this, ParentDashboard.class);
                startActivity(intent7);
                break;
            case R.id.nav_notifications:
                Intent intent = new Intent(BarGraph.this, NotificationUserActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_log:
                Intent intent2 = new Intent(BarGraph.this, RiderLogViewActivity.class);
                startActivity(intent2);
                break;

            case R.id.nav_trace:
                Intent intent4 = new Intent(BarGraph.this, RiderListView.class);
                startActivity(intent4);
                break;

            case R.id.add_user:
                Intent intent6 = new Intent(BarGraph.this, AddUserActivity.class);
                startActivity(intent6);
                break;

            case R.id.see_report:
                break;

            case R.id.nav_profile:
                Intent intent3 = new Intent(BarGraph.this, ParentProfileActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_reset:
                Intent intent1 = new Intent(getApplicationContext(), ParentResetPasswordActivity.class);
                startActivity(intent1);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent5 = new Intent(BarGraph.this, UserSelect.class);
                startActivity(intent5);
                finish();
                Toast.makeText(BarGraph.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}