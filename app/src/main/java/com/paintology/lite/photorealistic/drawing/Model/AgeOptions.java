package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <p>
 * Copyright (c) 2021 <ClientName>. All rights reserved.
 * Created by mohammadarshikhan on 3/4/21.
 */
public class AgeOptions {

    @SerializedName("min")
    @Expose
    private Integer min;
    @SerializedName("max")
    @Expose
    private Integer max;

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }
}
