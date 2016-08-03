package generalassemb.ly.trantalk;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by brendan on 7/28/16.
 */
public class Database {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

 public   DatabaseReference messageDB = firebaseDatabase.getReference("messages");
 public   DatabaseReference channelDB = firebaseDatabase.getReference("channels");
 public   DatabaseReference translatedDB= firebaseDatabase.getReference("translated");

    //TODO: Set up event listeners for individual users and handel the information thats retrived





}
