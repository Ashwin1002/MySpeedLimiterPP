package com.ashwin.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import java.util.List;

public class LogViewTripParent extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    List<SpeedDetailClass> speedlist = new ArrayList<>();
    DatabaseReference RootRef, UserRef;
    RecyclerView TriprecyclerView;
    private String receiverUserID;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Query postQuery;

    //Menu Variable Declaration
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_view_trip_parent);

        TriprecyclerView = (RecyclerView) findViewById(R.id.logrecyclerView);
        TriprecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        TriprecyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userid = firebaseUser.getUid();
        receiverUserID = getIntent().getExtras().get("visit_log_id").toString();



        Log.d("com.ashwin.prototype", "Firebase userid: " +userid);
        //Toast.makeText(TravelLogActivity.this, "userid: "+userid, Toast.LENGTH_SHORT).show();
        RootRef = FirebaseDatabase.getInstance().getReference().child("TravelLog");

        postQuery = RootRef.child(receiverUserID);
        speedlist = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_log);

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
;
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<SpeedDetailClass> options =
                new FirebaseRecyclerOptions.Builder<SpeedDetailClass>()
                        .setQuery(postQuery, SpeedDetailClass.class)
                        .build();

        FirebaseRecyclerAdapter<SpeedDetailClass, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<SpeedDetailClass, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull SpeedDetailClass model) {

                        holder.setTriptitle(model.getTripT());
                        holder.setTripDes(model.getTripD());
                        holder.setTripdits(model.getDist());
                        holder.setTripmax(model.getMax());
                        holder.setTripavg(model.getAvg());
                        holder.setTriploc(model.getLoc());



                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.parent_singleview_log, viewGroup, false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;
                    }
                };

        TriprecyclerView.setAdapter(adapter);

        adapter.startListening();


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        public TextView triptitle, TripDes, Tripdits, Tripmax, Tripavg, Triploc;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            triptitle = itemView.findViewById(R.id.LogTripTV);
            TripDes = itemView.findViewById(R.id.LogTripDesTV);
            Tripdits = itemView.findViewById(R.id.txtdistance1);
            Tripmax = itemView.findViewById(R.id.tvMaxSpeed2);
            Tripavg = itemView.findViewById(R.id.tvAvgSpeed3);
            Triploc = itemView.findViewById(R.id.tvOverSpeed4);



        }
        public void setTriptitle(String triptitle){
            this.triptitle.setText(triptitle);
        }
        public void setTripDes(String TripDes) {
            this.TripDes.setText(TripDes);
        }
        public void setTripdits(String tripdits){
            this.Tripdits.setText(tripdits);
        }
        public void setTripmax(String tripmax){
            this.Tripmax.setText(tripmax);
        }
        public void  setTripavg(String tripavg){
            this.Tripavg.setText(tripavg);
        }
        public void setTriploc(String triploc){
            this.Triploc.setText(triploc);
        }
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
                Intent intent2 = new Intent(LogViewTripParent.this, ParentDashboard.class);
                startActivity(intent2);
                break;
            case R.id.nav_notifications:
                Intent intent = new Intent(LogViewTripParent.this, NotificationUserActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_log:
                break;

            case R.id.nav_trace:
                Intent intent4 = new Intent(LogViewTripParent.this, RiderListView.class);
                startActivity(intent4);
                break;

            case R.id.add_user:
                Intent intent6 = new Intent(LogViewTripParent.this, AddUserActivity.class);
                startActivity(intent6);
                break;

            case R.id.see_report:
                Intent intent7 = new Intent(LogViewTripParent.this, BarGraph.class);
                startActivity(intent7);
                break;

            case R.id.nav_profile:
                Intent intent3 = new Intent(LogViewTripParent.this, ParentProfileActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_reset:
                Intent intent1 = new Intent(getApplicationContext(), ParentResetPasswordActivity.class);
                startActivity(intent1);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent5 = new Intent(LogViewTripParent.this, UserSelect.class);
                startActivity(intent5);
                finish();
                Toast.makeText(LogViewTripParent.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
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