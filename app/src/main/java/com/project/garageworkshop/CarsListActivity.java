package com.project.garageworkshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CarsListActivity extends AppCompatActivity {
    private RecyclerView carsList;
    private RecyclerAdapter adapter;
    private Button addBtn, buttonNew, sensorsButton;
    private List<CarListItem> items;
    private DatabaseReference database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carslistlayout);
        carsList = (RecyclerView) findViewById(R.id.carslist);
        DividerItemDecoration decor = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.HORIZONTAL);
        carsList.addItemDecoration(decor);
        items = new ArrayList<>();

        String uid = auth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference("Cars").child(uid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String make = postSnapshot.child("make").getValue().toString();
                    String model = postSnapshot.child("model").getValue().toString();
                    String carNumb = postSnapshot.getKey();
                    String uri = postSnapshot.child("imageId").getValue().toString();
                    CarListItem car = new CarListItem(make, model, carNumb, uri);
                    items.add(car);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*items.add(new CarListItem("Audi", "A6", "AEU513", R.drawable.audi));
        items.add(new CarListItem("Subaru", "Forester", "JZU955", R.drawable.ic_car_image_placeholder));
        items.add(new CarListItem("Audi", "A6", "AEU513", R.drawable.audi));
        items.add(new CarListItem("Subaru", "Forester", "JZU955", R.drawable.ic_car_image_placeholder));
        items.add(new CarListItem("Audi", "A6", "AEU513", R.drawable.audi));
        items.add(new CarListItem("Subaru", "Forester", "JZU955", R.drawable.ic_car_image_placeholder));*/

        adapter = new RecyclerAdapter((ArrayList<CarListItem>) items);
        RecyclerView.LayoutManager man = new LinearLayoutManager(this);
        this.carsList.setLayoutManager(man);
        carsList.setAdapter(adapter);
        addBtn = (Button) findViewById(R.id.addcar);
        sensorsButton = (Button) findViewById(R.id.lab3Btn);
        buttonNew = (Button) findViewById(R.id.buttonNew);
        buttonNew.setOnClickListener(startLabActivity);
        addBtn.setOnClickListener(startAddActivity);
        sensorsButton.setOnClickListener(startSensorsActivity);


    }

    View.OnClickListener startLabActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), NewLabActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener startAddActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent =new Intent(getBaseContext(), AddCarActivity.class);
            intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) items);
            startActivity(intent);
        }
    };
    View.OnClickListener startSensorsActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), SensorsActivity.class);
            startActivity(intent);
        }
    };

}
