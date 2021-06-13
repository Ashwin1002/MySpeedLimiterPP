package com.ashwin.prototype;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class RiderRegisterActivity extends AppCompatActivity {

    Button navLogin;
    EditText nameEdText, emailEdText, passwordEdText, phoneEdText, usernameEdText;
    Button btn_Signup;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        firebaseAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();


        btn_Signup = findViewById(R.id.btnsignup);
        nameEdText = findViewById(R.id.nameEdText);
        emailEdText = findViewById(R.id.emailEdText);
        passwordEdText = findViewById(R.id.passwordEdText);
        phoneEdText = findViewById(R.id.phoneEdText);
        usernameEdText = findViewById(R.id.usernameEdText);


        navLogin = findViewById(R.id.navLogin);
        navLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                Intent i = new Intent(getApplicationContext(), RiderLoginActivity.class);
                startActivity(i);
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

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneno) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RiderRegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
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
                    FirebaseUser rUser = firebaseAuth.getCurrentUser();
                    String userId = rUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Rider").child(userId);
//
                    assert rUser != null;
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("userId", userId);
                    hashMap.put("name", name);
                    hashMap.put("username", username);
                    hashMap.put("email", email);
                    hashMap.put("phoneno", phoneno);
                    hashMap.put("password", password);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RiderRegisterActivity.this, RiderLoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RiderRegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(RiderRegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}