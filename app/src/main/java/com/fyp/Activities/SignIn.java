package com.fyp.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.fyp.R;
import com.fyp.Utils.Utilities;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import static com.fyp.Utils.Utilities.isEmailValid;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmail, mPass;
    private Button sign_in;
    private TextView forgot_pass, registration;
    private String email, password, forgetEmail;
    private FirebaseAuth auth;
    private boolean isExist = false, isForgetPass = false;
    private AlertDialog findMeDialog;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initialization();
    }

    //initialization
    public void initialization() {
        auth = FirebaseAuth.getInstance();
        mEmail = (EditText) findViewById(R.id.login_email);
        mPass = (EditText) findViewById(R.id.login_password);
        forgot_pass = (TextView) findViewById(R.id.forgot_pass);
        registration = (TextView) findViewById(R.id.switch_to_sign_up);
        sign_in = (Button) findViewById(R.id.sign_in);

        sign_in.setOnClickListener(this);
        forgot_pass.setOnClickListener(this);
        registration.setOnClickListener(SignIn.this);
    }

    //it will handle all clicks of Login Screen
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgot_pass:
                openForgetPasswordDialog();
                break;
            case R.id.switch_to_sign_up:
                Intent i = new Intent(SignIn.this, SingUp.class);
                startActivity(i);
                overridePendingTransition(R.anim.signin_incoming_screen_right_to_mean_position, R.anim.signin_current_screen_move_mean_to_left);
                finish();
                break;
            case R.id.sign_in:
                validation();
                break;
        }
    }

    //A function to perfom "forget password" feature
    public void openForgetPasswordDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_forget_password, null);
        dialogBuilder.setView(dialogView);
        findMeDialog = dialogBuilder.create();
        findMeDialog.show();
        LinearLayout reset_btn = (LinearLayout) dialogView.findViewById(R.id.ok);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when ok button will click, following actions will perform 1) is your email null? is your email valid? have you internet connection?
                EditText mForgetEmail = (EditText) dialogView.findViewById(R.id.forget_email);
                forgetEmail = mForgetEmail.getText().toString();
                if (forgetEmail != null || forgetEmail.equals("")) {
                    if (isEmailValid(forgetEmail)) {
                        if (Utilities.haveNetworkConnection(SignIn.this)) {
                            loading = ProgressDialog.show(SignIn.this, "Sending Email", "Please wait", false, false);
                            isForgetPass = true;
                            //this method "checkEmailList()" will call for two things 1) For signIn 2) For Forget-Pass to check user registrations
                            if (isForgetPass == true) {
                                final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                                mFirebaseAuth.sendPasswordResetEmail(forgetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SignIn.this, "check your email", Toast.LENGTH_LONG).show();
                                        loading.dismiss();
                                        findMeDialog.dismiss();
                                        isForgetPass = false;
                                    }
                                });
                            } else {
                                whenSignInSuccessfully();
                            }
                        } else {
                            findMeDialog.dismiss();
                            openDialog();
                        }
                    } else {
                        mForgetEmail.requestFocus();
                        Toast.makeText(SignIn.this, "invalid email", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mForgetEmail.requestFocus();
                    Toast.makeText(SignIn.this, "enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validation() {
        email = mEmail.getText().toString();
        password = mPass.getText().toString();

        if (Utilities.haveNetworkConnection(SignIn.this)) {
            if (isEmailValid(email)) {
                if (!(mPass.equals("") || mPass.equals(null))) {
                    //when fields will be validated
                    firebaseLogin();
                } else {
                    mPass.requestFocus();
                    Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
                }

            } else {
                mEmail.requestFocus();
                Toast.makeText(this, "invalid email", Toast.LENGTH_SHORT).show();
            }
        } else {
            openDialog();
        }
    }

    //firebase login request
    public void firebaseLogin() {
        loading = ProgressDialog.show(SignIn.this, "Signing in", "Please wait...", false, false);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //first it will check existance from firebase authentcated users list. In case of existance then further it will check from the list of users that are registered by admin
                        if (!task.isSuccessful()) {
                            loading.dismiss();
                            Toast.makeText(SignIn.this, "wrong email/password", Toast.LENGTH_SHORT).show();
                        } else {
                            whenSignInSuccessfully();
                        }
                    }
                });
    }

    public void whenSignInSuccessfully() {
        loading.dismiss();
        //we store values in local database using Shared Preferences
        SharedPreferences sPref = getSharedPreferences("Registration", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt("isRegister", 1);
        editor.commit();

        //we move from one screen to another using Intent Class
        Intent i = new Intent(SignIn.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.signin_incoming_screen_right_to_mean_position, R.anim.signin_current_screen_move_mean_to_left);
        finish();
        Toast.makeText(SignIn.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
    }

    public void openDialog() {
        //Dial log box will be appear when there will be no Internet connection
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_connection, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog findMeDialog = dialogBuilder.create();
        findMeDialog.show();
        LinearLayout reset_btn = (LinearLayout) dialogView.findViewById(R.id.ok);
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findMeDialog.dismiss();
            }
        });
    }
}
