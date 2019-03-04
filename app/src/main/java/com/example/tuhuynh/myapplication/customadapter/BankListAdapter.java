package com.example.tuhuynh.myapplication.customadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.bank.InterestAmount;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class BankListAdapter extends ArrayAdapter<BankInfo> implements Filterable {

    private List<BankInfo> originalBanks;
    private List<BankInfo> filteredBanks;
    private BankFilter filter;

    public BankListAdapter(Context context, int textViewResourceId, List<BankInfo> objects) {
        super(context, textViewResourceId, objects);
        this.originalBanks = objects;
        this.filteredBanks = objects;
    }

    @Override
    public int getCount() {
        return filteredBanks.size();
    }

    @Override
    public BankInfo getItem(int i) {
        return filteredBanks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
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

        BankInfo bankInfo = filteredBanks.get(position);
        tvBankName.setText(bankInfo.getName());

        List<InterestAmount> interestAmounts = bankInfo.getInterestAmounts();
        tvMonth.setText(new DecimalFormat("##.##").format(interestAmounts.get(0).getMonth()));
        tvInterest.setText(new DecimalFormat("##.##").format(interestAmounts.get(0).getInterest()));

        // Change icon based on name
        String shortName = bankInfo.getShortName();

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

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new BankFilter();
        }
        return filter;
    }

    private class BankFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();

            if (constraint.toString().length() > 0) {
                ArrayList<BankInfo> filteredItems = new ArrayList<>();

                for (int i = 0, l = originalBanks.size(); i < l; i++) {
                    BankInfo bankInfo = originalBanks.get(i);
                    if (bankInfo.getName().toLowerCase().contains(constraint))
                        filteredItems.add(bankInfo);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;

            } else {
                synchronized (this) {
                    result.values = originalBanks;
                    result.count = originalBanks.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredBanks = (ArrayList<BankInfo>) results.values;
            if (filteredBanks.size() > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }


}
