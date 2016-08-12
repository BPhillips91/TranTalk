package generalassemb.ly.trantalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.email.SignInActivity;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    String TAG = "HOME";
    public static String currentUser;
    public static String userLang;
    public static String chatRoom;

    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference chats = firebaseDatabase.child("chats");
    private List<String> chatRooms;
    private ChildEventListener listener;

    ListView chatList;
    ArrayAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        chatList = (ListView) findViewById(R.id.chat_list);
        chatRooms= new ArrayList<>();
        mAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,chatRooms);

        currentUser = "";
        chatRoom = "";
        userLang = "";

        Locale locale = Resources.getSystem().getConfiguration().locale;
        String language = locale.toString();
        userLang = Character.toString(language.charAt(0))+Character.toString(language.charAt(1));

        logIn();
        getChats();

        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chatRoom = chatList.getItemAtPosition(i).toString();
                startActivity(new Intent(HomeActivity.this, ChatRoom.class));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NewChat.class);
                startActivity(intent);

            }
        });
    }



    private void logIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            currentUser = auth.getCurrentUser().getDisplayName();
            setTitle("What's up, "+currentUser+"!");
        } else {
            // not signed in so start Authentication
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setTheme(R.style.RedandBlack)
                            .build(), 1);

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // user is signed in!
                    setTitle("Welcome to Omni-Chat!");
            } else {
                logIn();
            }
        }
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign out");
        builder.setMessage("Are you sure you want to signout?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AuthUI.getInstance()
                        .signOut(HomeActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();}});
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void getChats() {
          listener= new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                dataSnapshot.getChildren();
                String chatroom= dataSnapshot.getKey();
                chatRooms.add(chatroom);
                chatList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };chats.addChildEventListener(listener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout:
               signOut();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        chats.removeEventListener(listener);
    }
}
