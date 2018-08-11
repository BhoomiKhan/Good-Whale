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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import static com.fyp.Utils.Utilities.isEmailValid;

public class SingUp extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmail, mPass, mConfirm_pass;
    private Button sign_up;
    private TextView swith_to_login;
    private FirebaseAuth auth;
    private ProgressDialog loading;
    private String email, password, confirm_password;
    private boolean isExist=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
        initialization();
    }

    //firstly objects will initialize
    public void initialization() {
        auth = FirebaseAuth.getInstance();
        mEmail = (EditText) findViewById(R.id.signup_email);
        mPass = (EditText) findViewById(R.id.signup_password);
        mConfirm_pass = (EditText) findViewById(R.id.confirm_password);
        swith_to_login = (TextView) findViewById(R.id.switch_to_login);
        sign_up = (Button) findViewById(R.id.sign_up);
        sign_up.setOnClickListener(this);
        swith_to_login.setOnClickListener(SingUp.this);
        swith_to_login.setOnClickListener(this);
    }

    //it will handle all clicks on sign up screen
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_to_login:
                openActivity();
                break;
            case R.id.sign_up:
                validation();
                break;
        }
    }

    //first case & second case
    public void openActivity() {
        Intent i = new Intent(SingUp.this, SignIn.class);
        startActivity(i);
        overridePendingTransition(R.anim.signup_icoming_screen_left_to_mean, R.anim.signup_current_screen_mean_to_right);
        finish();
    }

    public void validation() {
        email = mEmail.getText().toString();
        password = mPass.getText().toString();
        confirm_password = mConfirm_pass.getText().toString();

        if (Utilities.haveNetworkConnection(SingUp.this)) {
            if (email != "") {
                if (isEmailValid(email)) {
                    if (!(password.equals("") || password.equals(null))) {
                        if (!(confirm_password.equals("")||confirm_password.equals(null))) {
                            if (password.equals(confirm_password)) {
                                //have internet connection, email is valid and passwords are matched then call firebase APIs
                                loading = ProgressDialog.show(SingUp.this, "Authentication", "Please Wait...", false, false);
                                firebaseSignUp();
                            } else {
                                mPass.requestFocus();
                                Toast.makeText(SingUp.this, "password mismatch", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mConfirm_pass.requestFocus();
                            Toast.makeText(this, "Enter confirm password", Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        mPass.requestFocus();
                        Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mEmail.requestFocus();
                    Toast.makeText(this, "invalid email", Toast.LENGTH_SHORT).show();
                }
            } else {
                mEmail.requestFocus();
                Toast.makeText(this, "invalid email", Toast.LENGTH_SHORT).show();
            }
        } else {
            openDialog();
        }
    }

    //firebase signUp request
    public void firebaseSignUp() {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SingUp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    loading.dismiss();
                    isExist = false;
                    if (task.getException().toString().contains("WEAK_PASSWORD")) {
                        Toast.makeText(SingUp.this, "weak password", Toast.LENGTH_SHORT).show();
                    }

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(SingUp.this, "invalid email", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(SingUp.this, "already exists", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    whenSignUpSuccessfully();
                }
            }
        });
    }

    public void whenSignUpSuccessfully() {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

        SharedPreferences sPref = getSharedPreferences("Registration", MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt("isRegister", 1);
        editor.commit();

        Intent i = new Intent(SingUp.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.signin_incoming_screen_right_to_mean_position, R.anim.signin_current_screen_move_mean_to_left);
        finish();
    }

    public void openDialog() {
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
