package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {
    private ArrayList<BookingModel> bookingList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onBookingClick(BookingModel booking);
    }

    public BookingHistoryAdapter(ArrayList<BookingModel> bookingList, OnItemClickListener listener) {
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingModel booking = bookingList.get(position);
        
        // Set booking ID
        holder.tvBookingId.setText("BK-" + String.format("%03d", booking.getId()));
        
        // Set status with color
        holder.tvBookingStatus.setText(booking.getStatus());
        setStatusColor(holder.tvBookingStatus, booking.getStatus());
        
        // Set package name (you might need to get this from package table)
        holder.tvPackageName.setText("Package #" + booking.getPackageId());
        
        // Set event date
        holder.tvEventDate.setText("Event Date: " + booking.getEventDate());
        
        // Set sub package
        holder.tvSubPackage.setText("Sub Package #" + booking.getSubPackageId());
        
        // Set total amount
        holder.tvTotalAmount.setText("RM " + String.format("%.2f", booking.getTotalAmount()));
        
        // Set payment status
        holder.tvPaymentStatus.setText("Payment: " + booking.getPaymentStatus());
        setPaymentStatusColor(holder.tvPaymentStatus, booking.getPaymentStatus());
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookingClick(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    private void setStatusColor(TextView textView, String status) {
        switch (status.toLowerCase()) {
            case "confirmed":
                textView.setTextColor(textView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "pending":
                textView.setTextColor(textView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "cancelled":
                textView.setTextColor(textView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                break;
            default:
                textView.setTextColor(textView.getContext().getResources().getColor(android.R.color.darker_gray));
                break;
        }
    }

    private void setPaymentStatusColor(TextView textView, String paymentStatus) {
        switch (paymentStatus.toLowerCase()) {
            case "paid":
                textView.setTextColor(textView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "pending":
                textView.setTextColor(textView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "refunded":
                textView.setTextColor(textView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
                break;
            default:
                textView.setTextColor(textView.getContext().getResources().getColor(android.R.color.darker_gray));
                break;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingId, tvBookingStatus, tvPackageName, tvEventDate, 
                 tvSubPackage, tvTotalAmount, tvPaymentStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvSubPackage = itemView.findViewById(R.id.tvSubPackage);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
        }
    }
}
