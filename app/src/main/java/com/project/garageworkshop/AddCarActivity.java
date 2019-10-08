package com.project.garageworkshop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddCarActivity extends AppCompatActivity {
    Button addCarBtn, sortButton;
    private RecyclerView carsList;
    private RecyclerAdapter adapter;
    EditText make, model, carnumb;
    Uri imageUri;
    SearchView searchView;
    List<CarListItem> list  =new ArrayList<>();
    private static final int PICK_IMAGE = 100;
    ImageView carImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        list =  getIntent().getParcelableArrayListExtra("list");
        adapter = new RecyclerAdapter( (ArrayList<CarListItem>) list);
        carsList = (RecyclerView) findViewById(R.id.recyclerview);
        carsList.setHasFixedSize(true);
        RecyclerView.LayoutManager man = new LinearLayoutManager(this);
        this.carsList.setLayoutManager(man);
        addCarBtn = findViewById(R.id.addcar);
        sortButton = findViewById(R.id.sortBtn);
        make = findViewById(R.id.make);
        searchView = findViewById(R.id.searchView);
        model = findViewById(R.id.model);
        carnumb = findViewById(R.id.carNumb);
        carImage = findViewById(R.id.carImg);
        addCarBtn.setOnClickListener(addCar);
        sortButton.setOnClickListener(sortList);
        carImage.setOnClickListener(addNewImage);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


            carsList.setAdapter(adapter);


        adapter.setOnItemClickListener(new RecyclerAdapter.RecyclerClickListener() {
            @Override
            public void onOnItemClick(int position) {
                Intent intent = new Intent(getBaseContext(), CarElementActivity.class);
                CarListItem car = list.get(position);
                intent.putExtra("car", (Parcelable) car);
                startActivity(intent);
            }
        });
    }

    View.OnClickListener addCar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String makeString = make.getText().toString().trim();
            String modelString = model.getText().toString().trim();
            String numbString = carnumb.getText().toString().trim();
            CarListItem car = new CarListItem(makeString, modelString, numbString, R.drawable.ic_taxi );
            list.add(car);
            adapter.added();
            adapter.notifyDataSetChanged();

        }
    };
    View.OnClickListener sortList = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Collections.sort(list, new CarListItem.CustomComparator());
            adapter.notifyDataSetChanged();
        }
    };
    View.OnClickListener addNewImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            carImage.setImageURI(imageUri);
        }
    }

}
