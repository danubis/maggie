package com.magicpi.maggie.utils;


import io.indico.Indico;
import io.indico.api.utils.IndicoException;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class SimilarityHelper {

    private Indico indico;

    public SimilarityHelper(Indico indico) {
        this.indico = indico;
    }

    //this method will return a map contains one item
    public HashMap<String, List<Double>> findStringFeature(String text)
            throws IOException, IndicoException {

        if (indico == null) {
            throw new NullPointerException("indico must not be null");
        }

        HashMap<String, List<Double>> textFeatureMap = new HashMap<>();
        textFeatureMap.put(text, indico.textFeatures.predict(text).getTextFeatures());

        return textFeatureMap;
    }


    //this method will return a map contains multiple item
    public HashMap<String, List<Double>> findStringsFeature(String[] texts)
            throws IOException, IndicoException {

        if (indico == null) {
            throw new NullPointerException("indico must not be null");
        }

        HashMap<String, List<Double>> textFeatureMap = new HashMap<>();
        List<List<Double>> textsFeatures = indico.textFeatures.predict(texts).getTextFeatures();
        for (int i = 0; i < texts.length; i++) {
            textFeatureMap.put(texts[i], textsFeatures.get(i));
        }

        return textFeatureMap;
    }


    //find similarity by calculating cosine distance of high dimension vectors
    public double findSimilarity(
            final List<Double> vectors1,
            final List<Double> vectors2) {

        if (vectors1 == null || vectors1.isEmpty()) {
            throw new NullPointerException("s1 must not be null");
        }

        if (vectors2 == null || vectors2.isEmpty()) {
            throw new NullPointerException("s2 must not be null");
        }

        if (vectors1.equals(vectors2)) {
            return 1;
        }

        return dotProduct(vectors1, vectors2)
                / (norm(vectors1) * norm(vectors2));
    }

    //find similarity by fuzzy search
    public double findSimilarity(String text1, String text2) {
        return FuzzySearch.tokenSortPartialRatio(text1, text2) / 100;
    }

    private double norm(final List<Double> vectors) {
        double agg = 0;

        for (Double vector : vectors) {
            agg += 1.0 * vector * vector;
        }

        return Math.sqrt(agg);
    }

    private double dotProduct(
            final List<Double> vectors1,
            final List<Double> vectors2) {

        // Loop over the smallest map
        List<Double> small_profile = vectors2;
        List<Double> large_profile = vectors1;
        if (vectors1.size() < vectors2.size()) {
            small_profile = vectors1;
            large_profile = vectors2;
        }

        double agg = 0;

        for (int i = 0; i < small_profile.size(); i++) {
            agg += 1.0 * small_profile.get(i) * large_profile.get(i);
        }

        return agg;
    }
}
