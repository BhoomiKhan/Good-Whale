package com.fyp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fyp.R;
import com.fyp.Utils.GsonCourseModel;
import com.fyp.Utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

public class LoadCourse extends AppCompatActivity implements View.OnClickListener {

    private Button btnactive, btnsumbit, logOut;
    private ViewGroup activelayout, unactivelayout;
    private ImageView mImageView;
    private TextView mTextView;
    private String descriptions[];
    private Gson gson;
    private List<GsonCourseModel> listOfCourseImageAndDesc;
    private String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_course);
        initilizer();
    }

    private void initilizer() {
        if (getIntent().hasExtra("course_response")) {
            response = getIntent().getExtras().getString("course_response");
        }
        activelayout = (ViewGroup) findViewById(R.id.fitness_activelayout);
        unactivelayout = (ViewGroup) findViewById(R.id.fitness_unactivelayout);
        btnactive = (Button) findViewById(R.id.fitness_btnactive);
        btnactive.setOnClickListener(this);
        btnsumbit = (Button) findViewById(R.id.fitness_btnsubmit);
        btnsumbit.setOnClickListener(this);
        // txttimer = (TextView) view.findViewById(R.id.fitness_txttimer);
        mImageView = (ImageView) findViewById(R.id.fitness_image);
        mTextView = (TextView) findViewById(R.id.fitness_description);
        logOut = (Button) findViewById(R.id.logout);
        logOut.setOnClickListener(this);
        //it will get array of descriptions
        descriptions = getResources().getStringArray(R.array.descriptions);
        //check point to check that either tasks has been started or not
        if (Utilities.checkStatus(LoadCourse.this, "custom_active_button") == true) {
            activelayout.setVisibility(View.VISIBLE);
            unactivelayout.setVisibility(View.GONE);
            loadImageAndDescription();
        } else {
            unactivelayout.setVisibility(View.VISIBLE);
            activelayout.setVisibility(View.GONE);
        }

        SharedPreferences mSharedPreferences=getSharedPreferences("TaskManagementSys",MODE_PRIVATE);
        response=mSharedPreferences.getString("serverresponse", "nofound");
        Log.d("responseinloadscreen",response);

        if (!response.equals("nofound")) {
            //initialization for GSON library
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
            listOfCourseImageAndDesc = Arrays.asList(gson.fromJson(response, GsonCourseModel[].class));
        } else {
            Toast.makeText(this, "no record found", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        if (view.getId() == btnsumbit.getId()) {
            String btntext = btnsumbit.getText().toString();
            if (btntext.equalsIgnoreCase("Submit")) {
                startActivity(new Intent(LoadCourse.this, TaskSubmitted.class));
            }
        } else if (view.getId() == btnactive.getId()) {
            //when active button will call then it will update the status in sharedpreference and set visibility GONE to active button and show other layout and load image and description
            loadImageAndDescription();
            Utilities.saveStatus(LoadCourse.this, "custom_active_button");
            Utilities.setAlarm(LoadCourse.this, "custom", "custom_task_counter");
            activelayout.setVisibility(View.VISIBLE);
            unactivelayout.setVisibility(View.GONE);
        } else if (view.getId() == R.id.logout) {
            SharedPreferences sPref = LoadCourse.this.getSharedPreferences("Registration", MODE_PRIVATE);
            SharedPreferences.Editor mEditor = sPref.edit();
            mEditor.putInt("isRegister", 1);
            mEditor.commit();
            startActivity(new Intent(LoadCourse.this, SignIn.class));
            LoadCourse.this.finish();
        }
    }

    private void loadImageAndDescription() {
        //In server images are stored in naming convension as follows, images1.jpg, image2.jpg, image3.jpg so that's why i am using below logic to generate dynamic link
        int taskNumber = Utilities.getLastTaskNumber(LoadCourse.this, "custom_task_counter");
        //when task number will exceed the limit then last image and description will be used

        if (taskNumber < listOfCourseImageAndDesc.size()) {
            mTextView.setText(listOfCourseImageAndDesc.get(taskNumber).getDescription());
            String url = listOfCourseImageAndDesc.get(taskNumber).getFile_path();
            Glide.with(LoadCourse.this)
                    .load(url)
                    .into(mImageView);
        }
    }
}