package com.example.carspotter;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {
    /**
     * These are needed for the login process
     */
    private ConstraintLayout loginLayout;
    private String REGISTER_URL = "https://studev.groept.be/api/a22pt304/RegisterUser";
    private String CHECK_USER_URL = "https://studev.groept.be/api/a22pt304/CheckUser";
    private String LOGIN_URL = "https://studev.groept.be/api/a22pt304/CheckPass";
    private String STRING_URL = "https://studev.groept.be/api/a22pt304/GetRandomString";

    private View view;
    private ExtendedFloatingActionButton loginfab;
    private TextInputEditText username;
    private TextInputEditText password;
    private String givenUser;
    boolean exists = false;


    /**
     * These are needed to switch to a list of all the user's spots
     */
    private RecyclerView personalSpots;
    private ConstraintLayout spotLayout;
    private FloatingActionButton logoutBtn;

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

        loginLayout = view.findViewById(R.id.loginLayout);
        spotLayout = view.findViewById(R.id.spotLayout);
        logoutBtn = view.findViewById(R.id.logoutBtn);

        if (((MainActivity) (getContext())).getUser() != null) {
            toggleLayout(newLayout.personalSpots);
        }
        else {
            toggleLayout(newLayout.login);
        }
        logoutListener();
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

                // Check if username contains special characters
                Pattern specialChars = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~'\"/;`%:-]");
                Matcher hasSpecialUser = specialChars.matcher(input);
                if (hasSpecialUser.find()) {
                    username.setError("The username can't contain any special characters!");
                }
                else {
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
                // Check if username contains special characters
                Matcher hasSpecialPass = specialChars.matcher(input);
                if (hasSpecialPass.find()) {
                    password.setError("The password can't contain any special characters!");
                }
                else {
                    password.setError(null);
                    check += 1;
                }

                if(check == 4){
                    givenUser = String.valueOf(username.getText());
                    processData();
                }
            }
        });
    }
    public static boolean Password_Validation(String password)
    {
        if(password.length()>=8)
        {
            Pattern specialChars = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

            Matcher hasSpecial = specialChars.matcher(password);

            return hasSpecial.find();

        }
        else
            return false;

    }
    private void processData() {
        /**
         * Here we first check if the user already exists. If they do, the password will be checked and they'll log in.
         * If the user doesn't already exist, they'll be promted with a pop-up to confirm the registration.
         */
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                CHECK_USER_URL + "/" + givenUser,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check if database contains the user
                        if (response.length() != 0){
                            login();
                        }
                        else {
                            register();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getActivity(),
                                "Unable to communicate with the server",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(queueRequest);
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
                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                        StringRequest submitRequest = new StringRequest(
                                Request.Method.POST,
                                REGISTER_URL,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        ((MainActivity) (getContext())).setUser(givenUser);
                                        Toast.makeText(
                                                getActivity(),
                                                "Succesfully registered, welcome "+givenUser+"!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(
                                                getActivity(),
                                                "Unable to connect to database" + error,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                        ) { //NOTE THIS PART: here we are passing the POST parameters to the webservice
                            @Override
                            protected Map<String, String> getParams() {
                                String newPass = String.valueOf(password.getText());
                                String randomString = generateRandomString();
                                String hashedPass = hash(newPass,randomString);

                                Map<String, String> params = new HashMap<>();
                                params.put("user", givenUser);
                                params.put("pass", hashedPass);
                                params.put("string", randomString);
                                return params;
                            }
                        };
                        requestQueue.add(submitRequest);
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
    private String hash(String password, String randomString){
        String hashedPass = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update((password + randomString).getBytes(StandardCharsets.UTF_8));
            byte[] hash = messageDigest.digest();

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = String.format("%02x", b);
                hexString.append(hex);
            }
            hashedPass = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPass;
    }
    private void login(){
        /**
         * Here we create the hashed password from the given textfields and then proceed to checkLogin()
         */
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
            STRING_URL + "/" + givenUser,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check if database contains the user
                        try {
                            String randomString = response.getJSONObject(0).getString("randomString");
                            String givenPass = String.valueOf(password.getText());

                            String hashedPass = hash(givenPass,randomString);
                            checkLogin(hashedPass);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getActivity(),
                                "Unable to communicate with the server",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(queueRequest);
    }

    /**
     * Here we check if the local hashed password matches the one stored in the database.
     * The actual check will be in the SQL query for user safety.
     */
     private void checkLogin(String hashedPass){
         RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
         JsonArrayRequest queueRequest = new JsonArrayRequest(
                 Request.Method.GET,
                 LOGIN_URL + "/" + hashedPass + "/" +givenUser,
                 null,
                 new Response.Listener<JSONArray>() {
                     @Override
                     public void onResponse(JSONArray response) {
                         // Check if database contains the user
                         try {
                             if (response.getJSONObject(0).getString("password_match").equals("1")){
                                 String loggedUser = String.valueOf(username.getText());
                                 ((MainActivity) (getContext())).setUser(loggedUser);
                                 Toast.makeText(
                                         getActivity(),
                                         "Succesfully logged in, welcome back "+loggedUser,
                                         Toast.LENGTH_SHORT).show();
                                 toggleLayout(newLayout.personalSpots);
                             }
                             else {
                                 Toast.makeText(
                                         getActivity(),
                                         "Password doesn't match! Please try again.",
                                         Toast.LENGTH_SHORT).show();
                             }
                         } catch (JSONException e) {
                             throw new RuntimeException(e);
                         }
                     }
                 },
                 new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {
                         Toast.makeText(
                                 getActivity(),
                                 "Unable to communicate with the server",
                                 Toast.LENGTH_SHORT).show();
                     }
                 });
         requestQueue.add(queueRequest);
     }

    /**
     * Once logged in we switch layouts
     */
    public enum newLayout {
        login,
        personalSpots;
    }
    private void toggleLayout(newLayout layout){
        if(layout == newLayout.login){
            loginLayout.setVisibility(view.VISIBLE);
            loginfab.setVisibility(view.VISIBLE);

            spotLayout.setVisibility(view.INVISIBLE);
        }
        else{
            loginLayout.setVisibility(view.INVISIBLE);
            loginfab.setVisibility(view.INVISIBLE);

            spotLayout.setVisibility(view.VISIBLE);
        }
    }

    private void logoutListener(){
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) (getContext())).logoutUser();
                toggleLayout(newLayout.login);
            }
        });
    }
}