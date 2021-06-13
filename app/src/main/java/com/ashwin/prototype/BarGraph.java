package com.ashwin.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashwin.prototype.modelclass.CountViolation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
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

public class BarGraph extends AppCompatActivity {


    ArrayList<OverSpeedHelperClass> yData;
    DatabaseReference mPostReference, userref;
    ValueEventListener valueEventListener;
    private String countSpeed;
    String rRider, userID, name;
    Query query;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);

        BarChart barChart = findViewById(R.id.bargraph);
        recyclerView = findViewById(R.id.barlist);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);




        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rRider = firebaseUser.getUid();


        mPostReference = FirebaseDatabase.getInstance().getReference().child("SpeedCount");
        query = mPostReference.orderByChild("parentID").equalTo(rRider);


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
                    BarDataSet barDataSet = new BarDataSet(visitors, name);
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);


                    BarData barData = new BarData(barDataSet);

                    barChart.setFitBars(true);
                    barChart.setData(barData);
                    barChart.getDescription().setText("Comparing Speed Violation");
                    barChart.animateY(2000);


                    FirebaseRecyclerOptions<CountViolation> options =
                            new FirebaseRecyclerOptions.Builder<CountViolation>()
                                    .setQuery(query, CountViolation.class)
                                    .build();

                    FirebaseRecyclerAdapter<CountViolation, FindFriendViewHolder> adapter =
                            new FirebaseRecyclerAdapter<CountViolation, FindFriendViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull CountViolation model) {

                                    holder.setNametv(model.getName());
                                    holder.setIdtv(model.getViolation_count());



                                }

                                @NonNull
                                @Override
                                public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bargraphlegendlist, viewGroup, false);
                                    FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                                    return viewHolder;
                                }
                            };

                    recyclerView.setAdapter(adapter);

                    adapter.startListening();


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

    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        public TextView nametv, idtv;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            nametv = itemView.findViewById(R.id.nameTV);
            idtv = itemView.findViewById(R.id.idTV);
        }
        public void setNametv(String nametv){
            this.nametv.setText(nametv);
        }
        public void setIdtv(String idtv) {
            this.idtv.setText(idtv);
        }
    }
}