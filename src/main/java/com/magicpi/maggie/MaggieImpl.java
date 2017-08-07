package com.magicpi.maggie;

import com.magicpi.maggie.model.Dictionary;
import com.magicpi.maggie.model.Response;
import com.magicpi.maggie.model.Unit;
import com.magicpi.maggie.utils.SimilarityHelper;
import io.indico.api.utils.IndicoException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class MaggieImpl implements Maggie {

    private SimilarityHelper helper;
    private Double similarityLevelOne;
    private Double similarityLevelTwo;

    private Dictionary currentDict;
    private Map<String, Dictionary> dictMap = new HashMap<>();

    private Unit suggestedUnit;


    public MaggieImpl(Double similarityLevelOne,
                      Double similarityLevelTwo,
                      SimilarityHelper helper) {

        this.similarityLevelOne = similarityLevelOne;
        this.similarityLevelTwo = similarityLevelTwo;
        this.helper = helper;
    }


    @Override
    public Response findAnswer(String text) {

        if (currentDict == null) {
            return Response.Error("No Dictionary Found!");
        }

        try {
            List<Double> textFeature = helper.findStringFeature(text).get(text);
            System.out.println("text feature: " + textFeature);

            //create a new unit to store detail about input text
            Unit newUnit = new Unit();
            newUnit.setText(text);
            newUnit.setFeature(textFeature);

            Map<Unit, Double> similarityMap = new HashMap<>();
            System.out.println("Comparing with dictionary...");
            for (Unit unit : currentDict.getUnits()) {
                Double similarity = helper
                        .findSimilarity(textFeature, unit.getFeature());
                similarityMap.put(unit, similarity);
                System.out.println("Similarity: " + similarity);
            }

            Map<Unit, Double> sortedResultMap = sortByValue(similarityMap);
            Iterator iterator = sortedResultMap.entrySet().iterator();

            //get highest similarity result
            Map.Entry<Unit, Double> firstResult = (Map.Entry<Unit, Double>) iterator.next();
            System.out.println("Got highest result: " + firstResult.getKey().getText());
            System.out.println("Got highest result: " + firstResult.getValue());

            //return result when similarity is greater than set level one
            if (firstResult.getValue() > similarityLevelOne) {
                System.out.println("Returning answer: " + firstResult.getKey().getText());
                return Response.Answer(firstResult.getKey().getRef());
            }

            //if no string has similarity greater than set level one
            //do fuzzy search on strings have similarity greater than set level two
            else if (firstResult.getValue() < similarityLevelOne &&
                    firstResult.getValue() > similarityLevelTwo) {

                System.out.println("can't find result has similarity >75% ...");
                System.out.println("doing fuzzy search...");

                Double highestSimilarity = 0d;
                Unit bestResult = firstResult.getKey();
                while (iterator.hasNext()) {
                    Map.Entry<Unit, Double> result =
                            ((Map.Entry<Unit, Double>) iterator.next());

                    if (result.getValue() > similarityLevelTwo) {

                        Double fuzzySimilarity = helper
                                .findSimilarity(text, result.getKey().getText());

                        if (fuzzySimilarity > highestSimilarity) {
                            highestSimilarity = fuzzySimilarity;
                            bestResult = result.getKey();
                        }
                    }
                }
                newUnit.setRef(bestResult.getRef());
                //cache the question reference and give a suggestion response
                suggestedUnit = newUnit;
                return Response.Suggestion(bestResult.getRef());
            }

        } catch (IOException | IndicoException e) {
            e.printStackTrace();
        }

        //if no string has similarity greater than set level two
        return Response.Error();
    }

    @Override
    public Response answerSuggestion(boolean isRight) {

        if (isRight) {

            Unit unit = new Unit(
                    suggestedUnit.getRef(),
                    suggestedUnit.getText(),
                    suggestedUnit.getFeature());

            suggestedUnit = null;

            //add cached question to dictionary
            currentDict.addUnit(unit);

            return Response.Answer(unit.getRef());
        }

        return Response.Error();
    }


    @Override
    public Dictionary generateDictionary(String id, Map<String, String> textRefMap) {

        Dictionary dictionary = new Dictionary(id);

        try {
            List<Unit> units = new ArrayList<>();

            //get texts from the textRefMap and convert to an array
            Set<String> texts = textRefMap.keySet();
            String[] textArray = texts.toArray(new String[texts.size()]);

            //generate text and feature map
            HashMap<String, List<Double>> textFeaturesMap = helper.findStringsFeature(textArray);

            //create units with ref, text and its feature
            for (String text : textArray) {

                String ref = textRefMap.get(text);
                List<Double> feature = textFeaturesMap.get(text);

                units.add(new Unit(ref, text, feature));
            }

            dictionary.setUnits(units);

        } catch (IOException | IndicoException e) {
            e.printStackTrace();
        }
        return dictionary;
    }


    @Override
    public boolean setDictionary(Dictionary dictionary) {

        if (dictMap.containsKey(dictionary.getId())) {
            dictMap.remove(dictionary.getId());
            dictMap.put(dictionary.getId(), dictionary);
        } else {
            dictMap.put(dictionary.getId(), dictionary);
        }

        currentDict = dictionary;

        return true;

    }


    @Override
    public Dictionary getDictionary(String id) {
        return dictMap.get(id);
    }


    @Override
    public boolean changeDictionary(String id) {

        if (dictMap.containsKey(id)) {
            currentDict = dictMap.get(id);
            return true;
        }

        return false;
    }


    //this method for java 1.8 only
    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

//    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
//        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
//        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
//            @Override
//            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
//                return (o2.getValue()).compareTo(o1.getValue());
//            }
//        });
//
//        Map<K, V> result = new LinkedHashMap<>();
//        for (Map.Entry<K, V> entry : list) {
//            result.put(entry.getKey(), entry.getValue());
//        }
//        return result;
//    }
}
