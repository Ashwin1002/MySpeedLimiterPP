package com.ashwin.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class RiderLogViewActivity extends AppCompatActivity {

    List<UserHelperClass> userlist = new ArrayList<>();
    DatabaseReference database, RootRef, UserRef;
    RecyclerView recyclerView;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Query postQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_log_view);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewRiderLog);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference().child("Rider");
        String userId = firebaseUser.getUid();
        postQuery =RootRef.child(userId);
        userlist = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<UserHelperClass> options =
                new FirebaseRecyclerOptions.Builder<UserHelperClass>()
                        .setQuery(postQuery, UserHelperClass.class)
                        .build();

        FirebaseRecyclerAdapter<UserHelperClass, MyViewHolder> adapter =
                new FirebaseRecyclerAdapter<UserHelperClass, MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull UserHelperClass model) {



                        holder.setUsername(model.getName());
                        holder.setUseremail(model.getEmail());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_log_id = getRef(position).getKey();

                                Intent logintent = new Intent(RiderLogViewActivity.this, LogViewTripParent.class);
                                logintent.putExtra("visit_log_id", visit_log_id);
                                startActivity(logintent);
                            }
                        });




                    }

                    @NonNull
                    @Override
                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rider_log_single_item, viewGroup, false);
                        MyViewHolder viewHolder = new MyViewHolder(view);
                        return viewHolder;
                    }
                };

        recyclerView.setAdapter(adapter);

        adapter.startListening();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, useremail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.nameriderTV);

            useremail = itemView.findViewById(R.id.emailriderTV);

        }

        public void setUsername(String username){
            this.username.setText(username);
        }
        public void setUseremail(String useremail) {
            this.useremail.setText(useremail);
        }
    }

}