package com.ashwin.prototype;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ashwin.prototype.modelclass.CountViolation;

import java.util.List;

public class UserSpeedCountList extends ArrayAdapter<CountViolation> {
    private Activity context;
    List<CountViolation> artists;
    public UserSpeedCountList(Activity context, List<CountViolation> artists) {
        super(context, R.layout.bargraphlegendlist, artists);
        this.context = context;
        this.artists = artists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.bargraphlegendlist, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.nameTV2);
        TextView textViewGenre = (TextView) listViewItem.findViewById(R.id.idTV2);

        CountViolation artist = artists.get(position);
        textViewName.setText(artist.getName());
        textViewGenre.setText(artist.getViolation_count());

        return listViewItem;
    }

}
