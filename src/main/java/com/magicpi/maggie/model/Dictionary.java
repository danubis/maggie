package com.magicpi.maggie.model;


import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class Dictionary {

    private String id;
    //    private HashMap<String, List<Double>> textFeatureMap;
    private List<Unit> units;
    private boolean isUpdated = false;

    public Dictionary(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public void addUnit(Unit unit) {
        this.units.add(unit);
        isUpdated = true;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

//    public HashMap<String, List<Double>> getTextFeatureMap() {
//        return textFeatureMap;
//    }
//
//    public void setTextFeatureMap(HashMap<String, List<Double>> textFeatureMap) {
//        this.textFeatureMap = textFeatureMap;
//    }

    public void save(String path, Gson gson) throws IOException {
        FileWriter writer = new FileWriter(path);
        gson.toJson(this, writer);
        writer.close();
        isUpdated = false;
    }

    public static Dictionary load(String path) throws FileNotFoundException {

        JsonReader reader = new JsonReader(new FileReader(path));
        return new Gson().fromJson(reader, Dictionary.class);
    }
}