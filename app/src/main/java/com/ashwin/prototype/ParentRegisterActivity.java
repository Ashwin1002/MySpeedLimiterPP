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

                if(!validateName() |!validatePassword() | !validatePhoneno() | !validateEmail() | !validateUserName())
                {
                    Toast.makeText(getApplicationContext(), "Please validate the form properly!", Toast.LENGTH_SHORT).show();
                    return;
                }  else {
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
//                    hashMap.put("password", password);

                    reference1.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(ParentRegisterActivity.this, ParentLoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Registered Successfully!", Toast.LENGTH_SHORT).show();
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

    private  Boolean validateName(){
        String val = nameET.getText().toString();

        if(val.isEmpty()){
            nameET.setError("Field cannot be empty");
            return  false;
        }
        else{
            nameET.setError(null);
            //nameET.setErrorEnabled(false);
            return true;
        }
    }

    private  Boolean validateUserName(){
        String val = usernameET.getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if(val.isEmpty()){
            usernameET.setError("Field cannot be empty");
            return  false;
        }
        else if(val.length()>= 15){
            usernameET.setError("Username too long");
            return  false;
        }
        else if(val.length()<= 5){
            usernameET.setError("Username too short");
            return  false;
        }
        else if(!val.matches(noWhiteSpace)){
            usernameET.setError("No white spaces allowed");
            return  false;
        }
        else{
            usernameET.setError(null);
            //usernameET.setErrorEnabled(false);
            return true;
        }
    }

    private  Boolean validateEmail(){
        String val = emailET.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(val.isEmpty()){
            emailET.setError("Field cannot be empty");
            return  false;
        }
        else if (!val.matches(emailPattern)) {
            emailET.setError("Invalid email address");
            return false;
        }
        else{
            emailET.setError(null);
            //emailET.setErrorEnabled(false);
            return true;
        }
    }

    private  Boolean validatePhoneno(){
        String val = phoneET.getText().toString();

        if(val.isEmpty()){
            phoneET.setError("Field cannot be empty");
            return  false;
        }
        else if(val.length()< 10 || val.length()>10){
            phoneET.setError("Invalid Number!!");
            return  false;
        }
        else{
            phoneET.setError(null);
            //phoneET.setErrorEnabled(false);
            return true;
        }
    }

    private  Boolean validatePassword(){
        String val = passwordET.getText().toString();
        String passwordVal = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{6,}" +               //at least 6 characters
                "$";

        if(val.isEmpty()){
            passwordET.setError("Field cannot be empty");
            return  false;
        }
        else if (!val.matches(passwordVal)) {
            passwordET.setError("Password is too weak. It must contain a uppercase, a lowercase, a number and a special character and must be at least 6 characters");
            return false;
        }
        else{
            passwordET.setError(null);
            return true;
        }

    }

}