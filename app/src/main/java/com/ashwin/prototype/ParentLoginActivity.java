package com.ashwin.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ParentLoginActivity extends AppCompatActivity {

    Button navRegister;
    EditText txtLoginEmail,txtLoginPassword;
    Button customer_login;
    private FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        navRegister = findViewById(R.id.navRegister);
        txtLoginEmail = findViewById(R.id.txtLoginEmail);
        txtLoginPassword = findViewById(R.id.txtLoginPassword);
        customer_login = findViewById(R.id.customer_login);

        progressDialog = new ProgressDialog(ParentLoginActivity.this);
        progressDialog.setMessage("Please wait");
        firebaseAuth = FirebaseAuth.getInstance();
        navRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                Intent i = new Intent(getApplicationContext(), ParentRegisterActivity.class);
                startActivity(i);
            }
        });

        customer_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setCancelable(false);
                progressDialog.show();
                final String userEnteredUsername = txtLoginEmail.getText().toString().trim();
                final String userEnteredPassword = txtLoginPassword.getText().toString().trim();
                if (txtLoginEmail.getText().toString().equalsIgnoreCase("")) {
                    txtLoginEmail.setError("Enter your Username");
                } else if(txtLoginPassword.getText().toString().equalsIgnoreCase("")) {
                    txtLoginPassword.setError("Enter your password");
                }else {

                    login(userEnteredUsername, userEnteredPassword);

                }
            }
        });
    }

    private void login(String userEnteredUsername, String userEnteredPassword) {
        firebaseAuth.signInWithEmailAndPassword(userEnteredUsername, userEnteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Intent intent = new Intent(ParentLoginActivity.this, ParentDashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(ParentLoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(ParentLoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void displayMessage(@NonNull String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //directly redirect to mainactivity
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),ParentDashboard.class));
            finish();
        }
    }
}