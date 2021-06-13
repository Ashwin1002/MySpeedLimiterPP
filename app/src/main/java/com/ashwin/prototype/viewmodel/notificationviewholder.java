package com.ashwin.prototype.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashwin.prototype.R;

public class notificationviewholder extends RecyclerView.ViewHolder
{
    TextView userName, userStatus;

    public notificationviewholder(@NonNull View itemView)
    {
        super(itemView);

        userName = itemView.findViewById(R.id.user_name);
        userStatus = itemView.findViewById(R.id.user_notification);
    }

    public void imageView(Application application, String Email, String notification)
    {
        try {
            userName.setText(Email);
            userStatus.setText(notification);

        }catch (Exception ex)
        {
            Log.d("Imageview crashed", ex.getMessage().toString());
        }
    }

}
