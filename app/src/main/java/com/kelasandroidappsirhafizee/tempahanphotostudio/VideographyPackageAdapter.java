package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class VideographyPackageAdapter extends BaseAdapter {
    private Context context;
    private List<PackageModel> packageList;
    private LayoutInflater inflater;

    public VideographyPackageAdapter(Context context, List<PackageModel> packageList) {
        this.context = context;
        this.packageList = packageList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return packageList.size();
    }

    @Override
    public Object getItem(int position) {
        return packageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return packageList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_videography_package, parent, false);
            holder = new ViewHolder();
            holder.tvPackageCode = convertView.findViewById(R.id.tvPackageCode);
            holder.tvPackageName = convertView.findViewById(R.id.tvPackageName);
            holder.tvEventDuration = convertView.findViewById(R.id.tvEventDuration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PackageModel packageModel = packageList.get(position);
        
        // Set package code (using ID)
        holder.tvPackageCode.setText("VID-" + String.format("%03d", packageModel.getId()));
        
        // Set package name
        holder.tvPackageName.setText(packageModel.getPackageName());
        
        // Set event and duration
        holder.tvEventDuration.setText(packageModel.getEvent() + " | " + packageModel.getDuration());

        return convertView;
    }

    static class ViewHolder {
        TextView tvPackageCode;
        TextView tvPackageName;
        TextView tvEventDuration;
    }
}
