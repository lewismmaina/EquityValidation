package com.example.equityvalidation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class DashboardHome extends AppCompatActivity implements View.OnClickListener{
    private SessionHandler session;
    private CardView welcomeCard, logoutCard, createCard, reportCard, newsCard, contactCard, messageCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_dashboard);
        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeCard= findViewById(R.id.welcome_card);
        logoutCard= findViewById(R.id.logout_card);
        createCard= findViewById(R.id.create_card);
        reportCard= findViewById(R.id.report_card);
        newsCard= findViewById(R.id.news_card);
        contactCard= findViewById(R.id.contact_card);
        messageCard= findViewById(R.id.message_card);

        //Display user in a card view
        welcomeText.setText("Welcome "+user.getAgentcode()+"");

        logoutCard.setOnClickListener(this);
        createCard.setOnClickListener(this);
        reportCard.setOnClickListener(this);
        newsCard.setOnClickListener(this);
        contactCard.setOnClickListener(this);
        messageCard.setOnClickListener(this);

    }

    //open different activities
    @Override
    public void onClick(View v) {
        Intent i ;

        switch (v.getId()){
            case R.id.logout_card : session.logoutUser(); i = new Intent(this, MainActivity.class); startActivity(i); break ;
            case R.id.create_card : i = new Intent(this, CustomerNew.class); startActivity(i);finish(); break ;
            case R.id.report_card : i = new Intent(this, ReportActivity.class); startActivity(i);finish(); break ;
            case R.id.news_card : i = new Intent(this, NewsActivity.class); startActivity(i);finish(); break ;
            case R.id.contact_card : i = new Intent(this, ContactActivity.class); startActivity(i);finish(); break ;
            case R.id.message_card : i = new Intent(this, MessageActivity.class); startActivity(i);finish(); break ;
            default:break;
        }
    }
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            session.logoutUser();
            System.exit(0);
        } else {
            Toast.makeText(this, "Press Back again to Log Out and Exit.",
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_HOME)
        {
            session.logoutUser();

        }
        return false;
    }



}
