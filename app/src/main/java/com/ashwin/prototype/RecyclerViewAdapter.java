package com.ashwin.prototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    Context context;
    List<SpeedDetailClass> sppedlist;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    public RecyclerViewAdapter(Context context, List<SpeedDetailClass> list) {
        this.context = context;
        this.sppedlist = list;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_item, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new MyViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return sppedlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView triptitle, TripDes, Tripdits, Tripmax, Tripavg, Triploc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            triptitle = itemView.findViewById(R.id.LogTripTV);
            TripDes = itemView.findViewById(R.id.LogTripDesTV);


        }
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.MyViewHolder holder, int position) {

        SpeedDetailClass detailClass = sppedlist.get(position);
        holder.triptitle.setText(detailClass.getTripT());
        holder.TripDes.setText(detailClass.getTripD());




    }
}