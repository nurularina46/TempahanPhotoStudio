package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SubPackageAdapter extends RecyclerView.Adapter<SubPackageAdapter.ViewHolder> {
    private ArrayList<SubPackageModel> subPackageList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDetailsClick(SubPackageModel subPackage);
    }

    public SubPackageAdapter(ArrayList<SubPackageModel> subPackageList, OnItemClickListener listener) {
        this.subPackageList = subPackageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_sub_package, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubPackageModel subPackage = subPackageList.get(position);
        holder.tvSubPackageCode.setText("SUB-" + String.format("%03d", subPackage.getId()));
        holder.tvSubPackageName.setText(subPackage.getPackageClass());

        holder.btnDetails.setOnClickListener(v -> listener.onDetailsClick(subPackage));
    }

    @Override
    public int getItemCount() {
        return subPackageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubPackageCode, tvSubPackageName;
        Button btnDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubPackageCode = itemView.findViewById(R.id.tvSubPackageCode);
            tvSubPackageName = itemView.findViewById(R.id.tvSubPackageName);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}
