package com.example.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class DealDetails extends AppCompatActivity {
    TextView txtTitle;
    TextView txtDescription;
    TextView txtPrice;
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_details);

        txtTitle = findViewById(R.id.txtVTitle);
        txtDescription = findViewById(R.id.txtVDescription);
        txtPrice = findViewById(R.id.txtVPrice);
        imageView = findViewById(R.id.image);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if(deal == null){
            deal = new TravelDeal();
        }
        TravelDeal deal1 = deal;
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
}
