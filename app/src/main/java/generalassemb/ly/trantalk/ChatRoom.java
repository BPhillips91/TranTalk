package generalassemb.ly.trantalk;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by brendan on 8/9/16.
 */
public class ChatRoom extends AppCompatActivity {
    EditText messageInput;
    Button sendButton;
    RecyclerView mRecyclerView;
    String userName = "", userLang;
    TextWatcher messageWatcher;
    FirebaseRecyclerAdapter mAdapter;
    LinearLayoutManager layoutManger;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference("chats");
    DatabaseReference messageRef = root.child(HomeActivity.chatRoom);
    DatabaseReference englishRef = FirebaseDatabase.getInstance().getReference("english").child(HomeActivity.chatRoom);
    DatabaseReference spanishRef = FirebaseDatabase.getInstance().getReference("spanish").child(HomeActivity.chatRoom);
    FirebaseAuth auth = FirebaseAuth.getInstance();

    ValueEventListener messageListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroom);
        messageInput = (EditText) findViewById(R.id.new_message);
        sendButton = (Button) findViewById(R.id.send_button);
        mRecyclerView = (RecyclerView) findViewById(R.id.messages);
        mRecyclerView.setHasFixedSize(true);
        layoutManger = new LinearLayoutManager(this);
        layoutManger.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManger);
        messageWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = messageInput.getText().toString();
                if (!s.equals("")) {
                    sendButton.setVisibility(View.VISIBLE);
                } else {
                    sendButton.setVisibility(View.INVISIBLE);
                }
            }
        };
        userLang = HomeActivity.userLang;
        userName = auth.getCurrentUser().getDisplayName();
        setTitle(HomeActivity.chatRoom);
        messageInput.addTextChangedListener(messageWatcher);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        messageRef.addValueEventListener(messageListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateAdapter();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.original:
                setTitle(HomeActivity.chatRoom);
                updateAdapter();
                break;
            case R.id.spanish:
                setTitle(HomeActivity.chatRoom + " (Spanish)");
                updateAdapter();
                break;
            case R.id.english:
                setTitle(HomeActivity.chatRoom + " (English)");
                updateAdapter();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void updateAdapter() {
        mAdapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>
                (Chat.class, R.layout.message_view, ChatHolder.class, messageRef) {
            @Override
            public void populateViewHolder(ChatHolder chatMessageViewHolder, Chat chatMessage, int position) {
                String user = chatMessage.getUser();
                Date current = new Date(chatMessage.getTimestamp());
                SimpleDateFormat format = new SimpleDateFormat("MMM dd h:mm");
                String date = format.format(current);
                if (!user.equalsIgnoreCase(userName)) {
                    chatMessageViewHolder.setGravity("");
                    chatMessageViewHolder.setTime(date);
                    chatMessageViewHolder.setName(chatMessage.getUser());
                    chatMessageViewHolder.setText(chatMessage.getMessage());
                } else if (user.equalsIgnoreCase(userName)) {
                    chatMessageViewHolder.setGravity("right");
                    chatMessageViewHolder.setTime(date);
                    chatMessageViewHolder.setName("");
                    chatMessageViewHolder.setText(chatMessage.getMessage());
                }else{
                    Log.d("USER", "populateViewHolder: New User");
                }

            }
        };
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void sendMessage() {
        long currentTime = System.currentTimeMillis();
        String originalMsg = messageInput.getText().toString();
        messageInput.setText("");
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user", userName);
        map.put("message", originalMsg);
        map.put("userLan", userLang);
        map.put("timestamp", currentTime);
        String key = messageRef.push().getKey();
        messageRef.child(key).setValue(map);
        if (userLang.equalsIgnoreCase("en")) {
            englishRef.child(key).setValue(map);
            Translate.translateToSpanish(originalMsg, key, currentTime);

        } else if (userLang.equalsIgnoreCase("es")) {
            spanishRef.child(key).setValue(map);
            Translate.translateToEnglish(originalMsg, key, currentTime);
        }
        updateAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
        messageRef.removeEventListener(messageListener);
        messageInput.removeTextChangedListener(messageWatcher);
    }


}
