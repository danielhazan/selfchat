package com.example.selfchatex1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Parcelable;
import android.os.PersistableBundle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.Snackbar;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

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

    private chatAdapter.chatAdapter1 adapter = new chatAdapter.chatAdapter1(new ArrayList<String>());
    private RecyclerView recyclerView;
//    private ArrayList<String> strings = new ArrayList<>();
    private Parcelable recycleState;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> stringArrayList;
    AppDatabase db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,"databse-name").build();
//    MsgDao dao = db.msgDao().insertAll();/*todo continue persistence of text in db*/
    private int msgId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,
                false);
        final EditText editText = findViewById(R.id.edit_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler) ;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.callback = this;
        List<Msg> messegesList = db.msgDao().getAll();
        int sizeMsgList = db.msgDao().getNumofMsgs();
        android.util.Log.d(MainActivity.class.getName(),"current size of chat messages list: " +sizeMsgList );
        this.msgId = 0;

        initializeChat(editText);
        adapter.notifyDataSetChanged();



    }
    private void initializeChat(final EditText editText){
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(editText.getText().toString().equals(""))){
//                    TextView textView = findViewById(R.id.textView);
                    adapter.addItem(editText.getText().toString(),msgId);
                    Msg msg = new Msg();
                    msg.setMid(msgId);
                    msg.setMessage(editText.getText().toString());
                    db.msgDao().insertAll(msg);
                    msgId ++;
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
        outState.putParcelable("recyclestate",recycleState);
        outState.putStringArrayList("recyclerContent",adapter.getStrings());
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
//                strings1.remove(position);
                Msg msg = db.msgDao().findByMessageId(adapter.getMsgId(position));
                db.msgDao().delete(msg);
                adapter.removeItem(position);

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
    List<Msg> getAll();

    @Query("SELECT COUNT(*) FROM Msg")
    int getNumofMsgs();


    @Query("SELECT * FROM Msg WHERE Mid LIKE : msgId ")
    Msg findByMessageId(Integer msgId);

    @Insert
    void insertAll(Msg ... msgs);

    @Delete
    void delete(Msg msg);
}

@Database(entities = {Msg.class}, version = 1)
abstract class AppDatabase extends RoomDatabase{
    public abstract MsgDao msgDao();

}

