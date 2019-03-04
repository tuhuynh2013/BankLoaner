package com.example.tuhuynh.myapplication.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.bank.BankInfo;
import com.example.tuhuynh.myapplication.bank.BankInformationActivity;
import com.example.tuhuynh.myapplication.bank.InterestAmount;
import com.example.tuhuynh.myapplication.customadapter.BankListAdapter;
import com.example.tuhuynh.myapplication.util.CustomUtil;
import com.example.tuhuynh.myapplication.util.LoanCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class LoanSearchActivity extends AppCompatActivity {

    private TextView tvSearchMsg;
    private ListView lvSearchResult;
    private EditText edtDebitAmount, edtPayment;
    private RadioGroup rdgMonth;
    private Button btnSearch;
    private ProgressDialog pDialog;

    private List<BankInfo> banks;
    private List<BankInfo> banksResult = new ArrayList<>();

    private String strDebitAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_search);
        setTitle(getString(R.string.title_loan_search));

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initialViews();

        Intent i = this.getIntent();
        banks = (List<BankInfo>) i.getSerializableExtra("banks");

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

    }

    /**
     *
     */
    private void initialViews() {
        tvSearchMsg = findViewById(R.id.tv_search_msg);
        lvSearchResult = findViewById(R.id.lv_search_result);
        edtDebitAmount = findViewById(R.id.edt_debit_amount);
        edtPayment = findViewById(R.id.edt_payment);
        rdgMonth = findViewById(R.id.rdg_month);
        RadioButton rd12 = findViewById(R.id.rd_12_months);
        btnSearch = findViewById(R.id.btn_search);
        pDialog = new ProgressDialog(this);

        // selected 12 months by default
        rdgMonth.check(rd12.getId());

        edtDebitAmount.addTextChangedListener(onTextChangedListener(edtDebitAmount));
        edtPayment.addTextChangedListener(onTextChangedListener(edtPayment));
    }

    /**
     *
     */
    private void search() {
        banksResult.clear();
        tvSearchMsg.setVisibility(View.GONE);
        validateInput();

        if (banksResult.isEmpty()) {
            setMessage(getString(R.string.msg_not_enough_payment));
        } else {
            // Sort for the smallest interest
            Collections.sort(banksResult, new Comparator<BankInfo>() {
                @Override
                public int compare(BankInfo b1, BankInfo b2) {
                    Double interestB1 = b1.getInterestAmounts().get(0).getInterest();
                    Double interestB2 = b2.getInterestAmounts().get(0).getInterest();
                    return interestB1.compareTo(interestB2);
                }
            });

            BankListAdapter bankListAdapter = new BankListAdapter(this, R.layout.bank_list_adapter, banksResult);
            lvSearchResult.setAdapter(bankListAdapter);
            pDialog.dismiss();

            lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    BankInfo bank = (BankInfo) parent.getItemAtPosition(position);
                    Toast.makeText(v.getContext(), bank.getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), BankInformationActivity.class);
                    intent.putExtra("caller", "LoanSearchActivity");
                    intent.putExtra("debitAmount", strDebitAmount);
                    intent.putExtra("bank", bank);
                    parent.getContext().startActivity(intent);
                }
            });
        }

    }

    /**
     *
     */
    private void validateInput() {
        strDebitAmount = edtDebitAmount.getText().toString().trim();
        // Get checked value of month
        RadioButton rd = findViewById(rdgMonth.getCheckedRadioButtonId());
        int month = Integer.parseInt(rd.getText().toString());
        String strPayment = edtPayment.getText().toString().trim();

        if (strDebitAmount.isEmpty()) {
            edtDebitAmount.setError(getString(R.string.msg_empty_debit_amount));
            edtDebitAmount.requestFocus();
            return;
        }

        if (strPayment.isEmpty()) {
            edtPayment.setError(getString(R.string.msg_empty_payment));
            edtPayment.requestFocus();
            return;
        }
        displayProgressDialog();
        Long debitAmount = CustomUtil.convertFormattedStringToLong(strDebitAmount);
        Long payment = CustomUtil.convertFormattedStringToLong(strPayment);

        getBanksResult(debitAmount, month, payment);
    }

    /**
     *
     */
    private void getBanksResult(Long debitAmount, int month, Long payment) {
        Long principal = LoanCalculator.calculatePrincipal(month, Objects.requireNonNull(debitAmount));
        if (principal > Objects.requireNonNull(payment)) {
            setMessage(getString(R.string.msg_not_enough_payment));
        } else {
            for (BankInfo bank : banks) {
                for (InterestAmount interest : bank.getInterestAmounts()) {
                    // Find bank has expected month
                    if (interest.getMonth() == month) {
                        // Calculate total payment per month
                        Long interestAmount = (long) LoanCalculator.calculateInterest(debitAmount, interest.getInterest());
                        Long totalPayment = principal + interestAmount;
                        if (totalPayment > payment) {
                            setMessage(getString(R.string.msg_not_enough_payment));
                        } else {
                            BankInfo tempBank = new BankInfo();
                            tempBank.setId(bank.getId());
                            tempBank.setName(bank.getName());
                            tempBank.setShortName(bank.getShortName());
                            tempBank.setInterestAmounts(interest);
                            banksResult.add(tempBank);
                        }
                    }
                }
            }
        }

    }

    /**
     *
     */
    private void setMessage(String msg) {
        tvSearchMsg.setVisibility(View.VISIBLE);
        tvSearchMsg.setText(msg);
        pDialog.dismiss();
    }

    /**
     *
     */
    private TextWatcher onTextChangedListener(final EditText editText) {

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editText.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    String formattedString = CustomUtil.convertLongToFormattedString(longval);

                    // Setting text after format to EditText
                    editText.setText(formattedString);
                    editText.setSelection(editText.getText().length());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                editText.addTextChangedListener(this);
            }
        };
    }

    /**
     * Display Progress bar
     */
    private void displayProgressDialog() {
        pDialog.setMessage(getString(R.string.dialog_search_banks));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }


}
