package com.example.tuhuynh.myapplication.appication;

//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.example.tuhuynh.myapplication.R;
//import com.example.tuhuynh.myapplication.bank.BankArrayAdapter;
//import com.example.tuhuynh.myapplication.bank.BankInfo;
//import com.example.tuhuynh.myapplication.bank.BankInformationActivity;
//import com.example.tuhuynh.myapplication.connecthandler.RequestHandler;
//import com.example.tuhuynh.myapplication.connecthandler.URLs;
//import com.example.tuhuynh.myapplication.user.User;
//import com.example.tuhuynh.myapplication.util.SharedPrefManager;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.List;

public class ApplicationManagementFragment extends Fragment {
//
//    private View view;
//    private List<BankInfo> banks;
//
//
//    public ApplicationManagementFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        view = getLayoutInflater().inflate(R.layout.frag_application_list, container, false);
//        User user = SharedPrefManager.getInstance(getContext()).getUser();
//        getCustomerApplication(user);
//
//        return view;
//    }
//
//    private void getCustomerApplication(final User user) {
//
//        class CustomerApplicationList extends AsyncTask<Void, Void, String> {
//
//            private ProgressBar progressBar;
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                progressBar = view.findViewById(R.id.progressBar);
//                progressBar.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                // Creating request handler object
//                RequestHandler requestHandler = new RequestHandler();
//                HashMap<String, String> params = new HashMap<>();
//                params.put("user_id", Integer.toString(user.getId()));
//                // Return the response
//                return requestHandler.sendPostRequest(URLs.URL_GET_CUSTOMER_APPLICATIONS, params);
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                progressBar.setVisibility(View.GONE);
//
//                try {
//                    // Converting response to json object
//                    JSONObject obj = new JSONObject(s);
//                    String message = obj.getString("message");
//
//                    // If no error in response
//                    if (!obj.getBoolean("error") && message.equalsIgnoreCase(getString(R.string.msg_retrieve_banklist_success))) {
//
//                        // Getting the user from the response
//                        JSONArray jsonArray = obj.getJSONArray("banks");
//                        banks = extractBanklist(jsonArray);
//
//                        ListView listView = view.findViewById(R.id.lv_bank_list);
//                        BankArrayAdapter bankArrayAdapter = new BankArrayAdapter(view.getContext(), R.layout.bank_adapter, banks);
//                        listView.setAdapter(bankArrayAdapter);
//
//                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                                BankInfo bank = (BankInfo) parent.getItemAtPosition(position);
//                                Toast.makeText(v.getContext(), bank.getName(), Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(parent.getContext(), BankInformationActivity.class);
//                                intent.putExtra("bank", bank);
//                                parent.getContext().startActivity(intent);
//                            }
//                        });
//
//                    } else {
//                        Toast.makeText(view.getContext(), R.string.error_retrieve_fail, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//        BankList banklist = new BankList();
//        banklist.execute();
//
//    }
//
}
