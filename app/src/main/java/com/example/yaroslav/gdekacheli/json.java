package com.example.yaroslav.gdekacheli;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by yaroslav on 26.10.2016.
 */

public class json {
    public static void main(String[] args) {
        String link = "http://keklol.ru/gdekacheli/login.php";
        byte data[] = null;
        String params = "name=chipodeil&pass=123";
        InputStream is = null;
        try {
            String myParams = "token=583db7078d72b&name=chip@deil";
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Length", "" + Integer.toString(myParams.getBytes().length));
            OutputStream os = conn.getOutputStream();
            data = myParams.getBytes("UTF-8");
            os.write(data);
            data = null;
            conn.connect();
            int responseCode= conn.getResponseCode();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            String token = null;
            boolean success = false;
            while((line = br.readLine()) != null) {
                if (line.equals("false")) {
                    break;
                } else if(line.equals("error")){
                    break;
                }else {
                    token = line;
                    if (!token.isEmpty()){
                        success = true;
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*HttpClient httpclient = HttpClients.createDefault();
        String name = "chipodeil";
        HttpPost httppost = new HttpPost("http://keklol.ru/gdekacheli/login.php");
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("pass", "123"));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        String token = null;
        if (entity != null) {

            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
                String line = null;
                boolean login = false;
                while ((line = br.readLine()) != null) {
                    if(line.equals("false")){
                        break;
                    }else{
                        login = true;
                        token = line;
                    }
                }
                if(login){
                    HttpGet oracle = new HttpGet("http://keklol.ru/gdekacheli/test.php?name="+name+"&token="+token);
                    HttpResponse responseSec = httpclient.execute(oracle);
                    BufferedReader input = new BufferedReader(new InputStreamReader(responseSec.getEntity().getContent()));
                    boolean success = false;
                    while((line = input.readLine()) != null){
                        if(line.equals("false")){
                            break;
                        }else{
                            success = true;
                            token = line;
                        }
                    }
                    if(success){
                        System.out.println(token);
                    }
                }
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

            } finally {

            }
        }*/
    }
}
