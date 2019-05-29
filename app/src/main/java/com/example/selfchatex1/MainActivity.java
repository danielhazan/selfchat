package com.example.selfchatex1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import static com.example.selfchatex1.LoginViewModel.AuthenticationState.AUTHENTICATED;
//import TaskCompleted.java;

public class MainActivity extends FragmentActivity
        implements chatAdapter.chatBoxClickCallback, TaskCompleted,Serializable {

    private LoginViewModel viewModel;
    private MsgDetailsViewModel msgViewModel;

    private chatAdapter.chatAdapter1 adapter = new chatAdapter.chatAdapter1(new ArrayList<String>(),new ArrayList<Integer>());
    private RecyclerView recyclerView;
    private Parcelable recycleState;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> stringArrayList;
    AppDatabase db ;
    public int msgId;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    // Create a new user with a first, middle, and last name
    Map<String, Object> user1 = new HashMap<>();

    @Override
    public void addToFirebase(Msg msg) {
        Map<String, Object> user = new HashMap<>();

        user.put("msg",msg.getMessage());
        user.put("id",msg.getMid());
        user.put("timeStamp",msg.getTimestamp());
        String reqString = Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();

        user.put("model phone", reqString);
        firebaseFirestore.collection("chats").document(Integer.toString(msg.getMid()))
                .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               Log.d("ADDED****", "SUCCESSFULLY ADDED " );
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("NOT ADDED", "Error writing document", e);
                    }
                });

    }

    @Override
    public void loadFromFS() {
        new loadFromFeirestore(this.firebaseFirestore).execute();
        refreshmsgList();
    }


    private static class insertLocalAsyncTask extends AsyncTask<Msg,Void,Void>{
        private MsgDao mAsyntaskDao;


        insertLocalAsyncTask(MsgDao dao){
            this.mAsyntaskDao = dao;

        }

        @Override
        protected Void doInBackground(Msg... msgs) {
           Msg msg =  mAsyntaskDao.findByMessageId(msgs[0].getMid());
           if (msg != null){
               return null;
           }
           else {
               mAsyntaskDao.insertAll(msgs[0]);
           }
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Msg,Void,Void>{
        private MsgDao mAsyntaskDao;
        private Context mContext;
        private TaskCompleted mCallback;

        insertAsyncTask(Context context,MsgDao dao){
            this.mAsyntaskDao = dao;
            this.mContext = context;
            this.mCallback = (TaskCompleted) context;
        }



        @Override
        protected Void doInBackground(Msg... msgs) {

            mCallback.addToFirebase(msgs[0]);

            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Msg,Void,Void>{
        private MsgDao mAsyntaskDao;

        deleteAsyncTask(MsgDao dao){
            this.mAsyntaskDao = dao;
        }

        @Override
        protected Void doInBackground(Msg... msgs) {
            mAsyntaskDao.delete(msgs[0]);
            return null;
        }
    }
    private static class deleteALLAsyncTask extends AsyncTask<Msg,Void,Void>{
        private MsgDao mAsyntaskDao;

        deleteALLAsyncTask(MsgDao dao){
            this.mAsyntaskDao = dao;
        }

        @Override
        protected Void doInBackground(Msg... msgs) {
            mAsyntaskDao.deleteAll();
            return null;
        }
    }

    private class findMadIdLAsyncTask extends AsyncTask<Msg,Void,Void> {
        private MsgDao mAsyntaskDao;


        findMadIdLAsyncTask(MsgDao dao) {
            this.mAsyntaskDao = dao;

        }

        @Override
        protected Void doInBackground(Msg... msgs) {
            if (!adapter.isEmpty())


                msgId =  mAsyntaskDao.findMaxMid() +1;

            else
            {
                msgId = 0;
            }
            return null;
        }
    }
    private static class deleteMeAsyncTask extends AsyncTask<Integer, Void,Void>{
        private MsgDao mAsyntaskDao;

        deleteMeAsyncTask(MsgDao msgDao){
            this.mAsyntaskDao = msgDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            Msg msg = mAsyntaskDao.findByMessageId(integers[0]);
            if(msg != null) {
                mAsyntaskDao.deleteMe(integers[0]);
            }
            else{
                return null;
            }
            return null;
        }
    }

    private static class findByIdAsyncTask extends AsyncTask<Integer, Void,Void>{
        private MsgDao mAsyntaskDao;
        private Context mContext;
        private TaskCompleted mCallback;

        findByIdAsyncTask(MsgDao msgDao){
            this.mAsyntaskDao = msgDao;

        }

        @Override
        protected Void doInBackground(Integer... integers) {
            mAsyntaskDao.deleteMe(integers[0]);
            return null;
        }
    }

    private class loadFromFeirestore extends AsyncTask<Void, Void,Void> {
        private FirebaseFirestore firebaseFirestore;


        loadFromFeirestore(FirebaseFirestore firestore) {
            this.firebaseFirestore = firestore;

        }

        @Override
        protected Void doInBackground(Void ... voids) {
            firebaseFirestore.collection("chats").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {


                                Integer maxId = 0;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Msg newMsg = new Msg();
                                    for (Map.Entry<String, Object> entry : document.getData().entrySet()) {
                                        if (entry.getKey().equals("msg")) {
                                            newMsg.message = (String) entry.getValue();
                                        }
                                        if (entry.getKey().equals("id")) {
                                            newMsg.Mid = ((Long) entry.getValue()).intValue();
                                            if (newMsg.Mid > maxId) {
                                                maxId = newMsg.Mid;
                                            }
                                        }
                                        if (entry.getKey().equals("timeStamp")) {
                                            newMsg.timestamp = (String) entry.getValue();
                                        }


                                    }
                                    insertToLocalDB(newMsg); // check what happen if the msg already exists in local DB

                                    Log.d("DOC*****", document.getId() + " => " + document.getData());
                                }
                                msgId = maxId + 1;

                            } else {
                                Log.w("NOT OK****", "Error getting documents.");

                            }

                        }
                    });
            return null;

        }

    }
    public void showWelcomeMessage(){
        final TextView welcomeTextView = findViewById(R.id.textView5);
        DocumentReference docRef = firebaseFirestore.collection("chats").document("Defaults");
        final String userName ;
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();

                    welcomeTextView.setText("hello " + String.valueOf(doc.getString("username")) + "!");
                }
            }
        });



    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        msgViewModel = ViewModelProviders.of(this).get(MsgDetailsViewModel.class);


        //welcome to user if authenticated -->
        TextView user_name = findViewById(R.id.textView5);
        Bundle extras = getIntent().getExtras();
