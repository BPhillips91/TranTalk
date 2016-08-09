package generalassemb.ly.trantalk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by brendan on 8/9/16.
 */
public class ChatRoom extends AppCompatActivity {
    private EditText messageInput;
    private Button sendButton;
    private RecyclerView mRecyclerView;
    String userName, roomName, userLang;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("chats");
    private  FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatroom);
        messageInput = (EditText) findViewById(R.id.new_message);
        sendButton = (Button) findViewById(R.id.send_button);
        mRecyclerView = (RecyclerView) findViewById(R.id.messages);
        userName = auth.getCurrentUser().getDisplayName();
        userLang ="en";
        setTitle("chat1");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String originalMsg = messageInput.getText().toString();
                DatabaseReference messageRef = root.child("chat1");

                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("user",userName);
                map.put("message",originalMsg);
                map.put("userLan",userLang);
                String key = messageRef.push().getKey();
                messageRef.child(key).setValue(map);

                if(userLang.equalsIgnoreCase("en")){
                    DatabaseReference englishRef = FirebaseDatabase.getInstance().getReference("english");
                    englishRef.child(key).setValue(map);
                    Translate.translateToSpanish(originalMsg,key);

                }else if(userLang.equalsIgnoreCase("es")){
                    DatabaseReference spanishRef = FirebaseDatabase.getInstance().getReference("spanish");
                    spanishRef.child(key).setValue(map);
                    Translate.translateToEnglish(originalMsg, key);
                }

            }
        });
    }




}
