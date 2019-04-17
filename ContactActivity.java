package com.example.equityvalidation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ContactActivity extends AppCompatActivity implements View.OnClickListener{
    private SessionHandler session;
    private CardView welcomeCard, logoutCard, createCard;
    private Button btnDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeCard= findViewById(R.id.welcome_card);
        logoutCard= findViewById(R.id.logout_card);
        createCard= findViewById(R.id.create_card);
        btnDashboard= findViewById(R.id.btnDashboard);


        welcomeText.setText("Welcome "+user.getAgentcode()+"");

        logoutCard.setOnClickListener(this);
        createCard.setOnClickListener(this);
        //go back to dashboard on click
        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ContactActivity.this,DashboardHome.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    public void onClick(View v) {
        Intent i ;
    //allow access to the other tabs
        switch (v.getId()){
            case R.id.logout_card : session.logoutUser(); i = new Intent(this, MainActivity.class); startActivity(i); break ;
            default:break;
        }
    }
}
