package com.mngh.tuanvn.fbvideodownloader.Model;

import android.graphics.Bitmap;

import com.mngh.tuanvn.fbvideodownloader.R;

/**
 * Created by KAMAL OLI on 12/08/2017.
 */

public class VideoModel {
    private String url;
    private String name;
    private int id;
    private int resourceId;
    private Bitmap imageBitmap;
    public void setImageBitmap(Bitmap bitmap){
        imageBitmap=bitmap;
    }
    public Bitmap getImageBitmap(){
        return imageBitmap;
    }
    public void setResourceId(int id){
        resourceId= R.drawable.grid_image;
    }
    public int getResourceId(){
        return R.drawable.grid_image;
    }
    public void setUrl(String url){
        this.url=url;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getUrl(){
        return url;
    }
    public String getName(){
        return name;
    }
    public int getId(){
        return id;
    }
}
