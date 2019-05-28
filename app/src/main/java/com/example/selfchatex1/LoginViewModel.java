package com.example.selfchatex1;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }


    final MutableLiveData<AuthenticationState> authenticationState = new MutableLiveData<>();
    String username ;
    FirebaseFirestore firebaseFirestore ;
    String auth;


    public LoginViewModel() {
        // In this example, the user is always unauthenticated when MainActivity is launched
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
        this.username = "";
        this.firebaseFirestore  = FirebaseFirestore.getInstance();
        this.auth = "UNAUTHENTICATED";
    }

    public void authenticate(String username, String password) {
        if (passwordIsValidForUsername(username, password)) {
            this.username = username;
            authenticationState.setValue(AuthenticationState.AUTHENTICATED);
            this.auth = "AUTH";
            Map<String, Object> user = new HashMap<>();
            user.put("username", this.username);
            firebaseFirestore.collection("chats").document("Defaults")
                    .set(user, SetOptions.mergeFields())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ADDED****", " USERNAME SUCCESSFULLY ADDED " );
                        }
                    });
        } else {
            authenticationState.setValue(AuthenticationState.INVALID_AUTHENTICATION);
            this.auth = "INVAL";
        }
    }





    public void refuseAuthentication() {
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }

    private boolean passwordIsValidForUsername(String username, String password) {
        if (username == null){
            return false;
        }
        return true; //todo

    }

}
