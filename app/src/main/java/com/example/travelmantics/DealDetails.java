package com.example.travelmantics;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DealDetails extends AppCompatActivity {
    TextView txtTitle;
    TextView txtDescription;
    TextView txtPrice;
    ImageView imageView;
    private TravelDeal deal;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private boolean isAdministrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_details);

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        isAdministrator = FirebaseUtil.isAdmin;

        txtTitle = findViewById(R.id.txtVTitle);
        txtDescription = findViewById(R.id.txtVDescription);
        txtPrice = findViewById(R.id.txtVPrice);
        imageView = findViewById(R.id.image);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if(deal == null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        //TravelDeal deal1 = deal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        DealAdapter dealAdapter = new DealAdapter();
        String price = "Trip Price: " + dealAdapter.formatPrice(deal.getPrice());

        txtPrice.setText(price);
        showImage(deal.getImageUrl());
    }

    private void showImage(String url){
        if(url != null && !url.isEmpty()){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width * 2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }
    private void deleteDeal(){
        if(deal == null){
            Toast.makeText(this, "Please select deal to delete.", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(DealDetails.this);
        builder.setTitle(deal.getTitle());
        builder.setMessage("Are you sure you want to delete this deal?");
        builder.setIcon(R.drawable.ic_delete_24dp);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mDatabaseReference.child(deal.getId()).removeValue();
                if(deal.getImageName() != null && !deal.getImageName().isEmpty()){
                    StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
                    picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(DealDetails.this,"Deal and its image successfully deleted!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Delete image", e.getMessage());
                        }
                    });
                }
                dialog.dismiss();
                backToList();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Toast.makeText(DealDetails.this, "Delete has been cancelled", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deal_details_menu, menu);
        MenuItem bookMenu = menu.findItem(R.id.book_deal_menu);
        MenuItem editMenu = menu.findItem(R.id.edit_deal_menu);
        MenuItem deleteMenu = menu.findItem(R.id.delete_deal_menu);
        if(isAdministrator){
            //menu.clear(); //Remove entire menu
            editMenu.setVisible(true);
            deleteMenu.setVisible(true);
        }else{
            editMenu.setVisible(false);
            deleteMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_deal_menu:
                deleteDeal();
                return true;
            case R.id.edit_deal_menu:
                Intent intent = new Intent(this, DealActivity.class);
                intent.putExtra("Deal", deal);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }
}
