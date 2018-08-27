package com.mngh.tuanvn.fbvideodownloader.Controllers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.mngh.tuanvn.fbvideodownloader.Model.VideoModel;
import com.mngh.tuanvn.fbvideodownloader.R;
import com.mngh.tuanvn.fbvideodownloader.VideoPlayerActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tuancon on 12/08/2017.
 */

public class VideoFilesAdapters extends RecyclerView.Adapter<VideoFilesAdapters.GridViewHolder> {
    private Context context;
    private ArrayList<VideoModel> dataList;
    private LayoutInflater inflater;

    public VideoFilesAdapters(Context context, ArrayList<VideoModel> dataList) {
        this.context = context;
        this.dataList = dataList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    @NonNull
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_view_of_video, parent, false);

        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        VideoModel model = dataList.get(position);
        holder.setViews(model);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder {

        ImageView videoThumbnail;
        ImageButton btnPlay, btnShare, btnDelete;

        private GridViewHolder(View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   try{
                       Intent intent = new Intent(context, VideoPlayerActivity.class);
                       intent.putExtra("videoUrl", dataList.get(getAdapterPosition()).getUrl());
                       context.startActivity(intent);
                   }catch (Exception e){}
                }
            });
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, dataList.
                            get(position).getId());
                    ContentResolver resolver = context.getContentResolver();
                    resolver.delete(uri, null, null);
                    dataList.remove(position);
                    notifyItemRemoved(position);
                }
            });
            btnShare = itemView.findViewById(R.id.btnShare);
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   try {
                       Intent sendIntent = new Intent(Intent.ACTION_SEND);
                       sendIntent.setType("video/*");
                       File file = new File(dataList.get(getAdapterPosition()).getUrl());
                       sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Video");
                       sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                       sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the Video");
                       context.startActivity(Intent.createChooser(sendIntent, "Share via:"));
                   }catch (Exception e){}
                }
            });
        }

        private void setViews(VideoModel model) {
            videoThumbnail.setImageBitmap(model.getImageBitmap());
        }
    }
}
