package com.magicpi.maggie;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.magicpi.maggie.model.Dictionary;
import com.magicpi.maggie.model.Response;
import com.magicpi.maggie.utils.SimilarityHelper;
import io.indico.Indico;
import io.indico.api.utils.IndicoException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static com.magicpi.maggie.model.Response.ANSWER;
import static com.magicpi.maggie.model.Response.ERROR;
import static com.magicpi.maggie.model.Response.SUGGESTION;

public class Main {

    public static void main(String[] args) {

        try {
            System.out.println("Initiating Maggie...");
            Indico indico = new Indico("344bcb1eff966604ef32ee8b4ebafcc0");
            SimilarityHelper helper = new SimilarityHelper(indico);
            Maggie maggie = new MaggieImpl(0.85, 0.40, helper);

            test(maggie, Dictionary.load("json/museum_general_1.json"));

//            generateDictionary(maggie);

//            Double result = helper.findSimilarity("can i play games online with grand casino", "Can I gamble online with Crown Casino?");
//            System.out.println("result: " + result);

        } catch (IndicoException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void test(Maggie maggie, Dictionary dictionary) {

        maggie.setDictionary(dictionary);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please enter a string for testing or enter 'end' to exit.");

            String input = scanner.nextLine();

            if (input.equals("end")) {
                break;

            } else {
                System.out.println("Finding answer...");
                Response response = maggie.findAnswer(input);

                switch (response.state) {
                    case ANSWER:
                        System.out.println("Found answer: \n" + response.message);
                        break;
                    case SUGGESTION:
                        System.out.println("got suggestion: " + response.message);
                        System.out.println("it's right suggestion!");
                        String answer = maggie.answerSuggestion(true).message;
                        System.out.println("Found answer: \n" + response.message);
                        break;
                    case ERROR:
                        System.out.println("Error: " + response.message);
                        break;
                }
            }
        }

        scanner.close();
        System.out.println("Test ended...");
    }

    private static void generateDictionary(Maggie maggie) throws IOException {

        Gson gson = new Gson();

        JsonReader jsonReader = new JsonReader(new FileReader("json/items.json"));
        Type type = new TypeToken<List<Item>>() {
        }.getType();
        ArrayList<Item> items = gson.fromJson(jsonReader, type);

        System.out.println(items);

        Map<String, String> textRefMap;
        for (Item item : items) {
            String id = item.getId();
            textRefMap = new HashMap<>();

            for (Question question : item.getQuestions()) {
                textRefMap.put(question.getHint(), question.getRef());
            }

            Dictionary dictionary = maggie.generateDictionary(item.getId(), textRefMap);
            dictionary.save("json/" + id + ".json", gson);
        }
    }

    private class Item {
        private String id;
        private List<Question> questions;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }
    }

    private class Question {
        private String hint;
        private String ref;

        public String getHint() {
            return hint;
        }

        public void setHint(String hint) {
            this.hint = hint;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }
    }
}