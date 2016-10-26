package com.example.yaroslav.gdekacheli;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by yaroslav on 26.10.2016.
 */

public class json {
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        try {
            URL oracle = new URL("http://keklol.ru/gdekacheli/test.php");
            URLConnection uc = oracle.openConnection();
            BufferedReader input = new BufferedReader(new InputStreamReader(uc.getInputStream()));

            String inputLine;
            while((inputLine = input.readLine()) != null){
                JSONArray a = (JSONArray) parser.parse(inputLine);
                for (Object o : a){
                    JSONObject users = (JSONObject) o;
                    String id = (String) users.get("id");
                    String name = (String) users.get("login");
                    String pass = (String) users.get("pass");
                    System.out.println("Id: " +id+ " login: " +name+ " pass: " + pass);
                    System.out.println("\n");
                }
            }
            input.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
