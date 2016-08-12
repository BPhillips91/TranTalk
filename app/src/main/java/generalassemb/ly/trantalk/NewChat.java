package generalassemb.ly.trantalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by brendan on 8/10/16.
 */
public class NewChat extends AppCompatActivity {
    EditText newRoom;
    Button createRoom;
    DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats");
    DatabaseReference englishRef = FirebaseDatabase.getInstance().getReference("english");
    DatabaseReference spanishRef = FirebaseDatabase.getInstance().getReference("spanish");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newchat_activity);

        newRoom = (EditText) findViewById(R.id.chat_name);
        createRoom = (Button) findViewById(R.id.create_button);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = newRoom.getText().toString();
                if (!s.equals("")) {
                    createRoom.setVisibility(View.VISIBLE);
                } else
                    createRoom.setVisibility(View.INVISIBLE);
            }
        };
        newRoom.addTextChangedListener(textWatcher);
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newChatroom = newRoom.getText().toString();

                if (!newChatroom.equals("")) {
                    chatRef.child(newChatroom).setValue("");
                    englishRef.child(newChatroom).setValue("");
                    spanishRef.child(newChatroom).setValue("");
                    HomeActivity.chatRoom = newChatroom;
                    startActivity(new Intent(NewChat.this, ChatRoom.class));
                    finish();
                }
            }
        });
    }
}
