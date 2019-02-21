package com.example.tuhuynh.myapplication.customadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.bank.InterestAmount;

import java.text.DecimalFormat;
import java.util.List;

public class BankListAdapter extends ArrayAdapter<BankInfo> {

    private List<BankInfo> banks;

    public BankListAdapter(Context context, int textViewResourceId, List<BankInfo> objects) {
        super(context, textViewResourceId, objects);
        this.banks = objects;
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
            rowView = inflater.inflate(R.layout.bank_list_adapter, null);
        }

        ImageView imgBankIcon = rowView.findViewById(R.id.imgBankIcon);
        TextView tvBankName = rowView.findViewById(R.id.tvBankName);
        TextView tvMonth = rowView.findViewById(R.id.tv_month);
        TextView tvInterest = rowView.findViewById(R.id.tvInterest);

        tvBankName.setText(banks.get(position).getName());

        List<InterestAmount> interestAmounts = banks.get(position).getInterestAmounts();
        tvMonth.setText(new DecimalFormat("##.##").format(interestAmounts.get(0).getMonth()));
        tvInterest.setText(new DecimalFormat("##.##").format(interestAmounts.get(0).getInterest()));

        // Change icon based on name
        String shortName = banks.get(position).getShortName();

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
