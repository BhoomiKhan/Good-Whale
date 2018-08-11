package com.fyp.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.fyp.Activities.SignIn;
import com.fyp.Activities.TaskSubmitted;
import com.fyp.Const.Constants;
import com.fyp.R;
import com.fyp.Utils.Utilities;

import static android.content.Context.MODE_PRIVATE;

public class Social extends Fragment implements View.OnClickListener {

    private View view;
    private Button btnactive, btnsumbit, logOut;
    private ViewGroup activelayout, unactivelayout;
    private ImageView mImageView;
    private TextView mTextView;
    private String descriptions[];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.universal_tab_fragment, container, false);
        activelayout = (ViewGroup) view.findViewById(R.id.fitness_activelayout);
        unactivelayout = (ViewGroup) view.findViewById(R.id.fitness_unactivelayout);
        btnactive = (Button) view.findViewById(R.id.fitness_btnactive);
        btnactive.setOnClickListener(this);
        btnsumbit = (Button) view.findViewById(R.id.fitness_btnsubmit);
        btnsumbit.setOnClickListener(this);
        // txttimer = (TextView) view.findViewById(R.id.fitness_txttimer);
        mImageView = (ImageView) view.findViewById(R.id.fitness_image);
        mTextView = (TextView) view.findViewById(R.id.fitness_description);
        logOut = (Button) view.findViewById(R.id.logout);
        logOut.setOnClickListener(this);
        initilizer();
        return view;
    }

    private void initilizer() {
        //it will get array of descriptions
        descriptions = getResources().getStringArray(R.array.descriptions);
        //check point to check that either tasks has been started or not
        if (Utilities.checkStatus(getActivity(), "social_active_button") == true) {
            activelayout.setVisibility(View.VISIBLE);
            unactivelayout.setVisibility(View.GONE);
            loadImageAndDescription();
        } else {
            unactivelayout.setVisibility(View.VISIBLE);
            activelayout.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        if (view.getId() == btnsumbit.getId()) {
            String btntext = btnsumbit.getText().toString();
            if (btntext.equalsIgnoreCase("Submit")) {
                startActivity(new Intent(getActivity(), TaskSubmitted.class));
            }
        } else if (view.getId() == btnactive.getId()) {
            //when active button will call then it will update the status in sharedpreference and set visibility GONE to active button and show other layout and load image and description
            loadImageAndDescription();
            Utilities.saveStatus(getActivity(), "social_active_button");
            Utilities.setAlarm(getActivity(), "Social", "social_task_counter");
            activelayout.setVisibility(View.VISIBLE);
            unactivelayout.setVisibility(View.GONE);
        } else if (view.getId()==R.id.logout){
            SharedPreferences sPref=getActivity().getSharedPreferences("Registration",MODE_PRIVATE);
            SharedPreferences.Editor mEditor=sPref.edit();
            mEditor.putInt("isRegister",1);
            mEditor.commit();
            startActivity(new Intent(getActivity(), SignIn.class));
            getActivity().finish();
        }
    }

    private void loadImageAndDescription() {
        //In server images are stored in naming convension as follows, images1.jpg, image2.jpg, image3.jpg so that's why i am using below logic to generate dynamic link
        int taskNumber = Utilities.getLastTaskNumber(getActivity(), "social_task_counter");
        //when task number will exceed the limit then last image and description will be used
        if (taskNumber == 16) {
            taskNumber--;
        }

        mTextView.setText(descriptions[taskNumber]);
       String url = Constants.socialImageUrl + Integer.toString(taskNumber) + ".jpg";
        Log.d("urls",url);
        Glide.with(getActivity())
                .load(url)
                .into(mImageView);
    }
}