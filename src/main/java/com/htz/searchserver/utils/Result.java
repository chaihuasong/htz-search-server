package com.htz.searchserver.utils;


public class Result {
    public static String ok() {
        return "ok";
    }
    public static String error(String msg) {
        return "error:" + msg;
    }
}
