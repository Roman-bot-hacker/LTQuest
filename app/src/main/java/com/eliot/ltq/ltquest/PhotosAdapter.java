package com.eliot.ltq.ltquest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private List<String> pathList;
    private Context activityContext;
    private FirebaseStorageManager storageManager = new FirebaseStorageManager();

    public  static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoView;
        public ViewHolder(ImageView photoView){
            super(photoView);
            this.photoView = photoView;
        }
    }

    public PhotosAdapter(Context context, List<String> pathList){
        this.pathList = pathList;
        this.activityContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);

        ViewHolder vh = new ViewHolder(imageView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        storageManager.setImageIntoImageView(activityContext, pathList.get(position), holder.photoView);
        holder.photoView.setAdjustViewBounds(true);
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

}
