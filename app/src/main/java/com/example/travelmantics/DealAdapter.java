package com.example.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {
    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;
    private ImageView imageDeal;
    private boolean isAdmin;

    public DealAdapter() {
        //FirebaseUtil.openFbReference("traveldeals");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        isAdmin = FirebaseUtil.isAdmin;
        deals = FirebaseUtil.mDeals;
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deal:", td.getTitle());
                td.setId(dataSnapshot.getKey());
                deals.add(td);
                notifyItemInserted(deals.size() -1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row, parent, false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal deal = deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPrice;
        private Button editButton;
        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageDeal = itemView.findViewById(R.id.imageDeal);
            editButton = itemView.findViewById(R.id.btn_edit);
            Log.d("Admin DealAdapter", String.valueOf(isAdmin));
            if(isAdmin){
                editButton.setVisibility(View.VISIBLE);
                editButton.setOnClickListener(this);
            }else{
                editButton.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(this);

        }

        public void bind(TravelDeal deal){
            tvTitle.setText(deal.getTitle());
            tvDescription.setText(deal.getDescription());
            //String price = "Ksh " + String.format("%,.2f", deal.getPrice() );
            String price = formatPrice(deal.getPrice()) ;
            tvPrice.setText(price);
            showImage(deal.getImageUrl());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("Clik: ", String.valueOf(position));
            TravelDeal selectedDeal = deals.get(position);
            if(v.getId() == editButton.getId()){
                Intent intent = new Intent(v.getContext(), DealActivity.class);
                intent.putExtra("Deal", selectedDeal);
                v.getContext().startActivity(intent);
            }else{
                Intent intent = new Intent(v.getContext(), DealDetails.class);
                intent.putExtra("Deal", selectedDeal);
                v.getContext().startActivity(intent);
            }
        }
        private void showImage(String url) {
            if (url != null && !url.isEmpty()) {
                Picasso.get()
                        .load(url)
                        .resize(200, 200)
                        .centerCrop()
                        .into(imageDeal);
            }
        }
    }
    public String formatPrice(String price){
        Locale locale = new Locale("en", "KE");
        Double newPrice = Double.parseDouble(price);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        //System.out.println("Amount is - " + finalAmount);
        return formatter.format(newPrice);
    }
}
