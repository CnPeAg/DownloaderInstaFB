package com.mngh.tuanvn.fbvideodownloader.Model;

import com.google.gson.annotations.SerializedName;

public class AdsConfig
{
    @SerializedName("delayService")
    public int delayService;
    @SerializedName("idFullService")
    public String idFullService;
    @SerializedName("intervalService")
    public int intervalService;
    @SerializedName("delay_retention")
    public int delay_retention;
    @SerializedName("retention")
    public int retention;
    @SerializedName("delay_report")
    public int delay_report;
    @SerializedName("idFullFbService")
    public String idFullFbService;
}
