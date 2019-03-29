package com.example.selfchatex1;

import android.os.PersistableBundle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private chatAdapter.chatAdapter1 adapter = new chatAdapter.chatAdapter1(new ArrayList<String>());
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText editText = findViewById(R.id.edit_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,
                false));
        recyclerView.setAdapter(adapter);

        initializeChat(editText);
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

                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
