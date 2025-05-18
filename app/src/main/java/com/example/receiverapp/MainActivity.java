package com.example.receiverapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.*;

import java.util.List;

public class MainActivity extends Activity {
    EditText phoneEditText, amountEditText, ussd1EditText, ussd2EditText;
    Button sim1Button, sim2Button;
    String secretCode = "9261";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // التحقق من الرمز السري لمرة واحدة
        SharedPreferences prefs = getSharedPreferences("init", MODE_PRIVATE);
        if (!prefs.getBoolean("verified", false)) {
            final EditText input = new EditText(this);
            input.setHint("أدخل الرمز السري");
            new AlertDialog.Builder(this)
                .setTitle("التحقق")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("تأكيد", (dialog, which) -> {
                    if (input.getText().toString().equals(secretCode)) {
                        prefs.edit().putBoolean("verified", true).apply();
                        recreate();
                    } else {
                        finish();
                    }
                }).show();
            return;
        }

        setContentView(R.layout.activity_main);
        phoneEditText = findViewById(R.id.phoneEditText);
        amountEditText = findViewById(R.id.amountEditText);
        ussd1EditText = findViewById(R.id.ussd1EditText);
        ussd2EditText = findViewById(R.id.ussd2EditText);
        sim1Button = findViewById(R.id.sim1Button);
        sim2Button = findViewById(R.id.sim2Button);

        sim1Button.setOnClickListener(v -> makeCall(1));
        sim2Button.setOnClickListener(v -> makeCall(2));
    }

    void makeCall(int simSlot) {
        String phone = phoneEditText.getText().toString();
        String amount = amountEditText.getText().toString();
        String baseCode = simSlot == 1 ? ussd1EditText.getText().toString() : ussd2EditText.getText().toString();

        if (baseCode.contains("*") && baseCode.contains("#")) {
            String ussdCode = baseCode.replace("الرقم", phone).replace("الرصيد", amount);
            ussdCode = ussdCode.replace("#", Uri.encode("#"));

            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode));
            intent.putExtra("com.android.phone.force.slot", true);
            intent.putExtra("com.android.phone.extra.slot", simSlot - 1);
            intent.putExtra("slot", simSlot - 1);

            startActivity(intent);
        }
    }
}