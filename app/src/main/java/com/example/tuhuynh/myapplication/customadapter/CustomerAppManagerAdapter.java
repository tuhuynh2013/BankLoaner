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

import java.util.Date;
import java.util.List;

public class CustomerAppManagerAdapter extends ArrayAdapter<ApplicationInfo> {

    private List<ApplicationInfo> applicationInfos;

    public CustomerAppManagerAdapter(Context context, int textViewResourceId, List<ApplicationInfo> objects) {
        super(context, textViewResourceId, objects);
        this.applicationInfos = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @SuppressLint("InflateParams")
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View rowView = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (rowView == null) {
            rowView = inflater.inflate(R.layout.customer_app_adapter, null);
        }

        // Initial element of view
        ImageView imgBankIcon = rowView.findViewById(R.id.img_bank_icon);
        TextView tvBankName = rowView.findViewById(R.id.tv_bank_name);
        TextView tvAmount = rowView.findViewById(R.id.tv_amount);
        TextView tvDate = rowView.findViewById(R.id.tv_date);
        TextView tvStatus = rowView.findViewById(R.id.tv_status);

        // Get value for adapter
        String shortName = applicationInfos.get(position).getBankInfo().getShortName();
        Long amount = applicationInfos.get(position).getAmount();
        Date date = applicationInfos.get(position).getDate();
        // Convert Date to String
        String strDate = CustomUtil.convertDateToString(date, "dd-MM-yyyy");
        String status = applicationInfos.get(position).getStatus();

        tvBankName.setText(shortName);
        tvAmount.setText(CustomUtil.convertLongToFormattedString(amount));
        tvDate.setText(strDate);
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

        // Change icon based on name
        switch (shortName) {
            case "ACB":
                imgBankIcon.setImageResource(R.mipmap.acb_icon);
                break;
            case "TPBank":
                imgBankIcon.setImageResource(R.mipmap.tpbank_icon);
                break;
            case "DAF":
                imgBankIcon.setImageResource(R.mipmap.daf_icon);
                break;
            case "SeABank":
                imgBankIcon.setImageResource(R.mipmap.seabank_icon);
                break;
            case "MSB":
                imgBankIcon.setImageResource(R.mipmap.msb_icon);
                break;
            case "Techcomban":
                imgBankIcon.setImageResource(R.mipmap.techcombank_icon);
                break;
            case "VPBank":
                imgBankIcon.setImageResource(R.mipmap.vpbank_icon);
                break;
            case "VCB":
                imgBankIcon.setImageResource(R.mipmap.vcb_icon);
                break;
            case "HSBC":
                imgBankIcon.setImageResource(R.mipmap.hsbc_icon);
                break;
            case "Agribank":
                imgBankIcon.setImageResource(R.mipmap.agribank_icon);
                break;
            default:
                imgBankIcon.setImageResource(R.mipmap.default_bank_icon);
                break;
        }
        return rowView;

    }


}
