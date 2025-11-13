package com.example.payments.utils;

public class ErrorHandler {

    public static void log(String context, Exception e) {
        System.err.println("ERROR in " + context + ": " + e.getMessage());
        e.printStackTrace(System.err);
    }

    public static void log(String context, String message) {
        System.err.println("ERROR in " + context + ": " + message);
    }
}
