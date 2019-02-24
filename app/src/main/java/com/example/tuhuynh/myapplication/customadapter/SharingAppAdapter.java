package com.example.tuhuynh.myapplication.customadapter;

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


public class SharingAppAdapter extends ArrayAdapter<ApplicationInfo> {

    private List<ApplicationInfo> applications;

    public SharingAppAdapter(Context context, int textViewResourceId, List<ApplicationInfo> objects) {
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
            rowView = inflater.inflate(R.layout.sharing_app_adapter, null);
        }

        // Initial elements
        TextView tvAmount = rowView.findViewById(R.id.tv_amount);
        TextView tvMonth = rowView.findViewById(R.id.tv_month);
        TextView tvInterest = rowView.findViewById(R.id.tv_interest);
        TextView tvPhone = rowView.findViewById(R.id.tv_phone);
        TextView tvSalary = rowView.findViewById(R.id.tv_salary);

        // Get value
        ApplicationInfo application = applications.get(position);
        String strAmount = CustomUtil.convertLongToFormattedString(application.getAmount());
        String strMonth = Integer.toString(application.getMonth());
        String strInterest = Double.toString(application.getInterest());
        String strPhone = application.getCustomer().getPhone();
        String strSalary = CustomUtil.convertLongToFormattedString(application.getCustomer().getSalary());

        // Set value for TextView
        tvAmount.setText(strAmount);
        tvMonth.setText(strMonth);
        tvInterest.setText(strInterest);
        tvPhone.setText(strPhone);
        tvSalary.setText(strSalary);

        return rowView;
    }


}
