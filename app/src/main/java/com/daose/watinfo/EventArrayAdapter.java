package com.daose.watinfo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by student on 25/06/16.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> eventList;

    public EventArrayAdapter(Context context, ArrayList<Event> eventList) {
        super(context, 0, eventList);
        this.eventList = eventList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Event event = eventList.get(position);
        final String LOG_TAG = "position: " + position;


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event, parent, false);
        }
        final Animation buttonAnim = AnimationUtils.loadAnimation(convertView.getContext(), R.anim.anim_button);

        TextView companyName = (TextView) convertView.findViewById(R.id.eventName);
        TextView location = (TextView) convertView.findViewById(R.id.eventLocation);
        TextView time = (TextView) convertView.findViewById(R.id.eventTime);
        TextView date = (TextView) convertView.findViewById(R.id.eventDate);
        TextView voteUp = (TextView) convertView.findViewById(R.id.voteUp);
        TextView voteDown = (TextView) convertView.findViewById(R.id.voteDown);

        final ImageButton voteFoodButton = (ImageButton) convertView.findViewById(R.id.voteFoodButton);
        final ImageButton voteShirtButton = (ImageButton) convertView.findViewById(R.id.voteShirtButton);

        voteFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteFoodButton.startAnimation(buttonAnim);
                if (!event.isFoodVoted()) {
                    event.setVoteFood(event.getVoteFood() + 1);
                    event.setFoodVoted(true);
                    updateData(event, "voteFood", 1);
                } else {
                    event.setVoteFood(event.getVoteFood() - 1);
                    event.setFoodVoted(false);
                    updateData(event, "voteFood", -1);
                }
            }
        });

        voteShirtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteShirtButton.startAnimation(buttonAnim);
                if (!event.isShirtVoted()) {
                    event.setVoteShirt(event.getVoteShirt() + 1);
                    event.setShirtVoted(true);
                    updateData(event, "voteShirt", 1);
                } else {
                    event.setVoteShirt(event.getVoteShirt() - 1);
                    event.setShirtVoted(false);
                    updateData(event, "voteShirt", -1);
                }
            }

        });


        companyName.setText(event.getName());
        location.setText(event.getLocation());
        time.setText(event.getTime());
        date.setText(event.getDate());
        voteUp.setText(String.valueOf(event.getVoteFood()));
        voteDown.setText(String.valueOf(event.getVoteShirt()));

        return convertView;
    }

    private void updateData(final Event event, final String childName, final int amount) {
        this.notifyDataSetChanged();

        FirebaseDatabase.getInstance().getReference(EventListActivity.month).child(event.getDatabaseName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long prev;
                prev = (long) dataSnapshot.child(childName).getValue();
                FirebaseDatabase.getInstance().getReference(EventListActivity.month).child(event.getDatabaseName()).child(childName).setValue(prev + amount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
