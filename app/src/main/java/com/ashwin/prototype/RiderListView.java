package com.ashwin.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

public class RiderListView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    List<UserHelperClass> userlist = new ArrayList<>();
    DatabaseReference UserRef, RootRef;
    RecyclerView recyclerView;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Query postQuery;

    //Menu variable declaration
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_list_view);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewRider);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference().child("Rider");
        String userId = firebaseUser.getUid();
        postQuery =RootRef.child(userId);
        userlist = new ArrayList<>();

        //initialising menu variable into the menu item
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_trace);

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


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<UserHelperClass> options =
                new FirebaseRecyclerOptions.Builder<UserHelperClass>()
                        .setQuery(postQuery, UserHelperClass.class)
                        .build();

        FirebaseRecyclerAdapter<UserHelperClass, MyViewHolder> adapter =
                new FirebaseRecyclerAdapter<UserHelperClass, MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull UserHelperClass model) {

                        holder.setUseremail(model.getEmail());
                        holder.traceBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_log_id = getRef(position).getKey();

                                Intent logintent = new Intent(RiderListView.this, RetrieveMapsActivity.class);
                                logintent.putExtra("visit_log_id", visit_log_id);
                                startActivity(logintent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rider_view_single, viewGroup, false);
                        MyViewHolder viewHolder = new MyViewHolder(view);
                        return viewHolder;
                    }
                };

        recyclerView.setAdapter(adapter);

        adapter.startListening();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView useremail;
        Button traceBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            useremail = itemView.findViewById(R.id.userEmailTv);
            traceBtn = itemView.findViewById(R.id.traceBtn);
        }

        public void setUseremail(String useremail) {
            this.useremail.setText(useremail);
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
                Intent intent4 = new Intent(RiderListView.this, ParentDashboard.class);
                startActivity(intent4);
                break;
            case R.id.nav_notifications:
                Intent intent = new Intent(RiderListView.this, NotificationUserActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_log:
                Intent intent2 = new Intent(RiderListView.this, RiderLogViewActivity.class);
                startActivity(intent2);
                break;

            case R.id.nav_trace:
                break;

            case R.id.add_user:
                Intent intent6 = new Intent(RiderListView.this, AddUserActivity.class);
                startActivity(intent6);
                break;

            case R.id.see_report:
                Intent intent7 = new Intent(RiderListView.this, BarGraph.class);
                startActivity(intent7);
                break;

            case R.id.nav_profile:
                Intent intent3 = new Intent(RiderListView.this, ParentProfileActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent5 = new Intent(RiderListView.this, UserSelect.class);
                startActivity(intent5);
                finish();
                Toast.makeText(RiderListView.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_reset:
                Intent intent1 = new Intent(getApplicationContext(), ParentResetPasswordActivity.class);
                startActivity(intent1);
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