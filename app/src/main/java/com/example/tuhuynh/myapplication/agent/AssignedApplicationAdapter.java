package com.example.tuhuynh.myapplication.agent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.util.CustomUtil;

import java.util.List;

public class AssignedApplicationAdapter extends ArrayAdapter<ApplicationInfo> {

    private List<ApplicationInfo> applications;

    AssignedApplicationAdapter(Context context, int textViewResourceId, List<ApplicationInfo> objects) {
        super(context, textViewResourceId, objects);
        this.applications = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View rowView = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (rowView == null) {
            rowView = inflater.inflate(R.layout.assigned_application_adapter, null);
        }

        // Initial elements
        TextView tvCustomerName = rowView.findViewById(R.id.tv_name);
        TextView tvAmount = rowView.findViewById(R.id.tv_amount);
        TextView tvMonth = rowView.findViewById(R.id.tv_month);
        TextView tvDate = rowView.findViewById(R.id.tv_date);

        // Get value
        ApplicationInfo application = applications.get(position);
        String strAmount = CustomUtil.convertLongToFormattedString(application.getAmount());
        String strMonth = Integer.toString(application.getMonth());
        String strDate = CustomUtil.convertDateToString(application.getDate(), "dd-MM-yyyy");

        // Set value for text view
        if (CustomUtil.hasCharacter(application.getCustomer().getSurname())) {
            String fullName = application.getCustomer().getName() + application.getCustomer().getSurname();
            tvCustomerName.setText(fullName);
        } else {
            tvCustomerName.setText(application.getCustomer().getName());
        }
        tvAmount.setText(strAmount);
        tvMonth.setText(strMonth);
        tvDate.setText(strDate);

        return rowView;

    }


}
