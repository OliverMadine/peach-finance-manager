package com.olivermadine.financeManager.financeManager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.olivermadine.financeManager.R;

import java.util.Locale;

// Manages user interface.
public class FinanceActivity extends AppCompatActivity {

    private static final String BUDGET_LABEL_TAG_EXT = "_budget";
    private static final String BUDGET_BAR_TAG_EXT = "_bar";

    private final FinancePresenter presenter = new FinancePresenter(this);
    private LinearLayout categoriesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finance_manager);
        refreshBalance();

        // load categories
        categoriesLayout = findViewById(R.id.categories);
        presenter.getCategories().forEach(this::createCategory);

        // init buttons
        final Button logTransActionButton = findViewById(R.id.log_transaction);
        logTransActionButton.setOnClickListener(v -> createLogTransactionLayout());
        final Button addCategoryBtn = findViewById(R.id.add_category);
        addCategoryBtn.setOnClickListener(v -> createAddCategoryLayout());
    }

    public void createCategory(String name) {
        final LinearLayout newCategory = (LinearLayout) getLayoutInflater().inflate(R.layout.category, null);

        final TextView categoryLabel = newCategory.findViewById(R.id.category_name);
        categoryLabel.setText(name);

        final ProgressBar budgetBar = newCategory.findViewById(R.id.budget_bar);
        budgetBar.setTag(name + BUDGET_BAR_TAG_EXT);

        final TextView budgetLabel = newCategory.findViewById(R.id.budget);
        budgetLabel.setTag(name + BUDGET_LABEL_TAG_EXT);

        categoriesLayout.addView(newCategory);
        refreshBudgetProgress(name);
    }

    protected void refreshBalance() {
        setTitle("Balance " + showCurrency(presenter.getBalance()));
    }

    protected void refreshBudgetProgress(String categoryName) {
        final ProgressBar budgetBar = categoriesLayout.findViewWithTag(categoryName + BUDGET_BAR_TAG_EXT);
        final TextView budgetLabel = categoriesLayout.findViewWithTag(categoryName + BUDGET_LABEL_TAG_EXT);
        budgetBar.setProgress(presenter.getSpendingProgress(categoryName));
        budgetLabel.setText(showCurrency(presenter.getRemainingBudget(categoryName)));
    }

    private void createAddCategoryLayout() {
        // init layout builder
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Category");
        final LinearLayout addLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.add_category, null);

        alertBuilder.setPositiveButton("Add", (dialog, which) -> {
            final EditText categoryField = addLayout.findViewById(R.id.add_category_field);
            final EditText budgetField = addLayout.findViewById(R.id.add_budget_field);

            presenter.addCategory(categoryField.getText().toString(), budgetField.getText().toString());
        });

        alertBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        alertBuilder.setView(addLayout).create().show();
    }

    private void createLogTransactionLayout() {
        // init layout builder
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Transaction");
        final LinearLayout logLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.log_transaction, null);

        // init category spinner
        final Spinner categorySpinner = logLayout.findViewById(R.id.log_category_spinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                presenter.getCategories()
        );
        categorySpinner.setAdapter(adapter);

        alertBuilder.setPositiveButton("Log", (dialog, which) -> {
            final EditText amountField = logLayout.findViewById(R.id.log_amount_field);
            if (categorySpinner.getSelectedItem() != null) {
                final String selectedCategory = categorySpinner.getSelectedItem().toString();
                presenter.logTransaction(selectedCategory, amountField.getText().toString());
            }
        });

        alertBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        alertBuilder.setView(logLayout).create().show();
    }

    private String showCurrency(float f) {
        return getString(R.string.currency) + " " + String.format(Locale.UK, "%.2f", f);
    }
}
