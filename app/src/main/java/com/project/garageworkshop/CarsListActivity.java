package com.project.garageworkshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class CarsListActivity extends AppCompatActivity {
    private RecyclerView carsList;
    private RecyclerAdapter adapter;
    private Button addBtn;
    private List<CarListItem> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carslistlayout);
        carsList = (RecyclerView) findViewById(R.id.carslist);
        DividerItemDecoration decor = new DividerItemDecoration(getBaseContext(), DividerItemDecoration.HORIZONTAL);
        carsList.addItemDecoration(decor);
        items = new ArrayList<>();
        items.add(new CarListItem("Audi", "A6", "AEU513", R.drawable.audi));
        items.add(new CarListItem("Subaru", "Forester", "JZU955", R.drawable.ic_car_image_placeholder));
        items.add(new CarListItem("Audi", "A6", "AEU513", R.drawable.audi));
        items.add(new CarListItem("Subaru", "Forester", "JZU955", R.drawable.ic_car_image_placeholder));
        items.add(new CarListItem("Audi", "A6", "AEU513", R.drawable.audi));
        items.add(new CarListItem("Subaru", "Forester", "JZU955", R.drawable.ic_car_image_placeholder));
        adapter = new RecyclerAdapter((ArrayList<CarListItem>) items);
        RecyclerView.LayoutManager man = new LinearLayoutManager(this);
        this.carsList.setLayoutManager(man);
        carsList.setAdapter(adapter);
        addBtn = (Button) findViewById(R.id.addcar);
        addBtn.setOnClickListener(startAddActivity);


    }

    View.OnClickListener startAddActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent =new Intent(getBaseContext(), AddCarActivity.class);
            intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) items);
            startActivity(intent);
        }
    };

}
