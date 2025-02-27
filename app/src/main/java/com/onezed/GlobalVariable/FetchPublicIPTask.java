package com.onezed.GlobalVariable;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchPublicIPTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... voids) {
        String publicIP = null;
        try {
            // Using ipify API to get the public IP
            URL url = new URL("https://api.ipify.org");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            publicIP = in.readLine();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicIP;
    }

    @Override
    protected void onPostExecute(String publicIP) {
        // Use the public IP (e.g., display it in a TextView)
        if (publicIP != null) {
            //System.out.println("Public IP: " + publicIP);
            Log.v("IPAddress",publicIP);
        } else {
            //System.out.println("Unable to fetch public IP");
            Log.v("IPAddress","Unable to fetch public IP");
        }
    }
}

