package com.ashwin.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class AddUserActivity extends AppCompatActivity {


    EditText nameEdText, emailEdText, passwordEdText, phoneEdText, usernameEdText;
    Button btn_Signup;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;
    FirebaseUser  firebaseUser;
    TextView text_sign_title;

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
        String userId = firebaseUser.getUid();

//                Intent intent = getIntent();
//                String parentid = intent.getExtras().getString("parentId");
        Log.d("com.ashwin.prototype", "Uid: " + userId );
//                text_sign_title.setText(parentid);



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
}