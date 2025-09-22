package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {

    private ArrayList<PackageModel> packageList;
    private OnItemClickListener listener;
    private String role;

    // Interface untuk callback
    public interface OnItemClickListener {
        void onItemClick(PackageModel selectedPackage);
    }

    // Constructor
    public PackageAdapter(ArrayList<PackageModel> packageList, String role, OnItemClickListener listener) {
        this.packageList = packageList;
        this.listener = listener;
        this.role = role;

    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_package, parent, false);
        return new PackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        PackageModel pkg = packageList.get(position);

        holder.txtCode.setText("PKG-" + pkg.getId());
        holder.txtTitle.setText(pkg.getPackageName());
        holder.txtEvent.setText(pkg.getEvent());
        holder.txtDuration.setText(pkg.getDuration());

        // btnAddSubPackage hanya Admin boleh nampak
        if ("Admin".equals(role)) {
            holder.btnAddSubPackage.setVisibility(View.VISIBLE);
            holder.btnAddSubPackage.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), ListSubPackageActivity.class);
                intent.putExtra("PACKAGE_ID", pkg.getId());
                intent.putExtra("PACKAGE_NAME", pkg.getPackageName());
                intent.putExtra("CATEGORY", pkg.getCategory());
                intent.putExtra("ROLE", role);
                holder.itemView.getContext().startActivity(intent);
            });
        } else {
            holder.btnAddSubPackage.setVisibility(View.GONE);
        }

        // Klik pada card → ikut listener asal
        holder.itemView.setOnClickListener(v -> listener.onItemClick(pkg));
    }


    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public static class PackageViewHolder extends RecyclerView.ViewHolder {
        TextView txtCode, txtTitle, txtEvent, txtDuration;
        ImageButton btnAddSubPackage;

        public PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCode = itemView.findViewById(R.id.txtCode);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtEvent = itemView.findViewById(R.id.txtEvent);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            btnAddSubPackage = itemView.findViewById(R.id.btnAddSubPackage);
        }
    }

}
