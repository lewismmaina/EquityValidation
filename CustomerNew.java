package com.example.equityvalidation;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class CustomerNew extends AppCompatActivity {
    public final static String MESSAGE_KEY ="com.example.equityvalidation.message_key";
    private SessionHandler session;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    public final static String KEY_FULLNAMES = "fullnames";
    public final static String KEY_IDNO = "idno";
    public final static String KEY_GENDER = "gender";
    public final static String KEY_PHONENO = "phoneno";
    public final static String KEY_ADDRESS = "address";
    public final static String KEY_DOB = "dob";
    public final static String KEY_NOKNAME = "nokname";
    public final static String KEY_AGENT = "agentcode";
    public final static String KEY_COUNTRY = "country";
    public final static String KEY_NOKPHONENO = "nokphoneno";
    public final static String KEY_OCCUPATION = "occupation";
    private static final String KEY_EMPTY = "";
    public static final int MULTIPLE_PERMISSIONS = 10;
    private EditText etFullNames;
    private EditText etIdNo;
    private EditText etGender;
    private EditText etPhoneNo;
    private EditText etAddress;
    private EditText etDOB;
    private EditText etNOKName;
    private EditText etNOKPhoneNo;
    private EditText etOccupation;
    private String fullNames;
    private String idNo;
    private String gender;
    private String phoneNo;
    private String address;
    private String dob;
    private String nokName;
    private String nokPhoneNo;
    private String occupation;
    String agentcode, agent, country;
    private ProgressDialog pDialog;
    DatePickerDialog picker;
    Spinner spinner;
    private String newcustomer_url = "http://192.168.43.252/EquityAgent/newcustomer.php";
    private static final int REQUEST_CAMERA = 110 , REQUEST_READ_STORAGE = 111, REQUEST_WRITE_STORAGE = 112;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());
        setContentView(R.layout.new_customer);
        session = new SessionHandler(getApplicationContext());
        final User user = session.getUserDetails();


        etFullNames = findViewById(R.id.etFullNames);
        agentcode= user.getAgentcode();
        etIdNo = findViewById(R.id.etIdNo);
        spinner = findViewById(R.id.gender_spinner);
        etPhoneNo = findViewById(R.id.etPhoneNo);
        etAddress = findViewById(R.id.etAddress);
        etDOB = findViewById(R.id.etDOB);
        etNOKName = findViewById(R.id.etNOKName);
        etNOKPhoneNo = findViewById(R.id.etNOKPhoneNo);
        etOccupation = findViewById(R.id.etOccupation);

        Button dashboard = findViewById(R.id.btnDashboard);
        Button proceed = findViewById(R.id.btnContinue);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        boolean hasPermissionPhoneState = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionPhoneState) {
            ActivityCompat.requestPermissions(CustomerNew.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }

        boolean hasPermissionLocation = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionLocation) {
            ActivityCompat.requestPermissions(CustomerNew.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        }

        boolean hasPermissionWrite = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionWrite) {
            ActivityCompat.requestPermissions(CustomerNew.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }


        etDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(CustomerNew.this, R.style.datepicker,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                etDOB.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.getDatePicker().setMaxDate((long) (System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 365.25 * 18)));
                picker.getDatePicker().setMinDate((long) (System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 365.25 * 100)));
                picker.show();
            }
        });

        //Launch dashboard screen when dashboard Button is clicked
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(CustomerNew.this,DashboardHome.class);

                startActivity(intent);
                finish();
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrieve the data entered in the edit texts
                fullNames = etFullNames.getText().toString().trim();
                idNo = etIdNo.getText().toString().toLowerCase().trim();
                gender = spinner.getSelectedItem().toString().trim();
                phoneNo = etPhoneNo.getText().toString().trim();
                address = etAddress.getText().toString().trim();
                dob = etDOB.getText().toString().trim();
                nokName = etNOKName.getText().toString().trim();
                nokPhoneNo = etNOKPhoneNo.getText().toString().trim();
                occupation = etOccupation.getText().toString().trim();
                if (validateInputs()) {
                    newCustomer();
                }

            }
        });

    }


    /**
     * Display Progress bar while registering
     */
    public void displayLoader() {
        pDialog = new ProgressDialog(CustomerNew.this);
        pDialog.setMessage("Proceeding.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }


    /**
     * Launch Images page on successful submission
     */
    private void loadDashboard() {

        etIdNo =  findViewById(R.id.etIdNo);
        String idno = etIdNo.getText().toString();

        Intent intent= new Intent(CustomerNew.this,PassportActivity.class);
        intent.putExtra(KEY_IDNO,idno);
        startActivity(intent);
        finish();


    }

    private void newCustomer() {
        displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_FULLNAMES, fullNames);
            request.put(KEY_IDNO, idNo);
            request.put(KEY_GENDER, gender);
            request.put(KEY_PHONENO, phoneNo);
            request.put(KEY_ADDRESS, address);
            request.put(KEY_DOB, dob);
            request.put(KEY_NOKNAME, nokName);
            request.put(KEY_NOKPHONENO, nokPhoneNo);
            request.put(KEY_OCCUPATION, occupation);
            request.put(KEY_AGENT, agentcode);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, newcustomer_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if user has a registered id no
                            if (response.getInt(KEY_STATUS) == 0) {
                                loadDashboard();

                            } else if (response.getInt(KEY_STATUS) == 1) {
                                //Display error message if id number is already exists
                                etIdNo.setError("An account with the id exists!");
                                session.getUserDetails();
                                etIdNo.requestFocus();
                            } else {
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

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    private boolean validateInputs() {
        if (KEY_EMPTY.equals(fullNames)) {
            etFullNames.setError("Full Names cannot be empty");
            etFullNames.requestFocus();
            return false;

        }
        if (KEY_EMPTY.equals(idNo)) {
            etIdNo.setError("Id Number cannot be empty");
            etIdNo.requestFocus();
            return false;
        }
        if (gender.equalsIgnoreCase("-Select Gender-")) {
            Toast.makeText(CustomerNew.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (KEY_EMPTY.equals(phoneNo)) {
            etPhoneNo.setError("Phone number cannot be empty");
            etPhoneNo.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(address)) {
            etAddress.setError("Address cannot be empty");
            etAddress.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(dob)) {
            etDOB.requestFocus();
            Toast.makeText(CustomerNew.this, "Please Select DOB", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (KEY_EMPTY.equals(nokName)) {
            etNOKName.setError("NOK Name cannot be empty");
            etNOKName.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(nokPhoneNo)) {
            etNOKPhoneNo.setError("NOK Phone cannot be empty");
            etNOKPhoneNo.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(occupation)) {
            etOccupation.setError("Occupation cannot be empty");
            etOccupation.requestFocus();
            return false;
        }

        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(CustomerNew.this, "Permission granted.", Toast.LENGTH_SHORT).show();
                    //reload my activity with permission granted or use the features what required the permission
                    finish();
                    startActivity(getIntent());
                } else
                {
                    Toast.makeText(CustomerNew.this, "The app was not allowed to get access Camera. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
            case REQUEST_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(CustomerNew.this, "Permission granted.", Toast.LENGTH_SHORT).show();
                    //reload my activity with permission granted or use the features what required the permission
                    finish();
                    startActivity(getIntent());
                } else
                {
                    Toast.makeText(CustomerNew.this, "The app was not allowed to read storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }

            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(CustomerNew.this, "Permission granted.", Toast.LENGTH_SHORT).show();
                    //reload my activity with permission granted or use the features what required the permission
                    finish();
                    startActivity(getIntent());
                } else
                {
                    Toast.makeText(CustomerNew.this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}