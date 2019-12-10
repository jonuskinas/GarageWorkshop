package com.project.garageworkshop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class AddCarActivity extends AppCompatActivity {
    Button addCarBtn, sortButton;
    private RecyclerView carsList;
    private DatabaseReference database;
    private RecyclerAdapter adapter;
     String userId;
    EditText make, model, carnumb;
    Uri imageUri;
    private StorageReference carImageStorage;
    private static final int GALLERY_PICK = 1;

    SearchView searchView;
    List<CarListItem> list  =new ArrayList<>();
    private static final int PICK_IMAGE = 100;
    ImageView carImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        list =  getIntent().getParcelableArrayListExtra("list");
        adapter = new RecyclerAdapter( (ArrayList<CarListItem>) list);
        carImageStorage =  FirebaseStorage.getInstance().getReference();
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

            @Override
            public void onLongItemClick(int position) {
                adapter.removeElement(position);
            }
        });


    }

    View.OnClickListener addCar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String makeString = make.getText().toString().trim();
            final String modelString = model.getText().toString().trim();
            final String numbString = carnumb.getText().toString().trim();
            if (imageUri != null) {
                final File thumb_filePath = new File(imageUri.getPath());
                try {
                    Bitmap thumb_bitmap = new Compressor(getBaseContext()).setMaxWidth(200).setMaxHeight(200).setQuality(60).compressToBitmap(thumb_filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] tumb_byte = baos.toByteArray();
                    final StorageReference filePath = carImageStorage.child("Car_Image").child(numbString + ".jpg");
                    UploadTask uploadTask = filePath.putBytes(tumb_byte);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                  final String download_uriThumb = uri.toString();
                                  database = FirebaseDatabase.getInstance().getReference("Cars").child(userId).child(numbString);
                                  CarListItem carListItem = new CarListItem(makeString, modelString, numbString, download_uriThumb);
                                  Map post = new HashMap<>();
                                  post.put("make", makeString);
                                  post.put("model", modelString);
                                  post.put("imageId", download_uriThumb);
                                  database.setValue(post);
                                  list.add(carListItem);
                                  adapter.notifyDataSetChanged();

                               }
                           });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /*CarListItem car = new CarListItem(makeString, modelString, numbString, R.drawable.ic_taxi );
            list.add(car);
            adapter.added();
            adapter.notifyDataSetChanged();*/

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
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            if (imageUri != null) {
                carImage.setImageURI(imageUri);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            //Uri imageUri = data.getData();

            CropImage.activity()
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }

    }

}
