package com.example.selfchatex1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
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

public class MainActivity extends AppCompatActivity
        implements chatAdapter.chatBoxClickCallback{

    private chatAdapter.chatAdapter1 adapter = new chatAdapter.chatAdapter1(new ArrayList<String>(),new ArrayList<Integer>());
    private RecyclerView recyclerView;
//    private ArrayList<String> strings = new ArrayList<>();
    private Parcelable recycleState;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> stringArrayList;
//    AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"databse-name").build();
    AppDatabase db ;
    public int msgId;

    private static class insertAsyncTask extends AsyncTask<Msg,Void,Void>{
        private MsgDao mAsyntaskDao;

        insertAsyncTask(MsgDao dao){
            this.mAsyntaskDao = dao;
        }

        @Override
        protected Void doInBackground(Msg... msgs) {
            mAsyntaskDao.insertAll(msgs[0]);
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
//            msgId =  mAsyntaskDao.findMaxMid() +1;
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
            mAsyntaskDao.deleteMe(integers[0]);
            return null;
        }
    }


//    private insertAsyncTask insertAsyncTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getDatabase(this);    /*todo*/

//        deleteAll();

        layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,
                false);
        final EditText editText = findViewById(R.id.edit_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler) ;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.callback = this;
//        List<Msg> messegesList = db.msgDao().getAll();
        db.msgDao().getNumofMsgs().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer integer) {
                android.util.Log.d(MainActivity.class.getName(),"current size of chat messages list: " +integer );

            }
        });
        db.msgDao().getAll().observe(this, new Observer<List<Msg>>() {
            @Override
            public void onChanged(@Nullable final List<Msg> msgs) {
                adapter.setWords(msgs);
            }
        });
//        int sizeMsgList = db.msgDao().getNumofMsgs();
//        android.util.Log.d(MainActivity.class.getName(),"current size of chat messages list: " +sizeMsgList );
        adapter.notifyDataSetChanged();
        try {
            findMaxMid();  /*todo*/
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        msgId++;
        Log.d(MainActivity.class.getName(),"********************************  " + msgId );
        initializeChat(editText);
        adapter.notifyDataSetChanged();
        Log.d(MainActivity.class.getName(),"********************************  " + msgId );
        try {
            findMaxMid();  /*todo*/
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        msgId++;
        Log.d(MainActivity.class.getName(),"********************************  " + msgId );
        getTableAsString(db,"Msg");//debug purposes




    }
    void insert(Msg msg){
        new insertAsyncTask(db.msgDao()).execute(msg);
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
//                    TextView textView = findViewById(R.id.textView);

                    try {
                        findMaxMid();  /*todo*/
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    adapter.addItem(editText.getText().toString(),msgId);
                    Msg msg = new Msg();
                    Log.d("DBTABLE ***************", " " + msgId  + " " + msg.getMid());//debug
                    msg.setMid(msgId);
                    Log.d("DBTABLE ***************", " " + msgId);//debug
                    msg.setMessage(editText.getText().toString());
//                    db.msgDao().insertAll(msg);
                    getTableAsString(db,"Msg");//debug purposes
                    Log.d("DBTABLE ***************", " " + msg.getMid());//debug
                    insert(msg);
                    msgId ++;
                    getTableAsString(db,"Msg");//debug purposes
//                    adapter.addItem("\n");
//                    textView.append("\n");
//                    textView.append(editText.getText().toString());
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
    private void removeMsgFromDB(Integer position){
//        db.msgDao().findByMessageId(adapter.getMsgId(position)).observe(this, new Observer<Msg>() {
//            @Override
//            public void onChanged(Msg msg) {
//                delete(msg);
//            }
//        });
        deleteMe(adapter.getMsgId(position));

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
    public void onChatBoxClick(final List<String> strings1, final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder((MainActivity)this);
        alertDialog.setTitle("popup message");
        alertDialog.setMessage("are you sure?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeMsgFromDB(position);

//                strings1.remove(position);
//                final Msg[] msg1 = new Msg[1];
//                db.msgDao().findByMessageId(adapter.getMsgId(position)).observe(this,new Observer<Msg>() {
//                    @Override
//                    public void onChanged(Msg msg) {
//                        msg1[0] = msg;
//                    }
//                });
//
////                Msg msg = db.msgDao().findByMessageId(adapter.getMsgId(position));
////                db.msgDao().delete(msg);
//                delete(msg1[0]);
                adapter.removeItem(position);
                try {
                    findMaxMid();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                dialog.cancel();
            }
        });
//        alertDialog.show();
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();





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
@Entity
class Msg{
    @PrimaryKey
    public int Mid;

    @ColumnInfo(name = "chatBox")
    public String message;

    public void setMid(int id){
        this.Mid = id;
    }
    public void setMessage(String msg){
        this.message = msg;
    }
    public int getMid(){ return this.Mid; }
    public String getMessage(){ return this.message;}
}

@Dao
interface MsgDao{

    @Query("SELECT * FROM Msg")
    LiveData<List<Msg>> getAll();

    @Query("SELECT COUNT(*) FROM Msg")
    LiveData<Integer> getNumofMsgs();


    @Query("SELECT * FROM Msg WHERE Mid LIKE :msgId ")
    LiveData<Msg> findByMessageId(Integer msgId);

    @Query("SELECT MAX(Mid) FROM Msg")
    Integer findMaxMid();

    @Query("DELETE FROM Msg ")
    void deleteAll();



    @Query("DELETE FROM Msg WHERE Mid LIKE :msgID")

    void deleteMe(Integer msgID);

    @Insert
    void insertAll(Msg ... msgs);

    @Delete
    void delete(Msg msg);
}

@Database(entities = {Msg.class}, version = 1)
abstract class AppDatabase extends RoomDatabase{
    public abstract MsgDao msgDao();
    private static volatile AppDatabase INSTANCE;
    static AppDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (AppDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"chat- database").build();

                }
            }
        }
        return INSTANCE;
    }


}
