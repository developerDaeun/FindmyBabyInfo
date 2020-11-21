package org.tensorflow.lite.examples.detection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CustomViewHolder> {

    private ArrayList<String> dataList;

    public RecyclerAdapter(ArrayList<String> dataList) {
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
        Bitmap bitmap=loadImageFromStorage(dataList.get(position));
        holder.imageView.setImageBitmap(bitmap);
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

    //path에서 img를 불러오는 함수
    private Bitmap loadImageFromStorage(String path)
    {
        Bitmap b=null;
        try {
            File f=new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
    }
}
