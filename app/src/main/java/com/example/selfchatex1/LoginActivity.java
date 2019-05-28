package com.example.selfchatex1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel mViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);


        //before presenting login search the user name in firestore initially. if exists - go to mainAct
        DocumentReference docRef = mViewModel.firebaseFirestore.collection("chats").document("Defaults");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().getData().size() > 0) {
                    DocumentSnapshot doc = task.getResult();

                    if (doc.getData() != null) {
                        mViewModel.auth = "AUTH";
                        Intent inte = new Intent(LoginActivity.this, MainActivity.class);
                        inte.putExtra("value", doc.getData().toString());
                        startActivity(inte);
                    }
                }


            }
        });









        final EditText editText = findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                View b = findViewById(R.id.button2);
                b.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = String.valueOf(editText.getText());
                mViewModel.authenticate(text,null);

            }
        });

        TextView skip = findViewById(R.id.textView3);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.authenticate(null,null);

            }
        });

        //maybe put these lines inside onclick() after authentication-->/todo

        final Intent inte = new Intent(LoginActivity.this, MainActivity.class);




        mViewModel.authenticationState.observe(this,
                new Observer<LoginViewModel.AuthenticationState>() {
                    @Override
                    public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
                        switch (authenticationState) {
                            case AUTHENTICATED:
                                inte.putExtra("value", mViewModel.username);
                                finish();

                                startActivity(inte);
                                break;
                            case INVALID_AUTHENTICATION:
                                inte.putExtra("value", mViewModel.username);
                                finish();

                                startActivity(inte);
                                break;
                        }
                    }
                });


    }
}
