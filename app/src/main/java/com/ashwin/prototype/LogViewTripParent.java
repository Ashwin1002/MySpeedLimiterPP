package com.ashwin.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class LogViewTripParent extends AppCompatActivity {

    List<SpeedDetailClass> speedlist = new ArrayList<>();
    //    RecyclerViewAdapter adapter;
    DatabaseReference RootRef, UserRef;
    RecyclerView TriprecyclerView;
    private String receiverUserID;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Query postQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_view_trip_parent);

        Intent intent = getIntent();
        /*
         * this line is important
         * this time we are not getting the reference of a direct node
         * but inside the node track we are creating a new child with the artist id
         * and inside that node we will store all the tracks with unique ids
         * */


        TriprecyclerView = (RecyclerView) findViewById(R.id.logrecyclerView);
        TriprecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        TriprecyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userid = firebaseUser.getUid();
        receiverUserID = getIntent().getExtras().get("visit_log_id").toString();



        Log.d("com.ashwin.prototype", "Firebase userid: " +userid);
        //Toast.makeText(TravelLogActivity.this, "userid: "+userid, Toast.LENGTH_SHORT).show();
        RootRef = FirebaseDatabase.getInstance().getReference().child("TravelLog");

        postQuery = RootRef.child(receiverUserID);
        speedlist = new ArrayList<>();
;
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<SpeedDetailClass> options =
                new FirebaseRecyclerOptions.Builder<SpeedDetailClass>()
                        .setQuery(postQuery, SpeedDetailClass.class)
                        .build();

        FirebaseRecyclerAdapter<SpeedDetailClass, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<SpeedDetailClass, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull SpeedDetailClass model) {

                        holder.setTriptitle(model.getTripT());
                        holder.setTripDes(model.getTripD());
                        holder.setTripdits(model.getDist());
                        holder.setTripmax(model.getMax());
                        holder.setTripavg(model.getAvg());
                        holder.setTriploc(model.getLoc());



                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.parent_singleview_log, viewGroup, false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;
                    }
                };

        TriprecyclerView.setAdapter(adapter);

        adapter.startListening();


    }


    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        public TextView triptitle, TripDes, Tripdits, Tripmax, Tripavg, Triploc;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            triptitle = itemView.findViewById(R.id.LogTripTV);
            TripDes = itemView.findViewById(R.id.LogTripDesTV);
            Tripdits = itemView.findViewById(R.id.txtdistance1);
            Tripmax = itemView.findViewById(R.id.tvMaxSpeed2);
            Tripavg = itemView.findViewById(R.id.tvAvgSpeed3);
            Triploc = itemView.findViewById(R.id.tvOverSpeed4);



        }
        public void setTriptitle(String triptitle){
            this.triptitle.setText(triptitle);
        }
        public void setTripDes(String TripDes) {
            this.TripDes.setText(TripDes);
        }
        public void setTripdits(String tripdits){
            this.Tripdits.setText(tripdits);
        }
        public void setTripmax(String tripmax){
            this.Tripmax.setText(tripmax);
        }
        public void  setTripavg(String tripavg){
            this.Tripavg.setText(tripavg);
        }
        public void setTriploc(String triploc){
            this.Triploc.setText(triploc);
        }
    }
}