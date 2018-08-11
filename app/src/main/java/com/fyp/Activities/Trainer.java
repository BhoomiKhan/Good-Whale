package com.fyp.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.R;
import com.fyp.Utils.Utilities;

import java.util.Random;

import static com.fyp.Utils.Utilities.isEmailValid;

public class Trainer extends AppCompatActivity implements View.OnClickListener{

    private EditText mTrainerName;
    private Button addTask;
    private TextView mCourseID;
    private String trainerName;
    private int courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        mTrainerName=(EditText) findViewById(R.id.trainer_name);
        addTask=(Button) findViewById(R.id.add_task);
        mCourseID=(TextView) findViewById(R.id.course_id);
        addTask.setOnClickListener(Trainer.this);
        courseID = generatePin();
        mCourseID.setText("Course ID: "+Integer.toString(courseID));
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.add_task) {
            validation();
        }
    }

    public void validation() {
        trainerName=mTrainerName.getText().toString();
        if (Utilities.haveNetworkConnection(Trainer.this)) {
            if (!trainerName.equals("")) {
                SharedPreferences mSharedPreferences=getSharedPreferences("TaskManagementSys",MODE_PRIVATE);
                SharedPreferences.Editor mEditor=mSharedPreferences.edit();
                mEditor.putString("trainerName",trainerName);
                mEditor.putString("courseID",Integer.toString(courseID));
                mEditor.commit();
                startActivity(new Intent(Trainer.this, AddTask.class));
                finish();
            } else {
                mTrainerName.requestFocus();
                Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            openDialog();
        }
    }

        public static int generatePin() throws Exception {
            Random generator = new Random();
            generator.setSeed(System.currentTimeMillis());

            int num = generator.nextInt(99999) + 99999;
            if (num < 100000 || num > 999999) {
                num = generator.nextInt(99999) + 99999;
                if (num < 100000 || num > 999999) {
                    throw new Exception("Unable to generate PIN at this time..");
                }
            }
            return num;
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