package com.example.yaroslav.gdekacheli;

import android.util.Log;

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
import java.util.ArrayList;

/**
 * Created by yaroslav on 26.10.2016.
 */

public class json {
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        try {
            URL oracle = new URL("http://keklol.ru/gdekacheli/getcoords.php");
            URLConnection uc = oracle.openConnection();
            BufferedReader input = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine = input.readLine();
            ArrayList<String[]> arr = new ArrayList<String[]>();
            JSONArray a = (JSONArray) parser.parse(inputLine);
            for (Object o : a){
                JSONObject users = (JSONObject) o;
                String[] mass = {users.get("id").toString(), users.get("title").toString(), users.get("desc").toString(), users.get("img").toString(),
                        users.get("latitude").toString(), users.get("longitude").toString()};
                arr.add(mass);
            }
            input.close();
            System.out.println(arr.size());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("asd", "asd");
        }


    }
}
