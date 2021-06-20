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
import androidx.recyclerview.widget.ItemTouchHelper;
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

public class TravelLogActivity extends AppCompatActivity {

    List<SpeedDetailClass> speedlist = new ArrayList<>();
    //    RecyclerViewAdapter adapter;
    DatabaseReference RootRef, UserRef;
    RecyclerView TriprecyclerView;
    private String receiverUserID;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Query postQuery;
    FirebaseRecyclerAdapter<SpeedDetailClass, FindFriendViewHolder> adapter;


    //Menu Variable Initialised
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_log);

        Intent intent = getIntent();
        /*
         * this line is important
         * this time we are not getting the reference of a direct node
         * but inside the node track we are creating a new child with the artist id
         * and inside that node we will store all the tracks with unique ids
         * */


        TriprecyclerView = (RecyclerView) findViewById(R.id.travelrecyclerView);
        TriprecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        TriprecyclerView.setLayoutManager(linearLayoutManager);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userid = firebaseUser.getUid();

        Log.d("com.ashwin.prototype", "Firebase userid: " + userid);
        //Toast.makeText(TravelLogActivity.this, "userid: "+userid, Toast.LENGTH_SHORT).show();
        RootRef = FirebaseDatabase.getInstance().getReference().child("TravelLog");

        postQuery = RootRef.child(userid);
        speedlist = new ArrayList<>();


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
       View navView = navigationView.inflateHeaderView(R.layout.header);

        drawerLayout = findViewById(R.id.drawer_layout);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        navigationView.setCheckedItem(R.id.nav_log);

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


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<SpeedDetailClass> options =
                new FirebaseRecyclerOptions.Builder<SpeedDetailClass>()
                        .setQuery(postQuery, SpeedDetailClass.class)
                        .build();

        adapter =
                new FirebaseRecyclerAdapter<SpeedDetailClass, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull SpeedDetailClass model) {

                        holder.setTriptitle(model.getTripT());
                        holder.setTripDes(model.getTripD());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_log_id = getRef(position).getKey();
                                String trip_name = model.getTripT();
                                Intent logintent = new Intent(TravelLogActivity.this, TravelLogEdit.class);
                                logintent.putExtra("visit_log_id", visit_log_id);
                                logintent.putExtra("trip_name", trip_name);
                                startActivity(logintent);
                            }
                        });



                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_view_item, viewGroup, false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;
                    }
                };



        TriprecyclerView.setAdapter(adapter);

        adapter.startListening();


    }


    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        public TextView triptitle, TripDes, Tripdits, Tripmax, Tripavg, Triploc;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            triptitle = itemView.findViewById(R.id.LogTripTV);
            TripDes = itemView.findViewById(R.id.LogTripDesTV);




        }

        public void setTriptitle(String triptitle) {
            this.triptitle.setText(triptitle);
        }

        public void setTripDes(String TripDes) {
            this.TripDes.setText(TripDes);
        }
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
                Intent intent = new Intent(TravelLogActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_convert:
                Intent intent5 = new Intent();
                intent5.setClass(TravelLogActivity.this, SettingsActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_directions:
                Intent intent9 = new Intent(TravelLogActivity.this, DirectionActivity.class);
                startActivity(intent9);
                break;

            case R.id.nav_log:
                break;

            case R.id.nav_location:
                Intent intent6 = new Intent(TravelLogActivity.this, UserLocationActivity.class);
                startActivity(intent6);
                break;

            case R.id.nav_profile:
                Intent intent3 = new Intent(TravelLogActivity.this, UserProfileActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent4 = new Intent(TravelLogActivity.this, UserSelect.class);
                startActivity(intent4);
                Toast.makeText(TravelLogActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
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