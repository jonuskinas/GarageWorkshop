package com.project.garageworkshop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RequestOperator extends Thread {


    public interface RequestOperatorListener{
        void success (List<Repairs> publication);
        void failed (int responseCode);
    }

    private RequestOperatorListener listener;
    private int responseCode;

    public void setListener (RequestOperatorListener listener) {
        this.listener =listener;
    }

    @Override
    public void run() {
        super.run();
        try {
            List<Repairs> publication = request();
            if (publication != null) {
                success(publication);
            }
            else {
                failed(responseCode);
            }

        }
        catch(IOException e) {
            failed(-1);
        }
        catch (JSONException e) {
            failed(-2);
        }
    }

    private List<Repairs> request() throws IOException, JSONException {

        URL obj = new URL("https://my-json-server.typicode.com/jonuskinas/GarageWorkshop/db");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        con.setRequestProperty("Content-Type", "application/json");

        responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        InputStreamReader streamReader;

        if (responseCode==200) {
            streamReader = new InputStreamReader(con.getInputStream());
        }
        else {
            streamReader = new InputStreamReader(con.getErrorStream());
        }
        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuffer response =new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());

        if(responseCode == 200) {
            return (parsingJsonObject(response.toString()));
        }
        else{
            return null;
        }
    }

    public List<Repairs> parsingJsonObject(String response) throws JSONException {
        JSONObject object = new JSONObject(response);
        JSONArray repairsData = object.getJSONArray("repairs");
        List<Repairs> allRepairs =new ArrayList<>();
        int n = repairsData.length();
        for (int i = 0; i < n; i++) {
            JSONObject rep = repairsData.getJSONObject(i);
            Repairs tmp =new Repairs();
            tmp.setUserId(rep.getString("userId"));
            tmp.setCarNumb(rep.getString("carNumb"));
            tmp.setRepaired(rep.getString("repaired"));
            tmp.setCost(rep.getDouble("cost"));
            allRepairs.add(tmp);


        }
        return allRepairs;
    }

    private void failed(int code) {
        if (listener != null) {
            listener.failed(code);
        }
    }

    private void success(List<Repairs> publication) {
        if (listener != null) {
            listener.success(publication);
        }
    }
}

