package com.project.garageworkshop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class NewLabActivity extends AppCompatActivity implements RequestOperator.RequestOperatorListener {

    Button sendRequestButton;
    private List<Repairs> repairsList;
    private IndicatingView indicator;
    private RotateAnimation rotate;
    private ViewCount countIndicator;
    private RecyclerView repairsView;
    private RecyclerAdapterRepairs adapterRepairs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lab);

        repairsView = findViewById(R.id.repairsView);
        repairsList =new ArrayList<>();


        sendRequestButton = (Button) findViewById(R.id.send_request);
        sendRequestButton.setOnClickListener(requestButtonClicked);
        indicator =(IndicatingView) findViewById(R.id.generated_graphic);
        countIndicator = (ViewCount) findViewById(R.id.generated_count);
        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(Animation.INFINITE);
        indicator.setAnimation(rotate);

    }

    View.OnClickListener requestButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            indicator.setAnimation(rotate);
            setIndicatorStatus(IndicatingView.INPROGRESS);
            sendRequest();
        }
    };

    private void sendRequest() {
        RequestOperator ro = new RequestOperator();
        ro.setListener(this);
        ro.start();
    }

    public void updatePublication() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (repairsList !=null) {
                    //Toast.makeText(NewLabActivity.this, Integer.toString(repairsList.size()), Toast.LENGTH_SHORT ).show();
                    //repairsList = requestList;
                    adapterRepairs = new RecyclerAdapterRepairs((ArrayList<Repairs>) repairsList);
                    RecyclerView.LayoutManager man = new LinearLayoutManager(getBaseContext());
                    repairsView.setLayoutManager(man);
                    repairsView.setAdapter(adapterRepairs);
                    countIndicator.setCount(repairsList.size());
                    countIndicator.invalidate();
                }
                else {
                    //requestList.clear();
                }
            }
        });
    }

    @Override
    public void success(List<Repairs> publication) {
        this.repairsList = publication;
        updatePublication();
        indicator.clearAnimation();
        setIndicatorStatus(IndicatingView.SUCCESS);
    }

    @Override
    public void failed(int responseCode) {
        this.repairsList = null;
        updatePublication();
        indicator.clearAnimation();
        setIndicatorStatus(IndicatingView.FAILED);
    }

    public void setIndicatorStatus(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                indicator.setState(status);
                indicator.invalidate();

            }
        });
    }



}
