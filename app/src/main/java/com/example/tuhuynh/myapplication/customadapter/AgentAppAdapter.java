package com.example.tuhuynh.myapplication.customadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.appication.ApplicationInfo;
import com.example.tuhuynh.myapplication.appication.ApplicationStatus;
import com.example.tuhuynh.myapplication.util.CustomUtil;

import java.util.List;

public class AgentAppAdapter extends ArrayAdapter<ApplicationInfo> {

    private List<ApplicationInfo> applications;
    private String caller;

    public AgentAppAdapter(Context context, int textViewResourceId, List<ApplicationInfo> objects, String caller) {
        super(context, textViewResourceId, objects);
        this.applications = objects;
        this.caller = caller;
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
            rowView = inflater.inflate(R.layout.agent_app_adapter, null);
        }

        // Initial elements
        TextView tvCustomerName = rowView.findViewById(R.id.tv_name);
        TextView tvAmount = rowView.findViewById(R.id.tv_amount);
        TextView tvMonth = rowView.findViewById(R.id.tv_month);
        TextView tvDate = rowView.findViewById(R.id.tv_date);
        TextView tvStatus = rowView.findViewById(R.id.tv_status);
        ImageView imgAvatar = rowView.findViewById(R.id.img_avatar);

        // Get value
        ApplicationInfo application = applications.get(position);
        String strAmount = CustomUtil.convertLongToFormattedString(application.getAmount());
        String strMonth = Integer.toString(application.getMonth());
        String strDate = CustomUtil.convertDateToString(application.getDate(), "dd-MM-yyyy");

        // Set value for text view
        if (CustomUtil.hasMeaning(application.getCustomer().getSurname())) {
            String fullName = application.getCustomer().getName() + application.getCustomer().getSurname();
            tvCustomerName.setText(fullName);
        } else {
            tvCustomerName.setText(application.getCustomer().getName());
        }
        tvAmount.setText(strAmount);
        tvMonth.setText(strMonth);
        tvDate.setText(strDate);
        imgAvatar.setImageResource(R.drawable.ic_user_avatar);

        // If caller is AgentAppHistory
        if (caller.equalsIgnoreCase("AgentAppHistory")) {
            String status = application.getStatus();
            tvStatus.setText(status);
            // Set color for status
            switch (status) {
                case ApplicationStatus.VALIDATING:
                    tvStatus.setTextColor(Color.parseColor("#FFCC00"));
                    break;
                case ApplicationStatus.APPROVED:
                    tvStatus.setTextColor(Color.parseColor("#009900"));
                    break;
                case ApplicationStatus.REJECTED:
                    tvStatus.setTextColor(Color.parseColor("#CC0000"));
                    break;
                case ApplicationStatus.DEBIT:
                    tvStatus.setTextColor(Color.parseColor("#0033FF"));
                    break;
                default:
                    break;
            }
        } else {
            tvStatus.setVisibility(View.GONE);
        }

        return rowView;
    }


}
