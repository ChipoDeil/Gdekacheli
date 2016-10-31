package com.example.yaroslav.gdekacheli;

import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by yaroslav on 28.10.2016.
 */
class getcoords extends AsyncTask<ArrayList<String[]>, Void, ArrayList<String[]>> {

    ArrayList<String[]> array;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ArrayList<String[]> doInBackground(ArrayList<String[]>... params) {
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
                arr = array;
            }
            input.close();
            return arr;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("error", "null1");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("error", "null2");
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("error", "null3");
            return array;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<String[]> result) {
        super.onPostExecute(result);
        array = result;
    }

    public ArrayList<String[]> getcoords(){
        return array;
    }

}