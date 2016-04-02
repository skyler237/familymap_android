package com.skyler.android.familymap.main_activity;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skyler.android.familymap.R;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.skyler.android.familymap.model.FamilyMapModel;
import com.skyler.android.familymap.model.User;
import com.skyler.android.familymap.network.HttpClient;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    static final String DEFAULT_USERNAME = "skyler237";
    static final String DEFAULT_PASSWORD = "pw";
    static final String DEFAULT_HOST = "192.168.1.5";
//    static final String DEFAULT_HOST = "10.14.35.204";
    static final String DEFAULT_PORT = "8080";

    private OnLoginButtonPressedListener mListener;

    private TextView usernameTextView;
    private static EditText usernameEditText;
    private TextView passwordTextView;
    private static EditText passwordEditText;
    private TextView serverHostTextView;
    private EditText serverHostEditText;
    private TextView serverPortTextView;
    private EditText serverPortEditText;
    private Button mSignInButton;

    public static HttpClient httpClient;
    public User currentUser;
    private String authorizationToken;
    private String personId;
    private String loginResponseBody;

    public interface OnLoginButtonPressedListener {
        void onLoginSuccessful();
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }
    //    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }

    public static String getUsername() {
        String typedUsername = usernameEditText.getText().toString();

        if(typedUsername.equals("")) {
            return DEFAULT_USERNAME;
        }
        else {
            return typedUsername;
        }
    }

    public static String getPassword() {
        String typedPassword = passwordEditText.getText().toString();

        if(typedPassword.equals("")) {
            return DEFAULT_PASSWORD;
        }
        else {
            return typedPassword;
        }
    }

    private String getServerHost() {
        String typedHost = serverHostEditText.getText().toString();

        if(typedHost.equals("")) {
            return DEFAULT_HOST;
        }
        else {
            return typedHost;
        }

    }

    private String getServerPort() {
        String typedPort = serverPortEditText.getText().toString();

        if(typedPort.equals("")) {
            return DEFAULT_PORT;
        }
        else {
            return typedPort;
        }
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public void setLoginResponseBody(String loginResponseBody) {
        this.loginResponseBody = loginResponseBody;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = (EditText) v.findViewById(R.id.username);
        passwordEditText = (EditText) v.findViewById(R.id.password);
        serverHostEditText = (EditText) v.findViewById(R.id.server_host);
        serverPortEditText = (EditText) v.findViewById(R.id.server_port);

        mSignInButton = (Button) v.findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClicked();
            }
        });

        return v;

    }

    private void onButtonClicked() {
        // Perform sign in here

        LoginTask loginTask = new LoginTask();
        httpClient = new HttpClient(getServerHost(),getServerPort());
        // FOR TESTING
//        httpClient = new HttpClient("192.168.203.1","8080");
        currentUser = new User(getUsername(),getPassword());
        FamilyMapModel.SINGLETON.setCurrentUser(currentUser);

        //Execute task
        loginTask.execute();

        RetrieveFamilyDataTask retrieveFamilyDataTask = new RetrieveFamilyDataTask();
        retrieveFamilyDataTask.execute();

        // Transfer to Map fragment


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginButtonPressedListener) {
            mListener = (OnLoginButtonPressedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void showLoginResult(Boolean result) {
        Toast.makeText(getContext(), result.toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class LoginTask extends AsyncTask<URL, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(URL... params) {
            boolean result;
            try {
                result = httpClient.login(currentUser);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                result = false;
            }

            return result;
        }

        protected void onPostExecute(Boolean loginSuccess) {
            if(loginSuccess) {
                Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    class RetrieveFamilyDataTask extends AsyncTask<URL, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(URL... params) {

            boolean retrievePersonSuccessful;
            boolean retrieveAllPeopleSuccessful;
            boolean retrieveAllEventsSuccessful;
            try {
                retrievePersonSuccessful = httpClient.retrievePersonData(currentUser);
                retrieveAllPeopleSuccessful = httpClient.retrieveAllPeopleData(currentUser);
                retrieveAllEventsSuccessful = httpClient.retrieveAllEventData(currentUser);

                if(!retrievePersonSuccessful) {
                    Log.e("RetrieveFamilyDataTask","retrieve person data unsuccessful");
                    return false;
                }
                else if(!retrieveAllPeopleSuccessful) {
                    Log.e("RetrieveFamilyDataTask","retrieve all people data unsuccessful");
                    return false;
                }
                else if(!retrieveAllEventsSuccessful) {
                    Log.e("RetrieveFamilyDataTask","retrieve all events data unsuccessful");
                    return false;
                }
                else {
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
            if(success) {
                Toast.makeText(getContext(), "Welcome, " + currentUser.getFirstName() + " " + currentUser.getLastName() + "!", Toast.LENGTH_LONG).show();
                FamilyMapModel.SINGLETON.setCurrentUser(currentUser); // Save the current user in the com.skyler.android.familymap.model before we exit
                mListener.onLoginSuccessful(); //Tell the main activity that we have logged in
            }
            else {
                if(currentUser.isLoggedIn) { // Only display this if the login was successful, but the data retrieval was not
                    Toast.makeText(getContext(), "Data retrieval failed", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}


