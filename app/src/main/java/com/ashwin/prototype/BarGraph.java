package com.ashwin.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashwin.prototype.modelclass.CountViolation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BarGraph extends AppCompatActivity {

    List<CountViolation> userlist = new ArrayList<>();
    DatabaseReference RootRef, mPostReference;
    RecyclerView recyclerView;
    ListView listViewArtists;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Query postQuery, query;
    ValueEventListener valueEventListener;

    private String countSpeed;
    String rRider, userID, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);

        listViewArtists = (ListView) findViewById(R.id.listViewArtists);

       /* recyclerView = (RecyclerView) findViewById(R.id.recyclerViewRider);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);*/

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference().child("SpeedCount");
        String userId = firebaseUser.getUid();
        postQuery =RootRef.child(userId);
        userlist = new ArrayList<>();


        BarChart barChart = findViewById(R.id.bargraph);
       mPostReference = FirebaseDatabase.getInstance().getReference().child("SpeedCount");
        query = mPostReference.orderByChild("parentID").equalTo(userId);

        query.addValueEventListener(valueEventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> visitors = new ArrayList<>();

                float i = 0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    i = i+1;
                    countSpeed = ds.child("violation_count").getValue().toString();
                    int counter = Integer.parseInt(countSpeed);
                    name = String.valueOf(ds.child("name").getValue());
                    visitors.add(new BarEntry(i, counter));
                    ArrayList<BarDataSet> riders = new ArrayList<>();
                    BarDataSet barDataSet = new BarDataSet(visitors, "Riders");
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);


                    BarData barData = new BarData(barDataSet);

                    barChart.setFitBars(true);
                    barChart.setData(barData);
                    barChart.getDescription().setText("Comparing Speed Violation");
                    barChart.animateY(2000);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userlist.clear();
                for(DataSnapshot postsnapshot : snapshot.getChildren()){
                    CountViolation violation = postsnapshot.getValue(CountViolation.class);
                    userlist.add(violation);
                }

                UserSpeedCountList adapter = new UserSpeedCountList(BarGraph.this, userlist);
                listViewArtists.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}