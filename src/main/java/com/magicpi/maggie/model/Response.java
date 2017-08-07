package com.magicpi.maggie.model;


public class Response {

    public static final int ERROR = -1;

    public static final int ANSWER = 0;

    public static final int SUGGESTION = 1;

    public final int state;
    public final String message;

    private Response(int state, String message) {
        this.state = state;
        this.message = message;
    }

    public static Response Error() {
        return Error("Error!");
    }

    public static Response Error(String message) {
        return new Response(ERROR, message);
    }

    public static Response Answer(String answer) {
        if (answer != null) {
            return new Response(ANSWER, answer);
        }
        return Error();
    }

    public static Response Suggestion(String suggestion) {
        if (suggestion != null) {
            return new Response(SUGGESTION, suggestion);
        }
        return Error();
    }
}
