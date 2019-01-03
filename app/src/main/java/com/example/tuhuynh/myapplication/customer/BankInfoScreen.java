package com.example.tuhuynh.myapplication.customer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuhuynh.myapplication.BankInfo;
import com.example.tuhuynh.myapplication.R;
import com.example.tuhuynh.myapplication.user.SharedPrefManager;
import com.example.tuhuynh.myapplication.user.User;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class BankInfoScreen extends AppCompatActivity implements AsyncResponse {

    private EditText edtAmount;
    private RadioGroup rdgYear;
    private RadioButton rd;
    private TextView tvInterest;
    private TableLayout tblInterestTable;
    private Button btnCalculate;
    private Button btnApply;

    BankInfo bankInfo;
    User user = SharedPrefManager.getInstance(this).getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_info_screen);

        // Create Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final Intent intent = this.getIntent();
        bankInfo = (BankInfo) intent.getSerializableExtra("bank");
        setTitle(bankInfo.getName());

        // Initial elements
        edtAmount = findViewById(R.id.edtAmount);
        rdgYear = findViewById(R.id.rgpYear);
        tvInterest = findViewById(R.id.tvInterest);
        tblInterestTable = findViewById(R.id.tbl_InterestTable);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnApply = findViewById(R.id.btn_apply);

        rdgYear.setOrientation(LinearLayout.HORIZONTAL);
        createRadioButton(bankInfo);
        // Get checked radio button
        rd = findViewById(rdgYear.getCheckedRadioButtonId());
        // Initial interest value
        setInterest(rd.getId(), bankInfo);

        rdgYear.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setInterest(checkedId, bankInfo);
            }
        });

        edtAmount.addTextChangedListener(onTextChangedListener());

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tblInterestTable.removeAllViews();

                // Get checked radio button
                rd = findViewById(rdgYear.getCheckedRadioButtonId());
                int month = Integer.parseInt(rd.getText().toString()) * 12;
                Long amount = convertFormattedStringToLong(edtAmount.getText().toString());
                Double interest = Double.parseDouble(tvInterest.getText().toString());

                if (isVaildAmount(amount)) {
                    generateInterestTable(month, amount, interest);
                }
            }
        });

    }

    /**
     *
     */
    private boolean isVaildAmount(Long amount) {
        if (amount != null) {
            if (amount > 100000000) {
                if (amount < Long.parseLong("100000000000")) {
                    return true;
                } else {
                    edtAmount.setError(getString(R.string.error_exceed_amount));
                    edtAmount.requestFocus();
                    return false;
                }
            } else {
                edtAmount.setError(getString(R.string.error_atleast_amount));
                edtAmount.requestFocus();
                return false;
            }
        } else {
            edtAmount.setError(getString(R.string.error_empty_amount));
            edtAmount.requestFocus();
            return false;
        }

    }

    /**
     *
     */
    private void generateInterestTable(int month, long amount, double interest) {

        createTableTitle();

        // Calculate principal
        long principal = calculatePrincipal((long) month, amount);
        String formattedPrincipal = convertLongToFormattedString(principal);

        long totalInterest = 0;

        // Create row for interest table
        for (int i = 0; i <= month + 1; i++) {

            TextView monthCell = new TextView(this);
            TextView balanceCell = new TextView(this);
            TextView principalCell = new TextView(this);
            TextView interestCell = new TextView(this);
            TextView totalPaymentCell = new TextView(this);

            setTextProperties(monthCell);
            setTextProperties(balanceCell);
            setTextProperties(principalCell);
            setTextProperties(interestCell);
            setTextProperties(totalPaymentCell);

            monthCell.setText(String.valueOf(i));
            if (i == 0) {
                balanceCell.setText(convertLongToFormattedString(amount));
            } else if (i == month + 1) {
                monthCell.setText(R.string.total);
                setTitleProperties(monthCell);
                interestCell.setText(convertLongToFormattedString(totalInterest));
                setTitleProperties(interestCell);
            } else {
                principalCell.setText(formattedPrincipal);

                long tempInterest = calculateInterest(amount, (long) interest);
                totalInterest += tempInterest;
                interestCell.setText(convertLongToFormattedString(tempInterest));

                amount = amount - principal;
                balanceCell.setText(convertLongToFormattedString(amount));

                totalPaymentCell.setText(convertLongToFormattedString(tempInterest + principal));
            }

            // Adding text views to table row
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new LinearLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(monthCell);
            tr.addView(balanceCell);
            tr.addView(principalCell);
            tr.addView(interestCell);
            tr.addView(totalPaymentCell);

            if (i % 2 == 0) {
                tr.setBackgroundColor(Color.GRAY);
            }
            // Adding table row to table layout
            tblInterestTable.addView(tr);
        }

    }

    /**
     * Create titles for interest table
     */
    private void createTableTitle() {

        TextView tvTitleMonth = new TextView(this);
        tvTitleMonth.setText(R.string.month);

        TextView tvTitleBalance = new TextView(this);
        tvTitleBalance.setText(R.string.balance);

        TextView tvTitlePrincipal = new TextView(this);
        tvTitlePrincipal.setText(R.string.principal);

        TextView tvTitleInterest = new TextView(this);
        tvTitleInterest.setText(R.string.interest);

        TextView tvTitleTotalPayment = new TextView(this);
        tvTitleTotalPayment.setText(R.string.total_payment);

        setTitleProperties(tvTitleMonth);
        setTitleProperties(tvTitleBalance);
        setTitleProperties(tvTitlePrincipal);
        setTitleProperties(tvTitleInterest);
        setTitleProperties(tvTitleTotalPayment);

        // Adding text views to table row
        TableRow trTitle = new TableRow(this);
        trTitle.addView(tvTitleMonth);
        trTitle.addView(tvTitleBalance);
        trTitle.addView(tvTitlePrincipal);
        trTitle.addView(tvTitleInterest);
        trTitle.addView(tvTitleTotalPayment);
        trTitle.setGravity(Gravity.CENTER);

        // Adding table row to table layout
        tblInterestTable.addView(trTitle);
    }

    /**
     *
     */
    private void setTitleProperties(TextView textView) {
        setTextProperties(textView);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
    }

    /**
     *
     */
    private void setTextProperties(TextView textView) {
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(10, 10, 10, 10);
    }

    /**
     *
     */
    private long calculateInterest(long amount, long interest) {
        return (amount * interest) / 1200;
    }

    /**
     *
     */
    private long calculatePrincipal(long month, long amount) {
        return amount / month;
    }

    /**
     *
     */
    private String convertLongToFormattedString(long l) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        return formatter.format(l);
    }

    /**
     *
     */
    private Long convertFormattedStringToLong(String s) {
        if (!s.isEmpty()) {
            return Long.parseLong(s.replace(",", ""));
        } else {
            return null;
        }
    }

    /**
     *
     */
    private void createRadioButton(BankInfo bankInfo) {
        for (int i = 0; i < bankInfo.getInterestAmounts().size(); i++) {
            int year = bankInfo.getInterestAmounts().get(i).getYear();
            RadioButton rb = new RadioButton(this);
            rb.setId(i);
            rb.setText(new DecimalFormat("##.##").format(year));
            if (i == 0)
                rb.setChecked(true);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            rb.setLayoutParams(params);
            rdgYear.addView(rb);
        }
    }

    /**
     *
     */
    private void setInterest(int checkedId, BankInfo bankInfo) {
        Double interest = bankInfo.getInterestAmounts().get(checkedId).getInterest();
        tvInterest.setText(new DecimalFormat("##.##").format(interest));
    }

    /**
     *
     */
    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edtAmount.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);
                    String formattedString = convertLongToFormattedString(longval);

                    // Setting text after format to EditText
                    edtAmount.setText(formattedString);
                    edtAmount.setSelection(edtAmount.getText().length());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                edtAmount.addTextChangedListener(this);
            }
        };
    }

    @Override
    public void processFinish(CustomerInfo output) {
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get checked radio button
                rd = findViewById(rdgYear.getCheckedRadioButtonId());
                int month = Integer.parseInt(rd.getText().toString()) * 12;
                Long amount = convertFormattedStringToLong(edtAmount.getText().toString());
                Double interest = Double.parseDouble(tvInterest.getText().toString());

                if (isVaildAmount(amount)) {
                    CustomerProfileAsyncTask asyncTask = new CustomerProfileAsyncTask(getApplicationContext(), v
                            /*findViewById(android.R.id.content).getRootView()*/, user);
                    asyncTask.execute();

                    Intent intent = new Intent(getApplicationContext(), LoanApplicationActivity.class);
                    intent.putExtra("bank", bankInfo);
                    intent.putExtra("month", month);
                    intent.putExtra("amount", amount);
                    intent.putExtra("interest", interest);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });

    }


}
