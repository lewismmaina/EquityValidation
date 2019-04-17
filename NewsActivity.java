package com.example.equityvalidation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class NewsActivity extends AppCompatActivity implements View.OnClickListener{
    private SessionHandler session;
    private CardView welcomeCard, logoutCard, createCard, newsCard, messageCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeCard= findViewById(R.id.welcome_card);
        logoutCard= findViewById(R.id.logout_card);
        createCard= findViewById(R.id.create_card);
        newsCard= findViewById(R.id.news_card);
        messageCard= findViewById(R.id.message_card);
        Button btnDashboard = findViewById(R.id.btnDashboard);

        welcomeText.setText("Welcome "+user.getAgentcode()+"");

        logoutCard.setOnClickListener(this);
        createCard.setOnClickListener(this);
        newsCard.setOnClickListener(this);
        messageCard.setOnClickListener(this);

        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(NewsActivity.this,DashboardHome.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    public void onClick(View v) {
        Intent i ;

        switch (v.getId()){
            case R.id.logout_card : session.logoutUser(); i = new Intent(this, MainActivity.class); startActivity(i); break ;
            default:break;
        }
    }

}
