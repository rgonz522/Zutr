package com.example.zutr.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zutr.R;
import com.example.zutr.models.Resource;

import java.util.List;

public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ViewHolder> {


    public static final String TAG = "ResourceAdapter";

    List<Resource> resources;
    Context context;

    public ResourceAdapter(List<Resource> resources, Context context) {
        this.resources = resources;
        this.context = context;
    }

    @NonNull
    @Override
    public ResourceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_resource, parent, false);


        return new ResourceAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ResourceAdapter.ViewHolder holder, int position) {

        Resource resource = resources.get(position);

        holder.bind(resource);
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvSubject;

        private ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSubject = itemView.findViewById(R.id.tvSubjects);
            ivPoster = itemView.findViewById(R.id.ivPoster);

        }

        public void bind(Resource resource) {

            tvTitle.setText(resource.getTitle());
            tvDescription.setText(resource.getSubject());

            Glide.with(ivPoster).load(resource.getImageURL()).into(ivPoster);

            tvSubject.setText(resource.getSubject());

            ivPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToUrl(resource.getResrcLink());
                }
            });


        }

        private void goToUrl(String url) {
            Uri uriUrl = Uri.parse(url);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            context.startActivity(launchBrowser);
        }
    }


}
