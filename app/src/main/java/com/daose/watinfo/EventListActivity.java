package com.daose.watinfo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class EventListActivity extends AppCompatActivity implements EventListListener {

    private static final String URL_BASE = "http://www.ceca.uwaterloo.ca/students/sessions_details.php?id=";
    private String URL;
    private static final String LOG_TAG = "EventListActivity";
    public static String month = "2016Jan";
    private ListView listView;
    private Toolbar toolBar;
    private FirebaseDatabase db;
    private DataSnapshot ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent i = getIntent();

        ArrayList<Event> eventList = (ArrayList<Event>) getIntent().getSerializableExtra("eventList");

        listView = (ListView) findViewById(R.id.listview);
        Toolbar toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolBar);

        EventArrayAdapter adapter = new EventArrayAdapter(getApplicationContext(), eventList);
        listView.addHeaderView((View) getLayoutInflater().inflate(R.layout.header, null));
        listView.setAdapter(adapter);
        //setupDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshButton:
                PopulateList.getInstance().setListener(this);
                PopulateList.getInstance().getEventList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onEventListReceived(ArrayList<Event> eventList) {
        EventArrayAdapter adapter = new EventArrayAdapter(getApplicationContext(), eventList);
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView((View) getLayoutInflater().inflate(R.layout.header, null));
        }
        listView.setAdapter(adapter);
    }
}
