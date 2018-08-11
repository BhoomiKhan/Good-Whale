package com.fyp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fyp.Const.Constants;
import com.fyp.R;
import com.fyp.Utils.GsonCourseModel;
import com.google.gson.Gson;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Trainee extends AppCompatActivity implements View.OnClickListener {

    Button active;
    EditText courseID;
    String courseId;
    ProgressDialog loading;
    private List<GsonCourseModel> listOfPlaces;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee);
        init();
    }

    private void init() {
        active = (Button) findViewById(R.id.active);
        courseID = (EditText) findViewById(R.id.course_id);
        active.setOnClickListener(this);
        courseID.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.active) {
            if (!courseID.getText().equals("")) {
                courseId = courseID.getText().toString();
                netWorkRequest();
            } else {
                Toast.makeText(this, "Enter Course Id", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void netWorkRequest() {
        loading = ProgressDialog.show(Trainee.this, "Loading Course", "Please wait..", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.loadCourse,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        if (!response.equals("NoRecord")) {
                            Intent mIntent = new Intent(Trainee.this, LoadCourse.class);
                            mIntent.putExtra("course_response", response);
                            SharedPreferences mSharedPreferences=getSharedPreferences("TaskManagementSys",MODE_PRIVATE);
                            SharedPreferences.Editor mEditor=mSharedPreferences.edit();
                            mEditor.putString("serverresponse",response);
                            mEditor.commit();
                            startActivity(mIntent);
                            finish();
                            Log.d("responsefromserver", response);
                        } else {
                            Toast.makeText(Trainee.this, "No Record found", Toast.LENGTH_SHORT).show();
                        }
                        // setAdapter();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("courseID", courseId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}