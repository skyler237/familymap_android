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
import com.skyler.android.familymap.model.FamilyMapModel;
import com.skyler.android.familymap.model.User;
import com.skyler.android.familymap.network.HttpClient;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * This Fragment handles the login screen of the app.
 * It interfaces with HttpClient class to login a user and retrieve family data.
 */
public class LoginFragment extends Fragment {
    static final String DEFAULT_USERNAME = "skyler237";
    static final String DEFAULT_PASSWORD = "pw";
//    static final String DEFAULT_HOST = "192.168.1.14";
//        static final String DEFAULT_HOST = "192.168.250.16";
    static final String DEFAULT_HOST = "10.24.222.69";
    static final String DEFAULT_PORT = "8080";
    public static HttpClient httpClient;
    private static EditText usernameEditText;
    private static EditText passwordEditText;
    public User currentUser;
    private OnLoginButtonPressedListener mListener;
    private EditText serverHostEditText;
    private EditText serverPortEditText;
    private Button mSignInButton;
    private String authorizationToken;
    private String personId;
    private String loginResponseBody;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }

    /**
     * Gets the username from the edit text on the Login screen.
     *      **For testing purposes, also allows for a hardcoded default value if the edit text is blank
     * @return - the username entered by the user (or a default value)
     */
    public static String getUsername() {
        String typedUsername = usernameEditText.getText().toString();

        if (typedUsername.equals("")) {
            return DEFAULT_USERNAME;
        } else {
            return typedUsername;
        }
    }

    /**
     * Gets the password from the edit text on the Login screen.
     *      **For testing purposes, also allows for a hardcoded default value if the edit text is blank
     * @return - the password entered by the user (or a default value)
     */
    public static String getPassword() {
        String typedPassword = passwordEditText.getText().toString();

        if (typedPassword.equals("")) {
            return DEFAULT_PASSWORD;
        } else {
            return typedPassword;
        }
    }

    /**
     * Gets the server host from the edit text on the Login screen.
     *      **For testing purposes, also allows for a hardcoded default value if the edit text is blank
     * @return - the server host entered by the user (or a default value)
     */
    private String getServerHost() {
        String typedHost = serverHostEditText.getText().toString();

        if (typedHost.equals("")) {
            return DEFAULT_HOST;
        } else {
            return typedHost;
        }

    }

    /**
     * Gets the server port from the edit text on the Login screen.
     *      **For testing purposes, also allows for a hardcoded default value if the edit text is blank
     * @return - the server port entered by the user (or a default value)
     */
    private String getServerPort() {
        String typedPort = serverPortEditText.getText().toString();

        if (typedPort.equals("")) {
            return DEFAULT_PORT;
        } else {
            return typedPort;
        }
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

    /**
     * Helper function to handle a press of the login button
     * This function will attempt to logon the user and retrieve their data from the server.
     */
    private void onButtonClicked() {
        // Set up the user
        currentUser = new User(getUsername(), getPassword());
        FamilyMapModel.SINGLETON.setCurrentUser(currentUser);

        // Set up our http client
        FamilyMapModel.SINGLETON.httpClient = new HttpClient(getServerHost(), getServerPort());
        httpClient = FamilyMapModel.SINGLETON.httpClient;

        // Attempt a login
        boolean loginSuccess = httpClient.login(currentUser);
        if (loginSuccess) {
            Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_LONG).show();
        }

        // Retrieve the user's family data
        boolean retrieveDataSuccess = httpClient.retrieveFamilyData();
        if (retrieveDataSuccess) {
            Toast.makeText(getContext(), "Welcome, " + currentUser.getFirstName() + " " + currentUser.getLastName() + "!", Toast.LENGTH_LONG).show();
            FamilyMapModel.SINGLETON.setCurrentUser(currentUser); // Save the current user in the com.skyler.android.familymap.model before we exit
            mListener.onLoginSuccessful(); //Tell the main activity that we have logged in
        } else {
            if (currentUser.isLoggedIn) { // Only display this if the login was successful, but the data retrieval was not
                Toast.makeText(getContext(), "Data retrieval failed", Toast.LENGTH_LONG).show();
            }
        }
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

    public interface OnLoginButtonPressedListener {
        void onLoginSuccessful();
    }


}


