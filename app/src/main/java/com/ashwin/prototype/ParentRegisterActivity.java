package com.ashwin.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class ParentRegisterActivity extends AppCompatActivity {

    Button parent_login;
    EditText nameET,emailET,passwordET,phoneET, usernameET;
    Button parent_signup;

    FirebaseDatabase rootNode1;
    DatabaseReference reference1;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        firebaseAuth = FirebaseAuth.getInstance();
        rootNode1 = FirebaseDatabase.getInstance();

        parent_signup = findViewById(R.id.btnsignup);
        nameET = findViewById(R.id.nameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        phoneET = findViewById(R.id.phoneET);
        usernameET = findViewById(R.id.usernameET);


        parent_login = findViewById(R.id.navLogin);
        parent_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                Intent inte = new Intent(getApplicationContext(), ParentLoginActivity.class);
                startActivity(inte);
            }
        });

        parent_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get all the values
                String name = nameET.getText().toString();
                String username = usernameET.getText().toString();
                String email = emailET.getText().toString();
                String phoneno = phoneET.getText().toString();
                String password = passwordET.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneno) || TextUtils.isEmpty(password)) {
                    Toast.makeText(ParentRegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    signup(name, username, email, phoneno, password);
                }
            }
        });
    }

    private void signup(String name, String username, String email, String phoneno, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser rUser = firebaseAuth.getCurrentUser();
                    String userId = rUser.getUid();
                    reference1 = FirebaseDatabase.getInstance().getReference("Parent").child(userId);
//
                    assert rUser != null;
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("userId", userId);
                    hashMap.put("name", name);
                    hashMap.put("username", username);
                    hashMap.put("email", email);
                    hashMap.put("phoneno", phoneno);
                    hashMap.put("password", password);

                    reference1.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(ParentRegisterActivity.this, ParentLoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ParentRegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(ParentRegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}