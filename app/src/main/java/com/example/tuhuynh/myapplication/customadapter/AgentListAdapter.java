package com.example.tuhuynh.myapplication.customadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.example.tuhuynh.myapplication.agent.AgentProfile;
import com.example.tuhuynh.myapplication.user.UserStatus;
import com.example.tuhuynh.myapplication.util.CustomUtil;

import java.util.ArrayList;
import java.util.List;


public class AgentListAdapter extends ArrayAdapter<AgentProfile> implements Filterable {

    private List<AgentProfile> originalAgents;
    private List<AgentProfile> filteredAgents;
    private AgentFilter filter;

    public AgentListAdapter(Context context, int textViewResourceId, List<AgentProfile> agents) {
        super(context, textViewResourceId, agents);
        this.originalAgents = agents;
        this.filteredAgents = agents;
    }

    @Override
    public int getCount() {
        return filteredAgents.size();
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View rowView = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (rowView == null) {
            rowView = inflater.inflate(R.layout.agent_list_adapter, null);
        }

        // Initial elements
        ImageView imgAvatar = rowView.findViewById(R.id.img_avatar);
        TextView tvName = rowView.findViewById(R.id.tv_name);
        TextView tvBank = rowView.findViewById(R.id.tv_bank);
        TextView tvPhone = rowView.findViewById(R.id.tv_phone);
        TextView tvEmail = rowView.findViewById(R.id.tv_email);
        TextView tvStatus = rowView.findViewById(R.id.tv_status);

        // Get value
        AgentProfile agent = filteredAgents.get(position);
        String bank = agent.getWorkBank().getName();
        String phone = agent.getPhone();
        String email = agent.getEmail();
        String status = agent.getStatus();

        // Set value for text view
        if (CustomUtil.hasMeaning(agent.getSurname())) {
            String fullName = agent.getName() + agent.getSurname();
            tvName.setText(fullName);
        } else {
            tvName.setText(agent.getName());
        }
        tvBank.setText(bank);
        tvPhone.setText(phone);
        tvEmail.setText(email);
        tvStatus.setText(status);

        if (status.equalsIgnoreCase(UserStatus.DEACTIVATE)) {
            tvStatus.setTextColor(Color.parseColor("#CC0000"));
        } else {
            tvStatus.setTextColor(Color.parseColor("#009900"));
        }
        imgAvatar.setImageResource(R.drawable.ic_user_avatar);

        return rowView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AgentFilter();
        }
        return filter;
    }

    private class AgentFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();

            if (constraint.toString().length() > 0) {
                ArrayList<AgentProfile> filteredItems = new ArrayList<>();

                for (int i = 0, l = originalAgents.size(); i < l; i++) {
                    AgentProfile agentProfile = originalAgents.get(i);
                    if (agentProfile.getName().toLowerCase().contains(constraint))
                        filteredItems.add(agentProfile);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;

            } else {
                synchronized (this) {
                    result.values = originalAgents;
                    result.count = originalAgents.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredAgents = (ArrayList<AgentProfile>) results.values;
            if (filteredAgents.size() > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }


}
