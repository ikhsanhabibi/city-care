package com.example.citycare.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.citycare.R;
import com.example.citycare.model.Complaint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

    Activity context;
    ArrayList<Complaint> notificationsArrayList;

    public NotificationsAdapter(Activity context, ArrayList<Complaint> notificationsArrayList) {
        this.context = context;
        this.notificationsArrayList = notificationsArrayList;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_notifications, parent, false);
        NotificationsViewHolder notificationsAdapter = new NotificationsViewHolder(view);
        return notificationsAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        Complaint complaint = notificationsArrayList.get(position);

        String str = holder.itemView.getContext().getString(R.string.your_complaint_about) + " " + complaint.getCategory().toLowerCase() + " " + holder.itemView.getContext().getString(R.string.in) + " " + complaint.getLocation() + " " + holder.itemView.getContext().getString(R.string.new_status) + " <i><b>" + complaint.getStatus() + "</b></i>.";
        holder.textViewNotification.setText(Html.fromHtml(str));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date(complaint.getTimestamp().getTime());
        String strDate = dateFormat.format(date);
        holder.textViewDate.setText(strDate);
    }

    @Override
    public int getItemCount() {
        return notificationsArrayList.size();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNotification, textViewDate;

        public NotificationsViewHolder(View itemView) {
            super(itemView);
            textViewNotification = itemView.findViewById(R.id.notification);
            textViewDate = itemView.findViewById(R.id.itemDate);
        }
    }
}
