package com.example.citycare.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.citycare.R;

import com.example.citycare.model.Form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintsViewHolder> {

    Activity context;
    ArrayList<Form> complaintsArrayList;


    public ComplaintAdapter(Activity context, ArrayList<Form> complaintsArrayList) {
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

        // Status
        if (form.getStatus().equals("DONE")) {
            holder.textViewStatus.setText(context.getResources().getString(R.string.done));
        } else if (form.getStatus().equals("REVIEWED")) {
            holder.textViewStatus.setText(context.getResources().getString(R.string.reviewed));
        } else if (form.getStatus().equals("IN PROGRESS")) {
            holder.textViewStatus.setText(context.getResources().getString(R.string.in_progress));
        } else {
            holder.textViewStatus.setText(context.getResources().getString(R.string.sent));
        }


        holder.textViewDescription.setText(form.getDescription());
        holder.textViewLocation.setText(form.getLocation());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(form.getTimestamp().getTime());
        String strDate = dateFormat.format(date);
        holder.textViewDate.setText(strDate);

        Glide.with(context).load(form.getImageUrl()).apply(new RequestOptions()
                .fitCenter()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(Target.SIZE_ORIGINAL)).error(R.drawable.image_not_found).into(holder.picture);

    }

    @Override
    public int getItemCount() {
        return complaintsArrayList.size();
    }

    public class ComplaintsViewHolder extends RecyclerView.ViewHolder {

        TextView textViewLocation, textViewCategory, textViewDescription, textViewStatus, textViewDate;
        ImageView picture;

        public ComplaintsViewHolder(View itemView) {
            super(itemView);
            textViewLocation = itemView.findViewById(R.id.itemLocation);
            textViewCategory = itemView.findViewById(R.id.itemCategory);
            textViewDescription = itemView.findViewById(R.id.itemDescription);
            textViewStatus = itemView.findViewById(R.id.itemStatus);
            textViewDate = itemView.findViewById(R.id.itemDate);
            picture = itemView.findViewById(R.id.picture);

            picture.setOnClickListener(new View.OnClickListener() {
                private static final String TAG = "TAG";

                @Override
                public void onClick(View view) {
                    final ImagePopup imagePopup = new ImagePopup(context);
                    imagePopup.setImageOnClickClose(true);
                    imagePopup.initiatePopup(picture.getDrawable());

                    imagePopup.viewPopup();
                    Log.d(TAG, "IMAGE POP OUT");

                }
            });


        }
    }
}
