package com.ani.anichat;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

class ChatMessage {
    public boolean left;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }
}



public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;

    String receiver;

    ParseQuery<ParseObject> query;

    Handler handler = new Handler();
    int delay = 10000;
    Runnable runnable;

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onResume() {

        handler.postDelayed( runnable = new Runnable() {
            @Override
            public void run() {


                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if(e == null)
                        {
                            //clear list
                            chatArrayAdapter.clear();
                            //populate list
                            if(objects.size()>0)
                            {

                                for (ParseObject message: objects)
                                {
                                    String content = message.getString("Content");
                                    if(message.getString("Sender").equals(ParseUser.getCurrentUser().getUsername()))
                                    {
                                        chatArrayAdapter.add(new ChatMessage(false,content));
                                    }else
                                    {
                                        chatArrayAdapter.add(new ChatMessage(true,content));
                                    }

                                }

                            }


                        }else
                        {
                            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                handler.postDelayed(runnable,delay);

            }
        }, 0);


        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        receiver = intent.getStringExtra("username");

        ((TextView)findViewById(R.id.contactTextView)).setText(receiver);

        buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);


        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);


/*
        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });*/


        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("Sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("Receiver", receiver);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");
        query2.whereEqualTo("Receiver", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("Sender", receiver);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        query = ParseQuery.or(queries);

        query.orderByAscending("createdAt");







    }

    public void sendChatMessage(View view) {

        final String content = chatText.getText().toString();


        ParseObject message = new ParseObject("Message");
        message.put("Sender", ParseUser.getCurrentUser().getUsername());
        message.put("Receiver", receiver);
        message.put("Content", content);

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                {
                    chatArrayAdapter.add(new ChatMessage(false, content));
                    chatArrayAdapter.notifyDataSetChanged();
                    chatText.setText("");
                }
            }
        });

    }

    class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

        private TextView chatText;
        private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
        private Context context;

        @Override
        public void add(ChatMessage object) {
            chatMessageList.add(object);
            super.add(object);
        }

        @Override
        public void clear() {
            chatMessageList.clear();
            super.clear();
        }



        public ChatArrayAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.context = context;
        }

        public int getCount() {
            return this.chatMessageList.size();
        }

        public ChatMessage getItem(int index) {
            return this.chatMessageList.get(index);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ChatMessage chatMessageObj = getItem(position);
            View row = convertView;
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (chatMessageObj.left) {
                row = inflater.inflate(R.layout.left, parent, false);
            } else {
                row = inflater.inflate(R.layout.right, parent, false);
            }
            chatText = (TextView) row.findViewById(R.id.msgr);
            chatText.setText(chatMessageObj.message);
            return row;


        }
    }
}


