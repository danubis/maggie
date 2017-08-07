package com.magicpi.maggie.model;

import java.util.List;

/**
 * Created by yiluo on 19/7/17.
 */
public class Unit {

    private String ref;
    private String text;
    private List<Double> feature;

    public Unit() {
    }

    public Unit(String ref, String text, List<Double> feature) {
        this.ref = ref;
        this.text = text;
        this.feature = feature;
    }

    public String getRef() {
        return ref;
    }

    public String getText() {
        return text;
    }

    public List<Double> getFeature() {
        return feature;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFeature(List<Double> feature) {
        this.feature = feature;
    }
}