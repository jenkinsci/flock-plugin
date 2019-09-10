package com.flock.plugin;

import hudson.model.BuildListener;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestsManager {

    public static void sendNotification(String webhookUrl, JSONObject payload, FlockLogger logger) throws IOException {
        URL url = new URL(webhookUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        // For POST only - START
        con.setDoOutput(true);
        Writer w = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
        w.write(payload.toString());
        w.flush();
        w.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        logger.log("POST Response code : " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.log(response.toString());
        } else {
            logger.log("POST request not worked. Got following message : " + con.getResponseMessage());
        }
    }
}
