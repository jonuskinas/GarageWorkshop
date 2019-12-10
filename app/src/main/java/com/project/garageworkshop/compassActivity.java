package com.project.garageworkshop;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class compassActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;

    private ImageView compassImage;

    private float degreeStart =10f;

    TextView degree;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassImage = (ImageView) findViewById(R.id.compass_image);
        degree =(TextView) findViewById(R.id.degree);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(this, SensorsActivity.class);
        startActivity(setIntent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degreeFloat = Math.round(event.values[0]);
        if((event.values[0] <= 5 && event.values[0] >=0) || ((event.values[0] <= 360 && event.values[0] >=355)))
        {
            boolean ft = true;
            Intent intent = new Intent(this, SensorsActivity.class);
            intent.putExtra("photo", ft);
            this.startActivity(intent);
        }
        degree.setText("Heading: " + Float.toString(degreeFloat) + " degrees");

        RotateAnimation ra = new RotateAnimation(degreeStart, -degreeFloat, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        ra.setFillAfter(true);
        ra.setDuration(210);

        compassImage.startAnimation(ra);
        degreeStart = -degreeFloat;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
