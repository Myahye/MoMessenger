package com.example.moman.myfirstapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText user_password, user_email, user_password_dup, user_name;
    private FirebaseAuth mAuth;
    private DatabaseReference storeDefaultUserData;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sign_up);
        loadingBar = new ProgressDialog(this);


        //password
        user_password = (EditText) findViewById(R.id.signUp_password);
        user_password_dup = (EditText) findViewById(R.id.signUp_password_dup);
        //email
        user_email = (EditText) findViewById(R.id.signUp_email);

        //username
        user_name = (EditText) findViewById(R.id.signUp_username);
        //We have the username, password, and email
        //Now do some error checking
        mAuth = FirebaseAuth.getInstance();
    }

    public void registration(View view){
        final String message_username = user_name.getText().toString();
        String message_useremail = user_email.getText().toString();
        String message_password = user_password.getText().toString();
        String message_password_dup = user_password_dup.getText().toString();

        //if Email Is Empty
        if(message_useremail.isEmpty()){
            user_email.setError("An email is required!");
            user_email.requestFocus();
            return;
        }

        if(message_username.isEmpty()){
            user_name.setError("A username is required!");
            user_name.requestFocus();
            return;
        }

        if(message_username.length() < 3){
            user_name.setError("The minimum length of the password is 3.");
            user_name.requestFocus();
            return;
        }

        if(message_username.length() > 10){
            user_name.setError("The maximum length of the username is 10.");
            user_name.requestFocus();
            return;
        }
        //if email is valid
        if(!Patterns.EMAIL_ADDRESS.matcher(message_useremail).matches()){
            user_email.setError("Please enter a valid email address.");
            user_email.requestFocus();
            return;
        }

        //if password is empty
        if(message_password.isEmpty()){
            user_password.setError("A password is required!");
            user_password.requestFocus();
            return;
        }

        //min length of password for firebase auth is 6
        if(message_password.length() < 6){
            user_password.setError("The minimum length of the password is 6.");
            user_password.requestFocus();
            return;
        }

        //if passwords are not equivalent
        if(!message_password.equals(message_password_dup)){
            user_password_dup.setError("The passwords are not equivalent.");
            user_password_dup.requestFocus();
            return;
        }

        loadingBar.setTitle("Registering user..");
        loadingBar.show();
        //dealing with a valid email/password combination
        Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(message_useremail, message_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //store user default value
                    String currentUID = mAuth.getCurrentUser().getUid();
                    storeDefaultUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
                    storeDefaultUserData.child("user_name").setValue(message_username);
                    storeDefaultUserData.child("user_image").setValue("default_profile_pic")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if( task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "User has been succesfully registerd!", Toast.LENGTH_SHORT).show();
                                        finish();
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Registration failed" + task.getException(), Toast.LENGTH_SHORT).show();
                }
                loadingBar.dismiss();
            }
        });
    }

}
