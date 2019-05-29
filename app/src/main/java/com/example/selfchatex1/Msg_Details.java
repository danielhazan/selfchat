package com.example.selfchatex1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Msg_Details extends AppCompatActivity {
    int msgToDlt;
    private MsgDetailsViewModel mViewModel;
    AppDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg__details);
        db = (AppDatabase) AppDatabase.getDatabase(this);

        msgToDlt = getIntent().getExtras().getInt("position");
//        mViewModel.adapter = (chatAdapter.chatAdapter1) getIntent().getSerializableExtra("adapter");

        mViewModel = ViewModelProviders.of(this).get(MsgDetailsViewModel.class);
        mViewModel.db = db;
        mViewModel.MsdIdToDlt = msgToDlt;
        TextView tv = findViewById(R.id.textView6);
        mViewModel.showMsgDetails(tv);
        Button btn = findViewById(R.id.button3);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Msg_Details.this);
                alertDialog.setTitle("popup message");
                alertDialog.setMessage("are you sure?");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mViewModel.delteMsg();
                        dialog.cancel();
                        finish();

                    }
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alertDialog.create();

                alert.show();

            }











        });
        // TODO: Use the ViewModel
    }



}

