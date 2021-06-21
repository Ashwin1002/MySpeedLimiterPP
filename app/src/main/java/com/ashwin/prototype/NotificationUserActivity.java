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

public class NotificationUserActivity extends AppCompatActivity {

    List<UserHelperClass> userlist = new ArrayList<>();
    DatabaseReference database, RootRef, UserRef;
    RecyclerView recyclerView;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Query postQuery;

    //Menu variables declaration
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_user);

        recyclerView = (RecyclerView) findViewById(R.id.notification_userlist);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference().child("Rider");
        String userId = firebaseUser.getUid();
        postQuery =RootRef.child(userId);
        userlist = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        navigationView.setCheckedItem(R.id.nav_notifications);

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



                        holder.setUsername(model.getName());
                        holder.setUseremail(model.getEmail());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_log_id = getRef(position).getKey();
                                String visit_log_name = model.getName();

                                Intent logintent = new Intent(NotificationUserActivity.this, Notifications.class);
                                logintent.putExtra("visit_log_id", visit_log_id);
                                logintent.putExtra("visit_log_name", visit_log_name);
                                startActivity(logintent);
                            }
                        });




                    }

                    @NonNull
                    @Override
                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_notfication_user, viewGroup, false);
                        MyViewHolder viewHolder = new MyViewHolder(view);
                        return viewHolder;
                    }
                };

        recyclerView.setAdapter(adapter);

        adapter.startListening();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, useremail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.nameriderTV);
            useremail = itemView.findViewById(R.id.emailriderTV);

        }

        public void setUsername(String username){
            this.username.setText(username);
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

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent(NotificationUserActivity.this, ParentDashboard.class);
                startActivity(intent);
                break;
            case R.id.nav_notifications:
                break;

            case R.id.nav_log:
                Intent intent2 = new Intent(NotificationUserActivity.this, RiderLogViewActivity.class);
                startActivity(intent2);
                break;

            case R.id.nav_trace:
                Intent intent4 = new Intent(NotificationUserActivity.this, RiderListView.class);
                startActivity(intent4);
                break;

            case R.id.add_user:
                Intent intent6 = new Intent(NotificationUserActivity.this, AddUserActivity.class);
                startActivity(intent6);
                break;

            case R.id.see_report:
                Intent intent7 = new Intent(NotificationUserActivity.this, BarGraph.class);
                startActivity(intent7);
                break;

            case R.id.nav_profile:
                Intent intent3 = new Intent(NotificationUserActivity.this, ParentProfileActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent5 = new Intent(NotificationUserActivity.this, UserSelect.class);
                startActivity(intent5);
                finish();
                Toast.makeText(NotificationUserActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
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