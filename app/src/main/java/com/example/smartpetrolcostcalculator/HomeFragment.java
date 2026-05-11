package com.example.smartpetrolcostcalculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class HomeFragment extends Fragment {

    // Constant subsidy rate based on assignment requirement
    private static final double BUDI_SUBSIDY_RATE = 1.99;

    // UI components
    private Spinner spinnerPetrolType;
    private EditText etPetrolPrice, etFuelUsage;
    private RadioButton rbEligibleYes, rbEligibleNo;
    private Button btnCalculate, btnReset;
    private TextView tvTotalPetrolCost, tvBudiRebate, tvTotalSaving;

    public HomeFragment() {
        // Required empty public constructor for Fragment
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Connect this Java Fragment to fragment_home.xml
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Link XML views with Java variables
        spinnerPetrolType = view.findViewById(R.id.spinnerPetrolType);
        etPetrolPrice = view.findViewById(R.id.etPetrolPrice);
        etFuelUsage = view.findViewById(R.id.etFuelUsage);
        rbEligibleYes = view.findViewById(R.id.rbEligibleYes);
        rbEligibleNo = view.findViewById(R.id.rbEligibleNo);
        btnCalculate = view.findViewById(R.id.btnCalculate);
        btnReset = view.findViewById(R.id.btnReset);
        tvTotalPetrolCost = view.findViewById(R.id.tvTotalPetrolCost);
        tvBudiRebate = view.findViewById(R.id.tvBudiRebate);
        tvTotalSaving = view.findViewById(R.id.tvTotalSaving);

        // Setup petrol type dropdown
        setupPetrolSpinner();

        // Calculate button action
        btnCalculate.setOnClickListener(v -> calculatePetrolCost());

        // Reset button action
        btnReset.setOnClickListener(v -> resetCalculator());

        return view;
    }

    private void setupPetrolSpinner() {
        // Spinner options required by assignment: RON95, RON97, Diesel
        String[] petrolTypes = {
                getString(R.string.option_ron95),
                getString(R.string.option_ron97),
                getString(R.string.option_diesel)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                petrolTypes
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPetrolType.setAdapter(adapter);
    }

    private void calculatePetrolCost() {
        String priceText = etPetrolPrice.getText().toString().trim();
        String usageText = etFuelUsage.getText().toString().trim();

        // Prevent crash if petrol price is empty
        if (TextUtils.isEmpty(priceText)) {
            etPetrolPrice.setError(getString(R.string.error_empty_price));
            etPetrolPrice.requestFocus();
            return;
        }

        // Prevent crash if fuel usage is empty
        if (TextUtils.isEmpty(usageText)) {
            etFuelUsage.setError(getString(R.string.error_empty_usage));
            etFuelUsage.requestFocus();
            return;
        }

        try {
            double petrolPrice = Double.parseDouble(priceText);
            double fuelUsage = Double.parseDouble(usageText);

            // Avoid negative values because petrol price and fuel usage cannot be negative
            if (petrolPrice < 0 || fuelUsage < 0) {
                Toast.makeText(requireContext(),
                        getString(R.string.error_negative_input),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String petrolType = spinnerPetrolType.getSelectedItem().toString();
            boolean isEligible = rbEligibleYes.isChecked();

            // Formula 1: Total petrol cost = fuel usage x petrol price per liter
            double totalPetrolCost = fuelUsage * petrolPrice;

            // Formula 2:
            // BUDI rebate = fuel usage x RM1.99
            // CRITICAL LOGIC: Only apply if petrol type is RON95 AND eligible status is Yes
            double budiRebate = 0.00;

            if (petrolType.equals(getString(R.string.option_ron95)) && isEligible) {
                budiRebate = fuelUsage * BUDI_SUBSIDY_RATE;
            }

            // Formula 3: Final payable / total saving = total petrol cost - BUDI rebate
            double finalPayable = totalPetrolCost - budiRebate;

            // Display results formatted to 2 decimal places
            tvTotalPetrolCost.setText(String.format(
                    Locale.getDefault(),
                    "Total Petrol Cost: RM %.2f",
                    totalPetrolCost
            ));

            tvBudiRebate.setText(String.format(
                    Locale.getDefault(),
                    "BUDI Rebate: RM %.2f",
                    budiRebate
            ));

            tvTotalSaving.setText(String.format(
                    Locale.getDefault(),
                    "Final Payable: RM %.2f",
                    finalPayable
            ));

        } catch (NumberFormatException e) {
            // Prevent app crash if user enters invalid number format
            Toast.makeText(requireContext(),
                    getString(R.string.error_invalid_input),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void resetCalculator() {
        // Clear inputs
        etPetrolPrice.setText("");
        etFuelUsage.setText("");

        // Reset spinner to RON95
        spinnerPetrolType.setSelection(0);

        // Reset eligibility to No
        rbEligibleNo.setChecked(true);

        // Reset result text
        tvTotalPetrolCost.setText(getString(R.string.total_petrol_cost_default));
        tvBudiRebate.setText(getString(R.string.budi_rebate_default));
        tvTotalSaving.setText(getString(R.string.total_saving_default));

        // Clear possible input errors
        etPetrolPrice.setError(null);
        etFuelUsage.setError(null);
    }
}