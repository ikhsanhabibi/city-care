package com.example.citycare.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.citycare.R;
import com.example.citycare.model.Form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ComplaintsViewHolder> {

    Activity context;
    ArrayList<Form> complaintsArrayList;
    ImageView picture;

    public ComplaintsAdapter(Activity context, ArrayList<Form> complaintsArrayList) {
        this.context = context;
        this.complaintsArrayList = complaintsArrayList;
    }

    @NonNull
    @Override
    public ComplaintsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_reports, parent, false);
        ComplaintsViewHolder complaintsAdapter = new ComplaintsViewHolder(view);
        return complaintsAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintsViewHolder holder, int position) {
        Form form = complaintsArrayList.get(position);
        holder.textViewCategory.setText(form.getCategory());
        holder.textViewStatus.setText(form.getStatus());
        holder.textViewDescription.setText(form.getDescription());
        holder.textViewLocation.setText(form.getLocation());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(form.getTimestamp().getTime());
        String strDate = dateFormat.format(date);
        holder.textViewDate.setText(strDate);

        Glide.with(context).load(form.getImageUrl()).into(picture);
    }

    @Override
    public int getItemCount() {
        return complaintsArrayList.size();
    }

    public class ComplaintsViewHolder extends RecyclerView.ViewHolder {

        TextView textViewLocation, textViewCategory, textViewDescription, textViewStatus, textViewDate;

        public ComplaintsViewHolder(View itemView) {
            super(itemView);
            textViewLocation = itemView.findViewById(R.id.itemLocation);
            textViewCategory = itemView.findViewById(R.id.itemCategory);
            textViewDescription = itemView.findViewById(R.id.itemDescription);
            textViewStatus = itemView.findViewById(R.id.itemStatus);
            textViewDate = itemView.findViewById(R.id.itemDate);
            picture = itemView.findViewById(R.id.picture);
        }
    }
}
