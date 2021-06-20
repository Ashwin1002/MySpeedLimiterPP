package com.ashwin.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TravelLogEdit extends AppCompatActivity {

    FirebaseDatabase rootNode;
    FirebaseAuth firebaseAuth;
    DatabaseReference savereference, dbRef;
    Button update, cancel;
    private String receivetriptitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_log_edit);
        EditText tripTitle = (EditText) findViewById(R.id.tripTitle);
        EditText tripDes = (EditText) findViewById(R.id.tripDes);

        update = (Button) findViewById(R.id.btn_update);
        cancel = (Button) findViewById(R.id.btn_cancel1);

        final TextView txtdistance1 = (TextView) findViewById(R.id.txtdistance1);
        final TextView tvMaxSpeed1 = (TextView) findViewById(R.id.tvMaxSpeed2);
        final TextView tvAvgSpeed1 = (TextView) findViewById(R.id.tvAvgSpeed3);
        final TextView tvOverSpeed1 = (TextView) findViewById(R.id.tvOverSpeed4);


        rootNode = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser rUser = firebaseAuth.getCurrentUser();
        String userId = rUser.getUid();

        receivetriptitle = getIntent().getExtras().get("trip_name").toString();
        savereference  = rootNode.getReference("TravelLog").child(userId);

        String key = savereference.push().getKey();

        //Retrieving data from firbase
        savereference.orderByChild("tripT").equalTo(receivetriptitle).
        addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                    String key = ds.getKey();
                    dbRef = savereference.child(key);
                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String title = String.valueOf(snapshot.child("tripT").getValue());
                            tripTitle.setText(title);
                            Log.d("com.ashwin.prototype", "key: " + title);
                            String desc = snapshot.child("tripD").getValue().toString();
                            tripDes.setText(desc);
                            String dist = snapshot.child("dist").getValue().toString();
                            txtdistance1.setText(dist);
                            String avg = snapshot.child("avg").getValue().toString();
                            tvAvgSpeed1.setText(avg);
                            String max = snapshot.child("max").getValue().toString();
                            tvMaxSpeed1.setText(max);
                            String loc = snapshot.child("loc").getValue().toString();
                            tvOverSpeed1.setText(loc);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Log.d("com.ashwin.prototype", "key: " + key);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Updating data from firebase

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title1 = tripTitle.getText().toString();
                String des1 = tripDes.getText().toString();
                HashMap hashMap = new HashMap();
                hashMap.put("tripT", title1);
                hashMap.put("tripD", des1);

                dbRef.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(TravelLogEdit.this, "Data updated Successfully!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for (DataSnapshot userSnapshot : snapshot.getChildren()){
                           userSnapshot.getRef().removeValue();
                           Toast.makeText(TravelLogEdit.this, "Log Removed Successfully!", Toast.LENGTH_SHORT).show();
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
                Intent intent = new Intent(TravelLogEdit.this, TravelLogActivity.class);
                startActivity(intent);
            }
        });

    }

}