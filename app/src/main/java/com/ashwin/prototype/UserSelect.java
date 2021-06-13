package com.ashwin.prototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserSelect extends AppCompatActivity {

    Button btn_rider, btn_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // edited here
            getWindow().setStatusBarColor(Color.parseColor("#63AB62"));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        btn_rider = findViewById(R.id.btnRider);
        btn_parent = findViewById(R.id.btnParent);

        btn_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSelect.this, RiderLoginActivity.class);
                startActivity(intent);
            }
        });

        btn_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSelect.this, ParentLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}