package com.ashwin.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class AddUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    EditText nameEdText, emailEdText, passwordEdText, phoneEdText, usernameEdText;
    Button btn_Signup;

    FirebaseDatabase rootNode;
    DatabaseReference UserRef;
    DatabaseReference reference, userRef;
    FirebaseAuth firebaseAuth;
    FirebaseUser  firebaseUser;
    TextView text_sign_title;
    String userId;

    //menu variable declaration
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        firebaseAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();


        btn_Signup = findViewById(R.id.btnsignup);
        nameEdText = findViewById(R.id.addname);
        emailEdText = findViewById(R.id.addemail);
        passwordEdText = findViewById(R.id.addpass);
        phoneEdText = findViewById(R.id.addphone);
        usernameEdText = findViewById(R.id.addusername);
        text_sign_title = findViewById(R.id.text_sign_title);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.add_user);

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



        btn_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get all the values
                String name = nameEdText.getText().toString();
                String username = usernameEdText.getText().toString();
                String email = emailEdText.getText().toString();
                String phoneno = phoneEdText.getText().toString();
                String password = passwordEdText.getText().toString();
                reference = FirebaseDatabase.getInstance().getReference("Rider").child(userId);



//                SharedPreferences sharedPreferences = getSharedPreferences(ParentDashboard.PARENT_ID, Context.MODE_PRIVATE);
//                String parentid = sharedPreferences.getString(ParentDashboard.PARENT_KEY, "");


                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneno) || TextUtils.isEmpty(password)) {
                    Toast.makeText(AddUserActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    signup(name, username, email, phoneno, password);
                }
            }
        });
    }

    public void signup(String name, String username, String email, String phoneno, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    assert firebaseUser.getUid() != null;
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseUser = firebaseAuth.getCurrentUser();
                    String riderid = firebaseUser.getUid();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("userid", riderid);
                    hashMap.put("name", name);
                    hashMap.put("username", username);
                    hashMap.put("email", email);
                    hashMap.put("phoneno", phoneno);
                    hashMap.put("password", password);
                    hashMap.put("parentid", userId);

                    userRef = FirebaseDatabase.getInstance().getReference("Rider").child(riderid);
                    userRef.setValue(hashMap);

                    reference.child(riderid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddUserActivity.this, "User added Successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddUserActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(AddUserActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
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
                Intent intent6 = new Intent(AddUserActivity.this, ParentDashboard.class);
                startActivity(intent6);
                break;
            case R.id.nav_notifications:
                Intent intent = new Intent(AddUserActivity.this, NotificationUserActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_log:
                Intent intent2 = new Intent(AddUserActivity.this, RiderLogViewActivity.class);
                startActivity(intent2);
                break;

            case R.id.nav_trace:
                Intent intent4 = new Intent(AddUserActivity.this, RiderListView.class);
                startActivity(intent4);
                break;

            case R.id.add_user:
                break;

            case R.id.see_report:
                Intent intent7 = new Intent(AddUserActivity.this, BarGraph.class);
                startActivity(intent7);
                break;

            case R.id.nav_profile:
                Intent intent3 = new Intent(AddUserActivity.this, ParentProfileActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent5 = new Intent(AddUserActivity.this, UserSelect.class);
                startActivity(intent5);
                finish();
                Toast.makeText(AddUserActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
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