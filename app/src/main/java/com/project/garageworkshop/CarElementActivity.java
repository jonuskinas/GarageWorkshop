package com.project.garageworkshop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CarElementActivity extends AppCompatActivity {
    TextView make, model, carnumb;
    ImageView carImage;
    Button remove;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_element);
        CarListItem car = getIntent().getParcelableExtra("car");
        make = findViewById(R.id.make);
        model = findViewById(R.id.model);
        carnumb = findViewById(R.id.carNumb);

        carImage = findViewById(R.id.carImg);
        remove = findViewById(R.id.remove);
        make.setText(car.getMake());
        model.setText(car.getModel());
        carnumb.setText(car.getCarNumb());
        carImage.setImageResource(car.getImageId());
        remove.setOnClickListener(removeImage);
    }
    View.OnClickListener removeImage = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            carImage.setVisibility(View.GONE);

        }
    };
}
