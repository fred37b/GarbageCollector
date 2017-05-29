package com.valhatech.garbagecollector.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by fred on 14/03/2016.
 */
public class RequestHandler {

    /**
     * Method to send httpPostRequest
     * This method is taking two arguments
     * First argument is the URL of the script to which we will send the request
     * Other is an HashMap with name value pairs containing the data to be send with the request
     *
     * @param requestURL
     * @param postDataParams
     * @return
     */
    public String sendPostRequest(String requestURL,
                                  HashMap<String, String> postDataParams) {
        //Creating a URL
        URL url;
        //StringBuilder object to store the message retrieved from the server
        StringBuilder stringBuilder = new StringBuilder();

        try {
            //Initializing Url
            url = new URL(requestURL);
            //Creating an httmlurl connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //Configuring connection properties
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Creating an output stream
            OutputStream outputStream = connection.getOutputStream();

            //Writing parameters to the request
            //We are using a method getPostDataString which is defined below
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            outputStream.close();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                stringBuilder = new StringBuilder();
                String response;
                //Reading server response
                while ((response = bufferedReader.readLine()) != null){
                    stringBuilder.append(response);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param requestURL
     * @return
     */
    public String sendGetRequest(String requestURL){
        StringBuilder stringBuilder =new StringBuilder();
        try {
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String string;
            while((string=bufferedReader.readLine())!=null){
                stringBuilder.append(string + "\n");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param requestURL
     * @param id
     * @return
     */
    public String sendGetRequestParam(String requestURL, String id){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(requestURL+id);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader);

            String myStr;
            while((myStr = bufferedReader.readLine())!=null){
                stringBuilder.append(myStr + "\n");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
