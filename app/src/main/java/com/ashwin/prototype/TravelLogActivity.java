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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class TravelLogActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_travel_log);

        Intent intent = getIntent();
        /*
         * this line is important
         * this time we are not getting the reference of a direct node
         * but inside the node track we are creating a new child with the artist id
         * and inside that node we will store all the tracks with unique ids
         * */


        TriprecyclerView = (RecyclerView) findViewById(R.id.travelrecyclerView);
        TriprecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        TriprecyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userid = firebaseUser.getUid();


        Log.d("com.ashwin.prototype", "Firebase userid: " + userid);
        //Toast.makeText(TravelLogActivity.this, "userid: "+userid, Toast.LENGTH_SHORT).show();
        RootRef = FirebaseDatabase.getInstance().getReference().child("TravelLog");

        postQuery = RootRef.child(userid);
        speedlist = new ArrayList<>();


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

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_log_id = getRef(position).getKey();
                                String trip_name = model.getTripT();
                                Intent logintent = new Intent(TravelLogActivity.this, TravelLogEdit.class);
                                logintent.putExtra("visit_log_id", visit_log_id);
                                logintent.putExtra("trip_name", trip_name);
                                startActivity(logintent);
                            }
                        });



                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_view_item, viewGroup, false);
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




        }

        public void setTriptitle(String triptitle) {
            this.triptitle.setText(triptitle);
        }

        public void setTripDes(String TripDes) {
            this.TripDes.setText(TripDes);
        }
    }
}