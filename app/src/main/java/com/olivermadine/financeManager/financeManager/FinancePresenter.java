package com.olivermadine.financeManager.financeManager;


import android.preference.PreferenceManager;

import com.olivermadine.financeManager.R;

import java.util.List;

// Manages input validation, database calls, and program logic.
public class FinancePresenter {

    private final FinanceActivity activity;
    private final FinanceSqlHelper financeSqlHelper;

    protected FinancePresenter(FinanceActivity activity) {
        this.activity = activity;
        financeSqlHelper = new FinanceSqlHelper(activity);
    }

    protected float getBalance() {
        return PreferenceManager.getDefaultSharedPreferences(activity).getFloat(activity.getString(R.string.balance_pref), 0);
    }

    protected float getRemainingBudget(String name) {
        return financeSqlHelper.getBudget(name) - financeSqlHelper.getSpent(name);
    }

    protected int getSpendingProgress(String category) {
        final float spent = financeSqlHelper.getSpent(category);
        final float budget = financeSqlHelper.getBudget(category);
        if (budget == 0) {
            return 100;
        }
        final int progress = (int) (spent / budget * 100);
        return Math.min(progress, 100);
    }

    protected List<String> getCategories() {
        return financeSqlHelper.getCategories();
    }


    protected void addCategory(String name, String budgetString) {
        if (validCurrency(budgetString) && !getCategories().contains(name)) {
            financeSqlHelper.insert(name, Float.parseFloat(budgetString));
            activity.createCategory(name);
        }
    }

    protected void logTransaction(String categoryName, String amountString) {
        if (validCurrency(amountString)) {
            final float amount = Float.parseFloat(amountString);
            // increase spent
            increaseSpent(categoryName, amount);

            // decrease balance
            PreferenceManager.getDefaultSharedPreferences(activity)
                    .edit()
                    .putFloat(activity.getString(R.string.balance_pref), getBalance() - amount)
                    .apply();

            // refresh view
            activity.refreshBalance();
            activity.refreshBudgetProgress(categoryName);
        }
    }

    protected void increaseSpent(String category, float spent) {
        financeSqlHelper.setSpent(category, financeSqlHelper.getSpent(category) + spent);
    }

    // valid for numbers with up to 2 decimal places
    private boolean validCurrency(String currency) {
        return currency.matches("\\d+(\\.\\d{1,2})?$");
    }
}
