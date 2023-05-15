package com.example.carspotter;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.carspotter.model.Spot;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Random;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private String REGISTER_URL = "https://studev.groept.be/api/a22pt304/RegisterUser";
    private String CHECK_USER_URL = "https://studev.groept.be/api/a22pt304/CheckUser";

    private View view;
    private ExtendedFloatingActionButton loginfab;
    private TextInputEditText username;
    private TextInputEditText password;
    private String checkedPass;
    private String checkedRandomString;
    boolean exists = false;

    public LoginFragment() {
        // Required empty public constructor
    }
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        loginfab = (ExtendedFloatingActionButton) view.findViewById(R.id.loginfab);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        Process();
        return view;
    }
    private void Process(){
        loginfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Here we simply check if both fields are filled in, if so we proceed to processing the data.
                 */
                //TODO: vermijd speciale characters

                // Check if username is filled in
                int check = 0;
                String input = username.getText().toString().trim();
                if (input.isEmpty()) {
                    username.setError("This field cannot be empty");
                } else {
                    username.setError(null);
                    check += 1;
                }

                // Check if password is filled in
                input = password.getText().toString().trim();
                if (input.isEmpty()) {
                    password.setError("This field cannot be empty");
                } else {
                    password.setError(null);
                    check += 1;
                }

                if(check == 2){
                    processData();
                }
            }
        });
    }
    private void processData(){
        /**
         * Here we first check if the user already exists. If they do, the password will be checked and they'll log in.
         * If the user doesn't already exist, they'll be promted with a pop-up to confirm the registration.
         */
        if(userExists()){
            login();
        }
        else {
            register();
        }
    }
    private boolean userExists(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                CHECK_USER_URL+"/"+String.valueOf(username.getText()),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check if database contains the user
                        try {
                            String user = String.valueOf(response.getJSONObject(0).getString("username"));
                            if (user.equals(String.valueOf(username.getText()))) {
                                checkedPass = String.valueOf(response.getJSONObject(0).getString("password"));
                                checkedRandomString = String.valueOf(response.getJSONObject(0).getString("randomString"));
                                exists = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getActivity(),
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(queueRequest);

        return exists;
    }
    private void register() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Register")
                .setMessage("Seems like you don't already have an account, do you wish to register? DISCLAIMER: You will not be able to change your username afterwards, chose carefully.")
                .setNeutralButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Respond to neutral button press
                    }
                })
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Respond to positive button press
                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                REGISTER_URL,
                                response -> {
                                    // Handle the response
                                    String user = String.valueOf(username.getText());
                                    ((MainActivity) (getContext())).setUser(user);
                                    Toast.makeText(getActivity(), "Welcome "+ user +", you succesfully registered!", Toast.LENGTH_LONG).show();
                                },
                                error -> {
                                    // Handle the error
                                    Toast.makeText(getActivity(), ""+error, Toast.LENGTH_LONG).show();
                                    //progressIndicatorAddWikiView.hide();
                                }) {
                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                String upUser = String.valueOf(username.getText());
                                String upPass = String.valueOf(password.getText());
                                String randomString = generateRandomString();

                                MessageDigest messageDigest = null;
                                try {
                                    messageDigest = MessageDigest.getInstance("SHA-256");
                                } catch (NoSuchAlgorithmException e) {
                                    throw new RuntimeException(e);
                                }
                                messageDigest.update((upPass + randomString).getBytes());
                                String hashedPass = new String(messageDigest.digest());

                                String body = null;
                                body = "user=" + upUser
                                        + "&" + "pass=" +  hashedPass
                                        + "&" + "string" + randomString
                                        ;
                                return body.getBytes();
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/x-www-form-urlencoded";
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                        requestQueue.add(stringRequest);
                    }
                })
                .show();
    }
    static String generateRandomString() {
        // This code is used for generating the randomString
        UUID randomUUID = UUID.randomUUID();
        String randomString = randomUUID.toString().replaceAll("_", "");
        randomString = randomUUID.toString().replaceAll("-", "");
        return randomString;

    }
    private void login(){
        /**
         * Here we check the password. If it matches, the user will be "logged in".
         * If it doesn't match, a notification will be given.
         */
        String user = String.valueOf(username.getText());
        String givenPass = String.valueOf(password.getText());
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        messageDigest.update((givenPass + checkedRandomString).getBytes());
        String givenHashedPass = new String(messageDigest.digest());

        if(givenHashedPass.equals(checkedPass)){
            ((MainActivity) (getContext())).setUser(user);
            Toast.makeText(getActivity(),"Succesfully logged in. Welcome "+ user,Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(),"Error: User exists, but password does not match.",Toast.LENGTH_SHORT).show();
        }
    }
}