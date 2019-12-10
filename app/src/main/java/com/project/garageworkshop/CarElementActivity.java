package com.project.garageworkshop;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.robertsimoes.shareable.Shareable;
import com.squareup.picasso.Picasso;

public class CarElementActivity extends AppCompatActivity {
    TextView make, model, carnumb;
    ImageView carImage;
    Uri imageUri;
    String carUrl;
    Shareable imageShare;
    String shareMake, shareModel;
    Button remove, shareButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_element);
        final CarListItem car = getIntent().getParcelableExtra("car");
        make = findViewById(R.id.make);
        model = findViewById(R.id.model);
        carnumb = findViewById(R.id.carNumb);
        shareButton = findViewById(R.id.shareBtn);
        carImage = findViewById(R.id.carImg);
        remove = findViewById(R.id.remove);
        make.setText(car.getMake());
        model.setText(car.getModel());
        imageUri = Uri.parse(car.getImageId());
        carnumb.setText(car.getCarNumb());
        shareMake = car.getMake();
        shareModel = car.getModel();
        carUrl = car.getImageId();
        Picasso.get().load(car.getImageId()).into(carImage);
        remove.setOnClickListener(removeImage);
        shareButton.setOnClickListener(shareOnFacebook);
        /*shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shareable imageShare = new Shareable.Builder(v.getContext())
                        .image(imageUri)
                        .build();
                imageShare.share();
               /* Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.putExtra(Intent.EXTRA_STREAM, car.getImageId());
                myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                myIntent.putExtra(Intent.EXTRA_TEXT, car.getMake() + " " + car.getModel());
                myIntent.setType("");
                startActivity(Intent.createChooser(myIntent,"Share using"));
            }
        });*/
    }
    void facebook() {
        imageShare = new Shareable.Builder(this)
                .message("Hey! Check out my stunning " + shareMake + " " + shareModel)
                .url(carUrl)
                .build();
        imageShare.share();
    }
    View.OnClickListener removeImage = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            carImage.setVisibility(View.GONE);

        }
    };

    View.OnClickListener shareOnFacebook = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            facebook();
        }
    };
}
