package com.example.yaroslav.gdekacheli;


import android.graphics.Path;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
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
        try {

            String link = "http://gdekacheli.ru/sendcoords.php";
            byte data[] = null;
            String myParams = "title=" + 123 + "&descr=" + 123 + "&longitude=" + 123 + "&latitude=" + 123 + "&token=" + "5846d27b28bd2" + "&img=" + 123 + "&name=" + "chipodeil";
            InputStream is = null;
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
            int responseCode = conn.getResponseCode();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
