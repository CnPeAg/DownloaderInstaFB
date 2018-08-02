package com.mngh.tuanvn.facebookvideodownloader.Model;

import com.google.gson.annotations.SerializedName;

public class Get {
    @SerializedName("delayAds")
    private String DELAY;

    @SerializedName("percentAds")
    private String percentAds;

    public Get() {
    }

    public String getDELAY() {
        return DELAY;
    }

    public String getPercentAds() {
        return percentAds;
    }
}
