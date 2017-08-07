package com.magicpi.maggie;

import com.magicpi.maggie.model.Dictionary;
import com.magicpi.maggie.model.Response;

import java.util.Map;


public interface Maggie {

    Response findAnswer(String text);

    Response answerSuggestion(boolean isRight);

    //generate a dictionary of strings and their text feature vectors
    Dictionary generateDictionary(String id, Map<String, String> textRefMap);

    //set current dictionary for maggie to find answer
    //if this dictionary exist in maggie, old one will be replaced
    boolean setDictionary(Dictionary dictionary);

    //set current dictionary for maggie to find answer
    boolean changeDictionary(String id);

    Dictionary getDictionary(String id);
}
