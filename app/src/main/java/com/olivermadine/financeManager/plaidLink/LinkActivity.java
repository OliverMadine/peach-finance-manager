/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.olivermadine.financeManager.plaidLink;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.olivermadine.financeManager.R;
import com.olivermadine.financeManager.financeManager.FinanceActivity;
import com.olivermadine.financeManager.linkNetwork.LinkTokenRequester;
import com.plaid.link.Plaid;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkAccountBalance;
import com.plaid.link.result.LinkResultHandler;

import kotlin.Unit;

public class LinkActivity extends AppCompatActivity {

    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // if the balance is already known then do not prompt relink
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.contains(getString(R.string.balance_pref))) {
            switchToFinanceManager();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_bank);

        result = findViewById(R.id.result);

        final Button button = findViewById(R.id.open_link);
        button.setOnClickListener(view -> {
            setOptionalEventListener();
            openLink();
        });
    }

    private void setOptionalEventListener() {
        Plaid.setLinkEventListener(linkEvent -> {
            Log.i("Event", linkEvent.toString());
            return Unit.INSTANCE;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!linkResultHandler.onActivityResult(requestCode, resultCode, data)) {
            Log.i(LinkActivity.class.getSimpleName(), "Not handled");
        }
    }

    private void openLink() {
        LinkTokenRequester.INSTANCE.getToken().subscribe(this::onLinkTokenSuccess, this::onLinkTokenError);
    }

    private void onLinkTokenSuccess(String token) {
        Plaid.create(
                getApplication(),
                new LinkTokenConfiguration.Builder()
                        .token(token)
                        .build())
                .open(this);
    }

    private void onLinkTokenError(Throwable error) {
        if (error instanceof java.net.ConnectException) {
            Toast.makeText(
                    this,
                    "Please run `sh start_server.sh <client_id> <sandbox_secret>`",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private final LinkResultHandler linkResultHandler = new LinkResultHandler(
            linkSuccess -> {
                final double totalBalance = linkSuccess.getMetadata().getAccounts()
                        .stream()
                        .mapToDouble(acc -> getAvailOrCurrent(acc.getBalance()))
                        .sum();
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putFloat(getString(R.string.balance_pref), (float) totalBalance)
                        .apply();
                switchToFinanceManager();
                return Unit.INSTANCE;
            },
            linkExit -> {
                if (linkExit.getError() != null) {
                    result.setText(getString(
                            R.string.content_exit,
                            linkExit.getError().getDisplayMessage(),
                            linkExit.getError().getErrorCode()));
                } else {
                    result.setText(getString(
                            R.string.content_cancel,
                            linkExit.getMetadata().getStatus() != null ? linkExit.getMetadata()
                                    .getStatus()
                                    .getJsonValue() : "unknown"));
                }
                return Unit.INSTANCE;
            }
    );

    // By the plaid specification, if available  is null, current will to not be null.
    private Double getAvailOrCurrent(LinkAccountBalance linkBalance) {
        final Double available = linkBalance.getAvailable();
        if (available == null) {
            return linkBalance.getCurrent();
        }
        return available;
    }

    private void switchToFinanceManager() {
        startActivity(new Intent(this, FinanceActivity.class));
        finish();
    }
}
