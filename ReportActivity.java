package com.example.equityvalidation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class ReportActivity extends AppCompatActivity {
    private SessionHandler session;
    private ProgressBar uploadProgressBar;
    class ViewReport{
        private String name;
        public ViewReport(String name) {
            this.name = name;
        }
        public String getName() {return name;}
    }


    //HTTP client
    public class MyUploader {
        private static final String DATA_UPLOAD_URL="http://192.168.43.252/EquityAgent/index.php";
        private final Context c;
        public MyUploader(Context c) {this.c = c;}

        public void upload(ViewReport s, final View...inputViews)
        {
            if(s == null){Toast.makeText(c, "Cannot Find Agent Code...", Toast.LENGTH_SHORT).show();}
            else {

                uploadProgressBar.setVisibility(View.VISIBLE);

                AndroidNetworking.upload(DATA_UPLOAD_URL)
                        //agent_code is search criteria in the database
                        .addMultipartParameter("agent_code",s.getName())
                        //name is the type of request in php
                        .addMultipartParameter("name","upload")
                        .setTag("MYSQL_UPLOAD")
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(response != null) {
                                    try{
                                        //the response to server is displayed in edi ttext
                                        String responseString = response.get("message").toString();
                                        String responseString1 = response.get("message1").toString();
                                        String responseString2 = response.get("message2").toString();
                                        String responseString3 = response.get("message3").toString();
                                        String responseString4 = response.get("message4").toString();
                                        String responseString5 = response.get("message5").toString();
                                        String responseString6 = response.get("message6").toString();
                                        String responseString7 = response.get("message7").toString();



                                        TextView AcceptedToday = findViewById(R.id.AcceptedToday);
                                        TextView RejectedToday = findViewById(R.id.RejectedToday);
                                        TextView WaitingToday = findViewById(R.id.WaitingToday);
                                        TextView TotalToday = findViewById(R.id.TotalToday);
                                        TextView AcceptedMonthly = findViewById(R.id.AcceptedMonthly);
                                        TextView RejectedMonthly = findViewById(R.id.RejectedMonthly);
                                        TextView WaitingMonthly = findViewById(R.id.WaitingMonthly);
                                        TextView TotalMonthly = findViewById(R.id.TotalMonthly);


                                        AcceptedToday.setText(responseString);
                                        RejectedToday.setText(responseString1);
                                        WaitingToday.setText(responseString2);
                                        TotalToday.setText(responseString3);
                                        AcceptedMonthly.setText(responseString4);
                                        RejectedMonthly.setText(responseString5);
                                        WaitingMonthly.setText(responseString6);
                                        TotalMonthly.setText(responseString7);
                                    }catch(Exception e)
                                    {
                                        e.printStackTrace();
                                        Toast.makeText(c, "JSONException "+e.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }else{
                                    Toast.makeText(c, "NULL RESPONSE. ", Toast.LENGTH_LONG).show();
                                }
                                uploadProgressBar.setVisibility(View.GONE);
                            }
                            @Override
                            public void onError(ANError error) {
                                error.printStackTrace();
                                uploadProgressBar.setVisibility(View.GONE);
                                Toast.makeText(c, "UNSUCCESSFUL :  ERROR IS : \n"+error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        session = new SessionHandler(getApplicationContext());
        CardView welcomeCard= findViewById(R.id.welcome_card);
        CardView logoutCard= findViewById(R.id.logout_card);
        User user = session.getUserDetails();
        TextView welcomeText = findViewById(R.id.welcomeText);
        uploadProgressBar=findViewById(R.id.myProgressBar);
        Button btnDashboard = findViewById(R.id.btnDashboard);

        welcomeText.setText("Welcome "+user.getAgentcode()+"");

        String agent_code = user.getAgentcode();
        ViewReport s = new ViewReport(agent_code);
        //new method to get results
        new MyUploader(ReportActivity.this).upload(s);

        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logoutUser();
                Intent intent= new Intent(ReportActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ReportActivity.this,DashboardHome.class);
                startActivity(intent);
                finish();
            }
        });

    }
}