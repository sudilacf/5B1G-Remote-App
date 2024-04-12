package com.fish.feeder.dialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fish.feeder.databinding.DialogWifiPickerBinding;

import java.util.ArrayList;
import java.util.List;

public class WiFiPickerDialog extends Dialog {

    private Context context;
    private String title;
    private OnConfirmListener onConfirmListener;

    private final DialogWifiPickerBinding binding;
    private final WifiManager wifiManager;
    private final List<String> wifiNetworks;
    private final ArrayAdapter<String> spinnerAdapter;

    public WiFiPickerDialog(Context context) {

        super(context);
        this.context = context;

        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiNetworks = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, wifiNetworks);
        context.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        binding = DialogWifiPickerBinding.inflate(LayoutInflater.from(context));
        this.setContentView(binding.getRoot());
        this.getWindow().setBackgroundDrawable(new GradientDrawable() { public GradientDrawable gd() { this.setColor(Color.TRANSPARENT); return this; } }.gd());

        binding.title.setText(title);
        binding.closeButton.setOnClickListener(v->{
            this.dismiss();
        });

        binding.scanBtn.setOnClickListener(v->{
            wifiManager.startScan();
            Toast.makeText(getContext(), "Scanning available networks...", Toast.LENGTH_LONG).show();
        });

        binding.confirmBtn.setOnClickListener(v->{
            if(onConfirmListener != null) {
                if(wifiNetworks.isEmpty()) {
                    Toast.makeText(getContext(), "Please select your network!", Toast.LENGTH_LONG).show();
                    onConfirmListener.onConfirmed(binding.wifiSSID.getSelectedItem().toString(), binding.wifiPassword.getText().toString());
                }
            }
            this.dismiss();
        });

    }

    private WiFiPickerDialog(Context context, Builder builder) {

        super(context);
        this.context = context;
        this.title = builder.title;
        this.onConfirmListener = builder.onConfirmListener;
        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiNetworks = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, wifiNetworks);
        context.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        binding = DialogWifiPickerBinding.inflate(LayoutInflater.from(context));
        this.setContentView(binding.getRoot());
        this.getWindow().setBackgroundDrawable(new GradientDrawable() { public GradientDrawable gd() { this.setColor(Color.TRANSPARENT); return this; } }.gd());
        this.setCancelable(builder.isCancelable);

        binding.title.setText(title);
        binding.closeButton.setOnClickListener(v->{
            this.dismiss();
        });

        binding.scanBtn.setOnClickListener(v->{
            wifiManager.startScan();
            Toast.makeText(getContext(), "Scanning available networks...", Toast.LENGTH_LONG).show();
        });

        binding.confirmBtn.setOnClickListener(v->{
            if(onConfirmListener != null) {
                if(wifiNetworks.isEmpty()) {
                    Toast.makeText(getContext(), "Please select your network!", Toast.LENGTH_LONG).show();
                } else {
                    this.dismiss();
                    onConfirmListener.onConfirmed(binding.wifiSSID.getSelectedItem().toString(), binding.wifiPassword.getText().toString());
                }
            }
        });

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WiFiPickerDialog onConfirmLister(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
        return this;
    }

    private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                binding.wifiSSID.setAdapter(spinnerAdapter);
                wifiNetworks.clear();
                List<ScanResult> results = wifiManager.getScanResults();

                for(ScanResult result : results) {

                    if (result.frequency >= 2412 && result.frequency <= 2484) {

                       wifiNetworks.add(result.SSID);

                    }

                }

                spinnerAdapter.notifyDataSetChanged();

            }

        }
    };

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
        context.unregisterReceiver(wifiScanReceiver);
    }

    public static class Builder {

        private Context context;
        private String title;
        private boolean isCancelable = true;
        private OnConfirmListener onConfirmListener;

        public Builder(Context context) {

            this.context = context;

        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            isCancelable = cancelable;
            return this;
        }

        public Builder onConfirmLister(OnConfirmListener onConfirmListener) {
            this.onConfirmListener = onConfirmListener;
            return this;
        }

        public WiFiPickerDialog build() {

            return new WiFiPickerDialog(this.context, this);

        }

    }

    public interface OnConfirmListener {

        void onConfirmed(String SSID, String password);

    }

}
