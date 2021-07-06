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
                    hashMap.put("role", "Rider");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RiderRegisterActivity.this, RiderLoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Registered Successfully!", Toast.LENGTH_SHORT).show();
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

    private  Boolean validateName(){
        String val = nameEdText.getText().toString();

        if(val.isEmpty()){
            nameEdText.setError("Field cannot be empty");
            return  false;
        }
        else{
            nameEdText.setError(null);
            //nameEdText.setErrorEnabled(false);
            return true;
        }
    }

    private  Boolean validateUserName(){
        String val = usernameEdText.getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if(val.isEmpty()){
            usernameEdText.setError("Field cannot be empty");
            return  false;
        }
        else if(val.length()<= 5){
            usernameEdText.setError("Username too short");
            return  false;
        }
        else if(val.length()>= 15){
            usernameEdText.setError("Username too long");
            return  false;
        }

        else{
            usernameEdText.setError(null);
            //usernameEdText.setErrorEnabled(false);
            return true;
        }
    }

    private  Boolean validateEmail(){
        String val = emailEdText.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(val.isEmpty()){
            emailEdText.setError("Field cannot be empty");
            return  false;
        }
        else if (!val.matches(emailPattern)) {
            emailEdText.setError("Invalid email address");
            return false;
        }
        else{
            emailEdText.setError(null);
            //emailEdText.setErrorEnabled(false);
            return true;
        }
    }

    private  Boolean validatePhoneno(){
        String val = phoneEdText.getText().toString();

        if(val.isEmpty()){
            phoneEdText.setError("Field cannot be empty");
            return  false;
        }
        else if(val.length()< 10 || val.length()>10){
            phoneEdText.setError("Invalid Number!!");
            return  false;
        }
        else{
            phoneEdText.setError(null);
            //phoneEdText.setErrorEnabled(false);
            return true;
        }
    }

    private  Boolean validatePassword(){
        String val = passwordEdText.getText().toString();
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
            passwordEdText.setError("Field cannot be empty");
            return  false;
        }
        else if (!val.matches(passwordVal)) {
            passwordEdText.setError("Password is too weak. It must contain a uppercase, a lowercase, a number and a special character and must be at least 6 characters");
            return false;
        }
        else{
            passwordEdText.setError(null);
            return true;
        }

    }
}