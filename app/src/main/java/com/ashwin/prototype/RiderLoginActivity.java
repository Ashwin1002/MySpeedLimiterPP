package com.ashwin.prototype;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class RiderLoginActivity extends AppCompatActivity {

    Button navRegister;
    EditText txtLoginEmail, txtLoginPassword;
    Button customer_login;
    DatabaseReference reference1;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    TextView ForgotPass;
    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;

    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        navRegister = findViewById(R.id.navRegister);
        txtLoginEmail = findViewById(R.id.txtLoginEmail);
        txtLoginPassword = findViewById(R.id.txtLoginPassword);
        customer_login = findViewById(R.id.customer_login);
        ForgotPass = findViewById(R.id.textView_forget_password_login);
        reset_alert = new AlertDialog.Builder(this, R.style.AlertDialogStyle);

        inflater = this.getLayoutInflater();

        progressDialog = new ProgressDialog(RiderLoginActivity.this);
        progressDialog.setMessage("Please wait");
        firebaseAuth = FirebaseAuth.getInstance();
        navRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                Intent i = new Intent(getApplicationContext(), RiderRegisterActivity.class);
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
                    txtLoginEmail.setError("Email field cannot be Empty");
                    progressDialog.dismiss();
                } else if (txtLoginPassword.getText().toString().equalsIgnoreCase("")) {
                    txtLoginPassword.setError("Password field cannot be Empty");
                    progressDialog.dismiss();
                } else {
                    login(userEnteredUsername, userEnteredPassword);
                }
            }
        });

        ForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start alertdialog
                View view = inflater.inflate(R.layout.forgot_password, null);
                AlertDialog alertDialog = reset_alert.create();
                alertDialog.getWindow().setLayout(900, 400); //Controlling width and height.
                reset_alert
                        .setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //validate the email address
                                EditText email = view.findViewById(R.id.reset_email_pop);
                                if (email.getText().toString().isEmpty()) {
                                    email.setError("Email required");
                                    return;
                                }
                                //send the reset password link
                                firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RiderLoginActivity.this, "Reset link sent. Please check your email!!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RiderLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("CANCEL", null).setView(view).create().show();
            }
        });
    }


    private void login(String userEnteredUsername, String userEnteredPassword) {

        firebaseAuth.signInWithEmailAndPassword(userEnteredUsername, userEnteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(RiderLoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(RiderLoginActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RiderLoginActivity.this, "Email and Password do not Match!", Toast.LENGTH_SHORT).show();
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
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}