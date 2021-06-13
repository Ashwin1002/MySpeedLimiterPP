package com.ashwin.prototype;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.strictmode.Violation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.util.HashMap;
import java.util.List;

public class Notifications extends AppCompatActivity {

    List<OverSpeedHelperClass> speedlist = new ArrayList<>();
    //    RecyclerViewAdapter adapter;
    DatabaseReference RootRef, countreference, Dataref, ctRef, ptRef;
    String parentid, receivername;
    RecyclerView TriprecyclerView;
    private String receiverUserID;
    int countSpeed =0;
    int count =0;
    ProgressBar mProgress;
    ImageButton imageButton;


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TextView countTV, smallcount;
    ImageButton imgbtn;
    Query postQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Intent intent = getIntent();
        /*
         * this line is important
         * this time we are not getting the reference of a direct node
         * but inside the node track we are creating a new child with the artist id
         * and inside that node we will store all the tracks with unique ids
         * */


        TriprecyclerView = (RecyclerView) findViewById(R.id.notification_list);
        TriprecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        TriprecyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        parentid = firebaseUser.getUid();
    
        receiverUserID = getIntent().getExtras().get("visit_log_id").toString();
        receivername = getIntent().getExtras().get("visit_log_name").toString();
                     Log.d("com.Ahswin.prowe", "email: " + receivername);
        RootRef = FirebaseDatabase.getInstance().getReference().child("SpeedCrossed");
        postQuery = RootRef.child(receiverUserID);
        speedlist = new ArrayList<>();

        countTV = findViewById(R.id.countTV);
        smallcount = findViewById(R.id.smallcounterTV);
        imgbtn = findViewById(R.id.imageButtonNot);

        ctRef = FirebaseDatabase.getInstance().getReference().child("SpeedCount").child(receiverUserID);

        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suggestion_dialog();
            }
        });


        //for progressbar
        mProgress = (ProgressBar) findViewById(R.id.simpleProgressBar);

        mProgress.setProgress(0);
        mProgress.setMax(10);
        
        countViolation();
        
    }

    private void countViolation() {
        firebaseAuth = FirebaseAuth.getInstance();
        countreference = FirebaseDatabase.getInstance().getReference().child("SpeedCrossed");
        countreference.child(receiverUserID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countSpeed = (int) snapshot.getChildrenCount();
                String counter = Integer.toString(countSpeed);
                count = Integer.parseInt(counter);
                countTV.setText(counter + " out of 100");
                smallcount.setText(counter+ "/100");
                mProgress.incrementProgressBy(count);

                Log.d("com.asjds.eowew", "count: " + mProgress);

                Dataref = FirebaseDatabase.getInstance().getReference().child("SpeedCount").child(receiverUserID);
                ptRef = FirebaseDatabase.getInstance().getReference().child("SpeedCount").child(parentid);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("userId", receiverUserID);
                hashMap.put("violation_count", counter);
                hashMap.put("parentID", parentid);
                hashMap.put("name", receivername);

                Dataref.setValue(hashMap);

                ptRef.child(receiverUserID).setValue(hashMap);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<OverSpeedHelperClass> options =
                new FirebaseRecyclerOptions.Builder<OverSpeedHelperClass>()
                        .setQuery(postQuery, OverSpeedHelperClass.class)
                        .build();

        FirebaseRecyclerAdapter<OverSpeedHelperClass, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<OverSpeedHelperClass, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull OverSpeedHelperClass model) {

                        holder.setEmail(model.getemail());
                        holder.setViolation(model.getviolation());



                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notifiaction_single_view, viewGroup, false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;
                    }
                };

        TriprecyclerView.setAdapter(adapter);

        adapter.startListening();


    }


    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        public TextView email, violation;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.user_name);
            violation = itemView.findViewById(R.id.user_notification);



        }
        public void  setEmail(String email){
            this.email.setText(email);
        }
        public void  setViolation(String violation){
            this.violation.setText(violation);
        }
    }

    private void suggestion_dialog(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(Notifications.this);
        View mView = getLayoutInflater().inflate(R.layout.sugesstion_dialog, null);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        final TextView txt_limit = (TextView) mView.findViewById(R.id.txt_limit12);
        final TextView txt_suggest = (TextView) mView.findViewById(R.id.txt_suggest);

        Button btn_cancel = (Button) mView.findViewById(R.id.btn_cancel);
        Button btn_ok = (Button) mView.findViewById(R.id.btn_ok);
        String limit = ("Your child name is: " + receivername +". Your child has violation count: "+ count);
        txt_limit.setText(limit);

        if(count >= 0 && count <= 3){
            txt_suggest.setText("Your child follows the speed rules. He is in safe zone.");
        }else if(count >= 4 && count <=6){
            txt_suggest.setText("Your child has crossed speed limit few times. He is in safe side now but if things get worse, he would be in Danger!");
        }else {
            txt_suggest.setText("Your child is overspeeding frequently!. Please tell him to ride safely!!!");
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

}