//        if(viewModel.auth.equals("AUTH") || viewModel.auth.equals("INVAL")){
            if(extras != null) {
                String str = extras.getString("value");
                if (str != null) {
                    user_name.setText("hello " + str + "!");
//                    showWelcomeMessage();
                }
            }
//        }

//        viewModel.authenticationState.observe(this,
//                new Observer<LoginViewModel.AuthenticationState>() {
//                    @Override
//                    public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
//                        switch (authenticationState) {
//                            case AUTHENTICATED:
//                                showWelcomeMessage();
//                                break;
//                            case UNAUTHENTICATED:
//
//                                break;
//                            case INVALID_AUTHENTICATION://on pressed "Skip"
//                                break;
//                        }
//                    }
//                });
//





        db = AppDatabase.getDatabase(this);    /*todo*/
        deleteAll();

        msgViewModel.db = this.db;

        //loading from firestore to local room
        firebaseFirestore.collection("chats").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {


                            Integer maxId = 0;
                            for   (QueryDocumentSnapshot document : task.getResult()) {
                                Msg newMsg = new Msg();
                                for (Map.Entry<String, Object> entry : document.getData().entrySet()) {
                                    if(entry.getKey().equals("msg")){
                                        newMsg.message = (String) entry.getValue();
                                    }
                                    if(entry.getKey().equals( "id")){
                                        newMsg.Mid = ((Long) entry.getValue()).intValue();
                                        if (newMsg.Mid > maxId) {
                                            maxId = newMsg.Mid;
                                        }
                                    }
                                    if(entry.getKey().equals( "timeStamp")){
                                        newMsg.timestamp = (String) entry.getValue();
                                    }

                                }
                                insertToLocalDB(newMsg); // check what happen if the msg already exists in local DB

                                Log.d("DOC*****", document.getId() + " => " + document.getData());
                                }
                            msgId = maxId +1;

                            }
                        else{
                            Log.w("NOT OK****", "Error getting documents." );

                        }

                    }
                });


        layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,
                false);
        final EditText editText = findViewById(R.id.edit_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler) ;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.callback = this;
        msgViewModel.adapter = this.adapter;
