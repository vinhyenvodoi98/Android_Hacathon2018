package com.example.hanh.ava_android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Instruct {
    @SerializedName("step")
    @Expose
    private List<Step> step = null;

    public List<Step> getStep() {
        return step;
    }

    public void setStep(List<Step> step) {
        this.step = step;
    }
}
