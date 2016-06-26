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

public class EventListActivity extends AppCompatActivity {

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
                setupDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupDatabase() {
        URL = URL_BASE + month;
        db = FirebaseDatabase.getInstance();
        db.getReference(month).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ds = dataSnapshot;
                //new SetList().execute();
                new FetchList().execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOG_TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private class SetList extends AsyncTask<Void, Void, Void> {

        private ArrayList<Event> eventList;
        private String companyName;
        private String location;
        private String time;
        private String date;
        private String website;
        DatabaseReference dbEventList;

        @Override
        protected void onPreExecute() {
            eventList = new ArrayList<>();
            dbEventList = db.getReference(month);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //JSOUP
                Document doc = Jsoup.connect(URL).get();
                Element table = doc.select("div#text").first();
                Iterator<Element> it = doc.select("td[width=45%]").iterator();
                Log.d(LOG_TAG, table.text());

                int count = 0;
                companyName = "";
                location = "";
                date = "";

                while (it.hasNext()) {
                    String info = it.next().text();
                    switch (count % 5) {
                        case 0:
                            companyName = info;
                            break;
                        case 1:
                            date = info;
                            break;
                        case 2:
                            time = info;
                            break;
                        case 3:
                            location = info;
                            break;
                        case 4:
                            website = info;
                            if (!location.equals("")) {
                                int voteUp = 0;
                                int voteDown = 0;
                                Event event = new Event(companyName, location, time, date);
                                event.setVoteFood(voteUp);
                                event.setVoteShirt(voteDown);

                                DatabaseReference dbEvent = dbEventList.child(event.getDatabaseName());
                                dbEvent.child("voteFood").setValue(0);
                                dbEvent.child("voteShirt").setValue(0);
                                eventList.add(event);
                            }
                            break;
                    }
                    count++;
                }


            } catch (IOException e) {
                Log.e(LOG_TAG, "error: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    private class FetchList extends AsyncTask<Void, Void, Void> {
        private ArrayList<Event> eventList;
        private String companyName;
        private String date = "";
        private String time;
        private String location;
        private String website;

        @Override
        protected void onPreExecute() {
            eventList = new ArrayList<>();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //JSOUP
                Document doc = Jsoup.connect(URL).get();
                Element table = doc.select("div#text").first();
                Iterator<Element> it = doc.select("td[width=45%]").iterator();

                int count = 0;
                companyName = "";
                location = "";

                while (it.hasNext()) {
                    String info = it.next().text();
                    switch (count % 5) {
                        case 0:
                            companyName = info;
                            break;
                        case 1:
                            date = info;
                            break;
                        case 2:
                            time = info;
                            break;
                        case 3:
                            location = info;
                            break;
                        case 4:
                            website = info;
                            if (!location.equals("")) {
                                Event event = new Event(companyName, location, time, date);
                                long voteFood = (long) ds.child(event.getDatabaseName()).child("voteFood").getValue();
                                long voteShirt = (long) ds.child(event.getDatabaseName()).child("voteShirt").getValue();
                                event.setVoteFood(voteFood);
                                event.setVoteShirt(voteShirt);
                                eventList.add(event);
                            }
                            break;
                    }
                    count++;
                }


            } catch (IOException e) {
                Log.e(LOG_TAG, "error: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            EventArrayAdapter adapter = new EventArrayAdapter(getApplicationContext(), eventList);
            if (listView.getHeaderViewsCount() == 0) {
                listView.addHeaderView((View) getLayoutInflater().inflate(R.layout.header, null));
            }
            listView.setAdapter(adapter);
        }

    }
}