//        List<Msg> messegesList = db.msgDao().getAll();
        db.msgDao().getNumofMsgs().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer integer) {
                Log.d(MainActivity.class.getName(),"current size of chat messages list: " +integer );

            }
        });
        db.msgDao().getAll().observe(this, new Observer<List<Msg>>() {
            @Override
            public void onChanged(@Nullable final List<Msg> msgs) {
                adapter.setWords(msgs);
            }
        });

        adapter.notifyDataSetChanged();
        if(adapter.isEmpty()){
            msgId = 0;
        }

        loadfromFS();
        refreshmsgList();
        Log.d(MainActivity.class.getName(),"********************************  " + msgId );
        initializeChat(editText);
        adapter.notifyDataSetChanged();
        Log.d(MainActivity.class.getName(),"********************************  " + msgId );

        Log.d(MainActivity.class.getName(),"********************************  " + msgId );
        getTableAsString(db,"Msg");//debug purposes
        loadfromFS();
        refreshmsgList();



        firebaseFirestore.collection("chats").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {

                loadFromFS();
                refreshmsgList();

                //another option -- load directly documents to db via Document.toObject(Msg.class)


//                if(e != null){
//                    Log.w("TAG", "listen:error", e);
//                    return;
//                }
//                List<DocumentChange> dc = queryDocumentSnapshots.getDocumentChanges();
//                for (DocumentChange doc : dc){
//                    Msg msg = doc.getDocument().toObject(Msg.class);
//                    insertToLocalDB(msg);
//                    refreshmsgList();
//                }
//                adapter.notifyDataSetChanged();

            }
        });


    }

    void refreshmsgList(){
        db.msgDao().getAll().observe(this, new Observer<List<Msg>>() {
            @Override
            public void onChanged(@Nullable final List<Msg> msgs) {
                adapter.setWords(msgs);
            }
        });

        adapter.notifyDataSetChanged();
    }

    void loadfromFS(){new loadFromFeirestore(this.firebaseFirestore).execute();}

    void insertToLocalDB(Msg msg) {
        new insertLocalAsyncTask(db.msgDao()).execute(msg);
    }
    void insert(Msg msg){
        new insertAsyncTask(MainActivity.this,db.msgDao()).execute(msg);
    }

    void delete(Msg msg){
        new deleteAsyncTask((db.msgDao())).execute(msg);
    }
    void deleteAll(){
        new deleteALLAsyncTask(db.msgDao()).execute();
    }

    void deleteMe(Integer Mid){
        new deleteMeAsyncTask(db.msgDao()).execute(Mid);
    }

    void findMaxMid() throws ExecutionException, InterruptedException {
        new findMadIdLAsyncTask(db.msgDao()).execute().get();

    }

    private void initializeChat(final EditText editText){
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(editText.getText().toString().equals(""))){

                    loadfromFS();
                    refreshmsgList();
                    adapter.addItem(editText.getText().toString(),msgId);
                    Msg msg = new Msg();
                    Log.d("DBTABLE ***************", " " + msgId  + " " + msg.getMid());//debug
                    msg.setMid(msgId);
                    Long tsLong = System.currentTimeMillis()/1000;
                    String date = getDateCurrentTimeZone(tsLong);
                    msg.setTimeStamp(date);
                    Log.d("DBTABLE ***************", " " + msgId);//debug
                    msg.setMessage(editText.getText().toString());
                    getTableAsString(db,"Msg");//debug purposes
                    Log.d("DBTABLE ***************", " " + msg.getMid());//debug
                    insert(msg);
                    adapter.addItem(msg.getMessage(),msg.getMid());;
                    adapter.notifyDataSetChanged();
                    msgId ++;
                    getTableAsString(db,"Msg");//debug purposes

                    editText.setText("");
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);

                }
                else{
                    View mainView = findViewById(R.id.main_layout_id);
                    String msg = "you can't send an empty message";
                    int duration = Snackbar.LENGTH_SHORT;
                    Snackbar.make(mainView,msg,duration).show();

                }
            }
        });

    }

    public  String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }


    private void removeMsgFromDB(Integer position){     // moved to MsgDetailsViewModel class -->


        //delete from local db
        deleteMe(adapter.getMsgId(position));


        Integer DocToDelete = adapter.getMsgId(position);

        firebaseFirestore.collection("chats").document(Integer.toString(DocToDelete))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REMOVED FROM FIREBASE","*****");
                    }
                });

    }
    @Override
    protected void onResume(){
        super.onResume();
        if(stringArrayList!= null){
            adapter.getStrings().addAll(stringArrayList);
        }
        if(recycleState != null){
            layoutManager.onRestoreInstanceState(recycleState);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        recycleState =layoutManager.onSaveInstanceState();

    }
    @Override
    protected void onRestoreInstanceState(Bundle state){
        super.onRestoreInstanceState(state);
        if(state != null){
            recycleState = state.getParcelable("recyclestate");
            stringArrayList = state.getStringArrayList("recyclerContent");
        }
    }

    @Override
    public void onChatBoxClick(final List<String> strings1, final int position) {

        Intent intent = new Intent(this, Msg_Details.class);
        intent.putExtra("position", adapter.getMsgId(position));
//        intent.putExtra("adapter", adapter);
        startActivity(intent);
//        Bundle bundle = new Bundle();
//        Fragment fr = new MsgDetails();
//        bundle.putInt("position" , position);
//        fr.setArguments(bundle);

//        AlertDialog.Builder alertDialog = new AlertDialog.Builder((MainActivity)this);
//        alertDialog.setTitle("popup message");
//        alertDialog.setMessage("are you sure?");
//        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                removeMsgFromDB(position);
//
//
//                adapter.removeItem(position);
//
//                dialog.cancel();
//            }
//        });
//        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        AlertDialog alert = alertDialog.create();
//        alert.show();


    }
    public String getTableAsString(AppDatabase db, String tableName) {
        Log.d("DBtable", "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.query("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        Log.d("DBTABLE ***************", tableString);

        return tableString;
    }

}

/**building the Room local DB for selfChat*/
