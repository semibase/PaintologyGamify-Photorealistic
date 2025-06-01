package com.paintology.lite.photorealistic.drawing.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * <p>
 * Copyright (c) 2021 <ClientName>. All rights reserved.
 * Created by mohammadarshikhan on 3/4/21.
 */
public class DeviceType {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
