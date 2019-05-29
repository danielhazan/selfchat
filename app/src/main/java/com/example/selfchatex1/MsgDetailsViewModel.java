package com.example.selfchatex1;

import android.os.AsyncTask;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.PublicKey;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class MsgDetailsViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    FirebaseFirestore firebaseFirestore ;
    int MsdIdToDlt;
    AppDatabase db;
    chatAdapter.chatAdapter1 adapter;

    public MsgDetailsViewModel(){
        firebaseFirestore = FirebaseFirestore.getInstance();




    }

    public void  showMsgDetails(final TextView tv){
        final StringBuilder details = new StringBuilder();

        firebaseFirestore.collection("chats").document(String.valueOf(MsdIdToDlt)).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            for (Map.Entry<String, Object> entry : doc.getData().entrySet()) {
                                if(entry.getKey().equals("msg")){
                                    details.append("content: ").append((String)entry.getValue()).append("\n");
//                                    tv.setText((String)entry.getValue());
                                }
//                                if(entry.getKey().equals( "id")){
//                                    details.append("id: ").append((String) String.valueOf(entry.getValue())).append("\n");
////                                    tv.setText((Long) entry.getValue()).intValue();
//
//                                }
                                if(entry.getKey().equals( "timeStamp")){
                                    details.append("date: ").append((String) entry.getValue());
                                }
                                if(entry.getKey().equals( "model phone")){
                                    details.append("sent from: ").append((String) entry.getValue());
                                }

                            }

                        }
                        tv.setText("message details: \n" +  details.toString());
                    }
                });

    }

    private static class deleteMeAsyncTask extends AsyncTask<Integer, Void,Void> {
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
    void deleteMe(Integer Mid){
        new deleteMeAsyncTask(db.msgDao()).execute(Mid);
    }

    private void removeMsgFromDB(Integer position){


        //delete from local db
        deleteMe(position);




        firebaseFirestore.collection("chats").document(Integer.toString(position))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("REMOVED FROM FIREBASE","*****");
                    }
                });

    }

    public void delteMsg(){
        removeMsgFromDB(MsdIdToDlt);
//        adapter.removeItem(MsdIdToDlt);
    }
}
