package com.fyp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fyp.Activities.Trainee;
import com.fyp.Activities.Trainer;
import com.fyp.R;

public class Custom extends Fragment implements View.OnClickListener{

    private View view;
    Button trainer, trainee;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.custom_task_manager, container, false);
        trainer=view.findViewById(R.id.trainer);
        trainee=view.findViewById(R.id.trainee);
        trainer.setOnClickListener(this);
        trainee.setOnClickListener(this);
        init();
        return view;
    }

    private void init() {

    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        if (v.getId()==R.id.trainer){
            intent=new Intent(getActivity(), Trainer.class);
        } else if (v.getId()==R.id.trainee){
            intent=new Intent(getActivity(), Trainee.class);
        }
        startActivity(intent);
    }
}