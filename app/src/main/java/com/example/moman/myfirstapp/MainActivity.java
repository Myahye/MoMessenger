package com.example.moman.myfirstapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private FirebaseAuth mAuth;
    private EditText user_password, user_email;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this, LogInActivity.class));
        }

        //password
        user_password = (EditText) findViewById(R.id.user_password);
        //email
        user_email = (EditText) findViewById(R.id.user_username);
    }

    /** Called when user clicks on send */
    public void log_in(View view){
        //responding to button

        String message_useremail = user_email.getText().toString();
        String message_password = user_password.getText().toString();
        //if Email Is Empty
        if(message_useremail.isEmpty()){
            user_email.setError("An email is required!");
            user_email.requestFocus();
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
        loadingBar.setTitle("Logging In..");
        loadingBar.show();
        //dealing with a valid email/password combination
        Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(message_useremail, message_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "LogIn failed" + task.getException(), Toast.LENGTH_SHORT).show();
                }
                loadingBar.dismiss();
            }
        });

    }

    public void sign_up(View view){
        //responding to button
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
