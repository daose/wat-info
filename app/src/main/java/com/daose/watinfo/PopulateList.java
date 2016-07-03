package com.daose.watinfo;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by student on 26/06/16.
 */
public class PopulateList {
    private static final String URL_BASE = "http://www.ceca.uwaterloo.ca/students/sessions_details.php?id=";
    private static String URL;
    private static final String LOG_TAG = "PopulateList";
    public static String month = "2016Jan";
    private static FirebaseDatabase db;
    private static DataSnapshot ds;
    private static PopulateList populateList = null;
    private EventListListener listener;

    protected PopulateList() {
    }

    public void setListener(EventListListener listener) {
        this.listener = listener;
    }

    public static PopulateList getInstance() {
        if (populateList == null) {
            populateList = new PopulateList();
        }
        return populateList;
    }

    public void getEventList() {
        URL = URL_BASE + month;
        db = FirebaseDatabase.getInstance();
        db.getReference("CompanyNames").addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void setEventList() {
        URL = URL_BASE + month;
        db = FirebaseDatabase.getInstance();
        db.getReference(month).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ds = dataSnapshot;
                new SetList().execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOG_TAG, "onCancelled", databaseError.toException());
            }
        });

    }

    private class FetchList extends AsyncTask<Void, Void, Void> {
        private ArrayList<Event> eventList;
        DatabaseReference dbEventList;

        @Override
        protected void onPreExecute() {
            eventList = new ArrayList<>();
            dbEventList = db.getReference("CompanyNames");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //JSOUP
                Document doc = Jsoup.connect(URL).get();
                Element table = doc.select("div#text").first();
                Iterator<Element> it = table.select("td[width=45%]").iterator();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM d,yyyy", Locale.CANADA);

                int count = 0;
                String companyName = "";
                String location = "";
                String time = "";
                Date date = null;
                URI website = null;

                while (it.hasNext()) {
                    String info = it.next().text();
                    switch (count % 5) {
                        case 0:
                            companyName = info;
                            break;
                        case 1:
                            try {
                                date = dateFormat.parse(info);
                            } catch (ParseException e) {
                                Log.e(LOG_TAG, "Unable to parse date: " + info, e.getCause());
                                e.printStackTrace();
                            }
                            break;
                        case 2:
                            time = info;
                            break;
                        case 3:
                            location = info;
                            break;
                        case 4:
                            if (!location.equals("")) {
                                Event event = new Event(companyName, location, time, date);
                                long voteFood, voteShirt;
                                if (!ds.hasChild(event.getDatabaseName())) {
                                    DatabaseReference dbEvent = dbEventList.child(event.getDatabaseName());
                                    dbEvent.child("voteFood").setValue(0);
                                    dbEvent.child("voteShirt").setValue(0);
                                    voteFood = 0;
                                    voteShirt = 0;
                                } else {
                                    voteFood = (long) ds.child(event.getDatabaseName()).child("voteFood").getValue();
                                    voteShirt = (long) ds.child(event.getDatabaseName()).child("voteShirt").getValue();
                                }
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
            listener.onEventListReceived(eventList);
        }

    }


    //ADMIN TOOL
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
                            //date = info;
                            break;
                        case 2:
                            //time = info;
                            break;
                        case 3:
                            //location = info;
                            break;
                        case 4:
                            //website = info;
                            if (!location.equals("")) {
                                int voteUp = 0;
                                int voteDown = 0;
                                //Event event = new Event(companyName, location, time, date);
                                //event.setVoteFood(voteUp);
                                //event.setVoteShirt(voteDown);

                                DatabaseReference dbEvent = dbEventList.child(companyName);
                                dbEvent.child("voteFood").setValue(0);
                                dbEvent.child("voteShirt").setValue(0);
                                //eventList.add(event);
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
}
