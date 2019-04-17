package com.example.equityvalidation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_AGENTCODE = "agentcode";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPTY = "";
    private EditText etAgentcode;
    private EditText etPassword;
    private String agentcode, country;
    private String password;
    private ProgressDialog pDialog;
    private String login_url = "http://192.168.43.252/EquityAgent/login.php";
    private SessionHandler session;

    RelativeLayout rellay1, rellay2;
    Handler handler=new Handler();
    Runnable runnable= new Runnable() {
        @Override
        public void run() {
           rellay1.setVisibility(View.VISIBLE);
           rellay2.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rellay1= findViewById(R.id.rellay1);
        rellay2= findViewById(R.id.rellay2);

        handler.postDelayed(runnable,  2500);//time to open the second relative layout

        session = new SessionHandler(getApplicationContext());



        etAgentcode = findViewById(R.id.etLoginAgentCode);
        etPassword = findViewById(R.id.etLoginPassword);

        Button register = findViewById(R.id.btnLoginRegister);
        Button login = findViewById(R.id.btnLogin);

        //open the registration page
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agentcode = etAgentcode.getText().toString().toLowerCase().trim();
                password = etPassword.getText().toString().trim();
                if (validateInputs()) {
                    login();
                }
            }
        });
    }
   //launch dashboard after successful login
    private void loadDashboard() {
        Intent intent= new Intent(MainActivity.this,DashboardHome.class);
        startActivity(intent);
        finish();

    }
    //shows loader while logging in
    private void displayLoader() {
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }
    //method to login
    private void login() {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            //get the request parameters
            request.put(KEY_AGENTCODE, agentcode);
            request.put(KEY_PASSWORD, password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsArrayRequest= new JsonObjectRequest
                (Request.Method.POST, login_url, request, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if agent got logged in successfully

                            if (response.getInt(KEY_STATUS) == 0) {
                                session.loginUser(agentcode,response.getString(KEY_FULL_NAME));
                                loadDashboard();

                            }else{
                                Toast.makeText(getApplicationContext(),
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override

                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }


    private boolean validateInputs() {
        if(KEY_EMPTY.equals(agentcode)){
            etAgentcode.setError("Agent Code cannot be empty");
            etAgentcode.requestFocus();
            return false;
        }
        if(KEY_EMPTY.equals(password)){
            etPassword.setError("Password cannot be empty");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            System.exit(0);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

}
