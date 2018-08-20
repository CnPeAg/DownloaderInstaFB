package com.mngh.tuanvn.fbvideodownloader.Controllers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mngh.tuanvn.fbvideodownloader.Model.VideoModel;
import com.mngh.tuanvn.fbvideodownloader.R;
import com.mngh.tuanvn.fbvideodownloader.VideoPlayerActivity;

import java.util.ArrayList;

/**
 * Created by KAMAL OLI on 12/08/2017.
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

    class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
             PopupMenu.OnMenuItemClickListener {//View.OnCreateContextMenuListener,
        PopupMenu popupMenu;
        ImageView videoThumbnail;
        TextView name;

        private GridViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            name = itemView.findViewById(R.id.video_name);
//            itemView.setOnCreateContextMenuListener(this);
        }

        private void setViews(VideoModel model) {
            videoThumbnail.setImageBitmap(model.getImageBitmap());
            name.setText(model.getName());
        }

        @Override
        public void onClick(View view) {
            popupMenu = new PopupMenu(context, view);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.inflate(R.menu.video_context_menu);
            popupMenu.show();

            Log.e("ViewId", view.getId() + "");
            VideoModel model = dataList.get(getAdapterPosition());
            Log.e("ArrayList index", model.getName());
            //context.startActivity(intent);
        }

//        @Override
//        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//            contextMenu.setHeaderTitle("Select The Action");
//            contextMenu.add(0, view.getId(), 0, "Call");
//            contextMenu.add(0, view.getId(), 0, "SMS");
//        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.play:
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("videoUrl", dataList.get(getAdapterPosition()).getUrl());
                    context.startActivity(intent);
                    return true;
                default:
                    return false;
                case R.id.cancel:
                    popupMenu.dismiss();
                    return true;
                case R.id.share:
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("video/*");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Title");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM,
                            Uri.parse(dataList.get(getAdapterPosition()).getUrl()));
                    context.startActivity(Intent.createChooser(sharingIntent, "share:"));
                    return true;
                case R.id.delete:
                    int position = getAdapterPosition();
                    Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, dataList.
                            get(position).getId());
                    ContentResolver resolver = context.getContentResolver();
                    resolver.delete(uri, null, null);
                    dataList.remove(position);
                    notifyItemRemoved(position);
                    return true;
//                case R.id.convert:
//                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(dataList.get(getAdapterPosition()).getUrl()));
//                    i.setDataAndType(Uri.parse(dataList.get(getAdapterPosition()).getUrl()), "video/*");
//                    context.startActivity(i);
//                    return true;
            }
        }
    }
}
