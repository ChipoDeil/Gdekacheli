package com.example.yaroslav.gdekacheli;


public class TokenHolder {
    private static String token;
    private static String name;
    private static double longitude;
    private static double latitude;
    public static String getToken(){
        return token;
    }
    public static String getName(){
        return name;
    }
    public static Double getLongitude(){
        return longitude;
    }
    public static Double getLatitude(){
        return latitude;
    }
    public static void setToken(String t){
        token = t;
    }
    public static void setName(String n){
        name = n;
    }
    public static void setLongitude(double lo){
        longitude = lo;
    }
    public static void setLatitude(double la){
        latitude = la;
    }
}
