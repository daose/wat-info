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

    private enum RowType {
        EVENT,
        HEADER
    }

    private static class ViewHolder {
        TextView companyName;
        TextView location;
        TextView time;
        TextView date;
        TextView voteUp;
        TextView voteDown;
        ImageButton voteFoodButton;
        ImageButton voteShirtButton;
    }

    public EventArrayAdapter(Context context, ArrayList<Event> eventList) {
        super(context, 0, eventList);
        this.eventList = eventList;
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        if (eventList.get(position).isDateHeader()) {
            return RowType.HEADER.ordinal();
        } else {
            return RowType.EVENT.ordinal();
        }
    }

    //TODO: use viewholder after transition to description layout
    //TODO: gradient of colours for the shirt/food icon
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Event event = eventList.get(position);
        final String LOG_TAG = "position: " + position;
        ViewHolder holder = null;
        RowType rowType = RowType.values()[getItemViewType(position)];
        Log.d(LOG_TAG, "rowType: " + rowType.toString());

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case EVENT:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.event, parent, false);
                    holder.companyName = (TextView) convertView.findViewById(R.id.eventName);
                    holder.location = (TextView) convertView.findViewById(R.id.eventLocation);
                    holder.time = (TextView) convertView.findViewById(R.id.eventTime);
                    holder.voteUp = (TextView) convertView.findViewById(R.id.voteUp);
                    holder.voteDown = (TextView) convertView.findViewById(R.id.voteDown);
                    holder.voteFoodButton = (ImageButton) convertView.findViewById(R.id.voteFoodButton);
                    holder.voteShirtButton = (ImageButton) convertView.findViewById(R.id.voteShirtButton);
                    break;
                case HEADER:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.date, parent, false);
                    holder.date = (TextView) convertView.findViewById(R.id.date);
                    break;
                default:
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Animation buttonAnim = AnimationUtils.loadAnimation(convertView.getContext(), R.anim.anim_button);

        switch (rowType) {
            case EVENT:
                holder.companyName.setText(event.getName());
                holder.location.setText(event.getLocation());
                holder.time.setText(event.getTime());
                holder.voteUp.setText(String.valueOf(event.getVoteFood()));
                holder.voteDown.setText(String.valueOf(event.getVoteShirt()));
                break;
            case HEADER:
                holder.date.setText(event.getDate().toString());
                break;
            default:
                break;
        }

        //TODO: add voting capability in the detailed page
        /*
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
        */
        return convertView;
    }

    //TODO: use "Save data as transactions" - Firebase
    private void updateData(final Event event, final String childName, final int amount) {
        this.notifyDataSetChanged();

        FirebaseDatabase.getInstance().getReference("CompanyNames").child(event.getDatabaseName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long prev;
                prev = (long) dataSnapshot.child(childName).getValue();
                FirebaseDatabase.getInstance().getReference("CompanyNames").child(event.getDatabaseName()).child(childName).setValue(prev + amount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
