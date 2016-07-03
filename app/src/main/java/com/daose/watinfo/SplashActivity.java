package com.daose.watinfo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class SplashActivity extends AppCompatActivity implements EventListListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PopulateList.getInstance().setListener(this);
        PopulateList.getInstance().getEventList();
    }

    @Override
    public void onEventListReceived(ArrayList<Event> eventList) {
        Intent intent = new Intent(this, EventListActivity.class);
        intent.putExtra("eventList", eventList);
        startActivity(intent);
    }
}
