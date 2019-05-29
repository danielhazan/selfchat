package com.example.selfchatex1;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import androidx.lifecycle.LiveData;
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

/**building the Room local DB for selfChat*/

@Entity
class Msg implements Serializable {
    @PrimaryKey
    public int Mid;

    @ColumnInfo(name = "chatBox")
    public String message;

    @ColumnInfo(name = "date")
    public String timestamp;



    public void setMid(int id){
        this.Mid = id;
    }
    public void setTimeStamp(String currentTime){
        this.timestamp = currentTime;
    }
    public void setMessage(String msg){
        this.message = msg;
    }


    public int getMid(){ return this.Mid; }
    public String getMessage(){ return this.message;}
    public String getTimestamp(){ return this.timestamp;}
}

@Dao
interface MsgDao extends Serializable {

    @Query("SELECT * FROM Msg")
    LiveData<List<Msg>> getAll();

    @Query("SELECT COUNT(*) FROM Msg")
    LiveData<Integer> getNumofMsgs();


    @Query("SELECT * FROM Msg WHERE Mid LIKE :msgId ")
    Msg findByMessageId(Integer msgId);

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
abstract class AppDatabase extends RoomDatabase implements Serializable{
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

