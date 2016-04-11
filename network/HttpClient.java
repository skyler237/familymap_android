package com.skyler.android.familymap.network;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.skyler.android.familymap.main_activity.MainActivity;
import com.skyler.android.familymap.model.Event;
import com.skyler.android.familymap.model.FamilyMapModel;
import com.skyler.android.familymap.model.Person;
import com.skyler.android.familymap.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Skyler on 3/15/2016.
 * Manages the networking portion of this app - performs logins, data accesses, etc.
 */
public class HttpClient {

    private String baseUrl;

    /**
     * Constructs an HttpClient that will be accessing the specified host and port
     *
     * @param serverHost - the host to be accessed (e.g. IP address)
     * @param serverPort - the port being accessed (e.g. 8080)
     */
    public HttpClient(String serverHost, String serverPort) {
        baseUrl = "http://" + serverHost + ":" + serverPort;
    }

    public HttpClient() {
        // Do nothing...
    }

    /**
     * Attempts to log <code>user</code> in to the current server
     * @param user - The user to be logged in
     * @return - returns true if login was successful and false if not.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public boolean login(User user) {
        FamilyMapModel.SINGLETON.setCurrentUser(user);
        try {
            return new LoginTask().execute().get(); // Returns whether or not the login was successful
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean retrieveFamilyData() {
        try {
            return new RetrieveFamilyDataTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean resyncData() {
        try {
            return new ResyncDataTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Attempts to log in the specified user at the specified url
     *
     * @param user - the user to be logged in
     * @return
     */
    private boolean performLogin(User user) throws MalformedURLException {
        URL url = new URL(baseUrl + "/user/login");
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
//                connection.setDoInput(true);
//                connection.addRequestProperty("Accept", "html/text");
            Log.i("LoginTask", "Trying to connect...");

            connection.connect();
            Log.i("LoginTask", "Connected!");

            OutputStream out = connection.getOutputStream();

            // Format username and password as JSON string
            JSONObject requestBodyJSON = new JSONObject();
            //FOR TESTING!
//            requestBodyJSON.put("username", "skyler237");
//            requestBodyJSON.put("password", "pw");
            requestBodyJSON.put("username", user.getUsername());
            requestBodyJSON.put("password", user.getPassword());

            String requestBody = requestBodyJSON.toString();
            out.write(requestBody.getBytes());
            out.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Get response body input stream
                InputStream responseBody = connection.getInputStream();

                // Read response body bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = responseBody.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }

                // Convert response body bytes to a string
                String loginResponseBody = baos.toString();
                // Parse the response and save authorization key and person ID
                JSONObject responseBodyJSON = new JSONObject(loginResponseBody);
                user.setAuthorizationToken(responseBodyJSON.getString("Authorization"));
                user.setPersonId(responseBodyJSON.getString("personId"));
                return true;

            }

        } catch (ProtocolException e) {
            Log.e("LoginTask", "Protocol Exception!! - URL: " + url.toString());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.e("LoginTask", "IO Exception!! - URL: " + url.toString());
            if (connection != null) {
                try {
                    Log.i("LoginTask", "Response code: " + String.valueOf(connection.getResponseCode()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            Log.e("LoginTask", "JSON Exception!! - URL: " + url.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * This function gets the person information for the current user (i.e. name, id, gender, father, mother, etc.)
     *
     * @param user - the user about whom we are getting the information
     * @return - true if successful; false if unsuccessful -- NOTE: stores the information in the user object
     * @throws MalformedURLException
     */
    private boolean retrievePersonData(User user) throws IOException, JSONException {
        if (user.isLoggedIn) { // Check to see if this user is logged in
            try {
                URL url = new URL(baseUrl + "/person/" + user.getPersonId());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", user.getAuthorizationToken());
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    // Get response body input stream
                    InputStream responseBody = connection.getInputStream();

                    // Read response body bytes
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    while ((length = responseBody.read(buffer)) != -1) {
                        baos.write(buffer, 0, length);
                    }

                    // Convert response body bytes to a string
                    String responseBodyData = baos.toString();

                    JSONObject responseBodyJSON = new JSONObject(responseBodyData);
                    user.setFirstName(responseBodyJSON.getString("firstName"));
                    user.setLastName(responseBodyJSON.getString("lastName"));
                    user.setGender(responseBodyJSON.getString("gender"));
                    user.setFatherId(responseBodyJSON.getString("father"));
                    user.setMotherId(responseBodyJSON.getString("mother"));

                    return true;

                } else {
                    // SERVER RETURNED AN HTTP ERROR
                    Log.e("retrievePersonData", "HTTP Error!! - URL: " + url.toString());
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } else {
            return false;
        }

    }

    /**
     * This function gets the data for all the people associated with the specified user
     *
     * @param user - the user about whom we are getting the information
     * @return - true if successful; false if unsuccessful -- NOTE: stores the information in the user object
     * @throws MalformedURLException
     */
    private boolean retrieveAllPeopleData(User user) throws IOException, JSONException {
        if (user.isLoggedIn) { // Check to see if this user is logged in
            try {
                URL url = new URL(baseUrl + "/person/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", user.getAuthorizationToken());
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    // Get response body input stream
                    InputStream responseBody = connection.getInputStream();

                    // Read response body bytes
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    while ((length = responseBody.read(buffer)) != -1) {
                        baos.write(buffer, 0, length);
                    }

                    // Convert response body bytes to a string
                    String responseBodyData = baos.toString();

                    JSONObject responseBodyJSON = new JSONObject(responseBodyData);
                    JSONArray dataArray = responseBodyJSON.getJSONArray("data");

                    // Gather all people info
                    for (int i = 0; i < dataArray.length(); i++) {
                        Person person = new Person(dataArray.getJSONObject(i));
                        FamilyMapModel.SINGLETON.addPerson(person);

                    }
                    FamilyMapModel.SINGLETON.populateFamilyData();

                    return true;

                } else {
                    // SERVER RETURNED AN HTTP ERROR
                    Log.e("retrieveAllPeopleData", "HTTP Error!! - URL: " + url.toString());
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } else {
            return false;
        }

    }


    /**
     * This function gets all the event data for all family members of the specified user
     *
     * @param user - the user about whom we are getting the information
     * @return - true if successful; false if unsuccessful -- NOTE: stores the information in the user object
     * @throws MalformedURLException
     */
    private boolean retrieveAllEventData(User user) throws IOException, JSONException {
        if (user.isLoggedIn) { // Check to see if this user is logged in
            try {
                URL url = new URL(baseUrl + "/event/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", user.getAuthorizationToken());
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    // Get response body input stream
                    InputStream responseBody = connection.getInputStream();

                    // Read response body bytes
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    while ((length = responseBody.read(buffer)) != -1) {
                        baos.write(buffer, 0, length);
                    }

                    // Convert response body bytes to a string
                    String responseBodyData = baos.toString();

                    JSONObject responseBodyJSON = new JSONObject(responseBodyData);
                    JSONArray dataArray = responseBodyJSON.getJSONArray("data");

                    // Gather all event info
                    for (int i = 0; i < dataArray.length(); i++) {
                        Event event = new Event(dataArray.getJSONObject(i));
                        user.addRelatedEvent(event);
                    }

                    // Set up the filters with all the existing event types
                    FamilyMapModel.SINGLETON.setFilters(Event.eventTypes);

                    return true;

                } else {
                    // SERVER RETURNED AN HTTP ERROR
                    Log.e("retrieveAllEventData", "HTTP Error!! - URL: " + url.toString());
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } else {
            return false;
        }

    }

    private class LoginTask extends AsyncTask<URL, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(URL... params) {
            boolean result;
            try {
                result = performLogin(FamilyMapModel.SINGLETON.currentUser);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                result = false;
            }

            return result;
        }

        protected void onPostExecute(Boolean loginSuccess) {
            // Do nothing...
        }
    }

    private class RetrieveFamilyDataTask extends AsyncTask<URL, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(URL... params) {
            User currentUser = FamilyMapModel.SINGLETON.currentUser;
            boolean retrievePersonSuccessful;
            boolean retrieveAllPeopleSuccessful;
            boolean retrieveAllEventsSuccessful;
            try {
                retrievePersonSuccessful = retrievePersonData(currentUser);
                retrieveAllPeopleSuccessful = retrieveAllPeopleData(currentUser);
                retrieveAllEventsSuccessful = retrieveAllEventData(currentUser);

                if (!retrievePersonSuccessful) {
                    Log.e("RetrieveFamilyDataTask", "retrieve person data unsuccessful");
                    return false;
                } else if (!retrieveAllPeopleSuccessful) {
                    Log.e("RetrieveFamilyDataTask", "retrieve all people data unsuccessful");
                    return false;
                } else if (!retrieveAllEventsSuccessful) {
                    Log.e("RetrieveFamilyDataTask", "retrieve all events data unsuccessful");
                    return false;
                } else {
                    // All went well!
                    return true;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        protected void onPostExecute(Boolean success) {
            // Do nothing...
        }
    }

    class ResyncDataTask extends AsyncTask<URL, Integer, Boolean> {
        HttpClient httpClient = FamilyMapModel.SINGLETON.httpClient;
        User currentUser = FamilyMapModel.SINGLETON.currentUser;

        protected Boolean doInBackground(URL... params) {

            boolean retrievePersonSuccessful;
            boolean retrieveAllPeopleSuccessful;
            boolean retrieveAllEventsSuccessful;
            try {
                FamilyMapModel.SINGLETON.clearData();
                retrievePersonSuccessful = httpClient.retrievePersonData(currentUser);
                retrieveAllPeopleSuccessful = httpClient.retrieveAllPeopleData(currentUser);
                retrieveAllEventsSuccessful = httpClient.retrieveAllEventData(currentUser);

                if (!retrievePersonSuccessful) {
                    Log.e("RetrieveFamilyDataTask", "retrieve person data unsuccessful");
                    return false;
                } else if (!retrieveAllPeopleSuccessful) {
                    Log.e("RetrieveFamilyDataTask", "retrieve all people data unsuccessful");
                    return false;
                } else if (!retrieveAllEventsSuccessful) {
                    Log.e("RetrieveFamilyDataTask", "retrieve all events data unsuccessful");
                    return false;
                } else {
                    // All went well!
                    return true;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        protected void onPostExecute(Boolean success) {

        }
    }
}
