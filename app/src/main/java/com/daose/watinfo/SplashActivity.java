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

public class SplashActivity extends AppCompatActivity {

    private static final String URL_BASE = "http://www.ceca.uwaterloo.ca/students/sessions_details.php?id=";
    private String URL;
    private static final String LOG_TAG = "SplashActivity";
    public static String month = "2016Jan";
    private FirebaseDatabase db;
    private DataSnapshot ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setupDatabase();
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
            goToEventListActivity(eventList);
        }

    }

    private void goToEventListActivity(ArrayList<Event> eventList) {
        Intent intent = new Intent(this, EventListActivity.class);
        intent.putExtra("eventList", eventList);
        startActivity(intent);
    }

}