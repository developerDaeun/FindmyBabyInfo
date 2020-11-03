package com.example.finalproject;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CustomViewHolder> {

    private ArrayList<RecyclerItem> dataList;

    public RecyclerAdapter(ArrayList<RecyclerItem> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleritem, parent, false);
        CustomViewHolder holder=new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.CustomViewHolder holder, int position) {
        holder.imageView.setImageURI(dataList.get(position).getImageUri());
    }

    @Override
    public int getItemCount() {
        return (dataList!=null ? dataList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView=(ImageView)itemView.findViewById(R.id.image);
        }
    }
}
