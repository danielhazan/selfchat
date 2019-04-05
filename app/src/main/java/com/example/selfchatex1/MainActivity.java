package com.example.selfchatex1;

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

public class MainActivity extends AppCompatActivity {
    private chatAdapter.chatAdapter1 adapter = new chatAdapter.chatAdapter1(new ArrayList<String>());
    private RecyclerView recyclerView;
    private ArrayList<String> strings = new ArrayList<>();
    private Parcelable recycleState;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> stringArrayList;


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
                    adapter.addItem(editText.getText().toString());
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
}
