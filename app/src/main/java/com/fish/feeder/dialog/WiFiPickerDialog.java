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
    private boolean isCancelable;
    private ButtonClickListener closeButtonListener;
    private ButtonClickListener scanButtonListener;
    private ButtonClickListener confirmButtonListener;

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
            if(closeButtonListener != null){
                closeButtonListener.onClick();
            }
            this.dismiss();
        });

        binding.scanBtn.setOnClickListener(v->{
            if(scanButtonListener != null) {
                scanButtonListener.onClick();
                wifiManager.startScan();
            }
        });

        binding.confirmBtn.setOnClickListener(v->{
            if(confirmButtonListener != null) {
                confirmButtonListener.onClick();
            }
            this.dismiss();
        });

    }

    private WiFiPickerDialog(Context context, Builder builder) {

        super(context);
        this.context = context;
        this.title = builder.title;
        this.isCancelable = builder.isCancelable;
        this.closeButtonListener = builder.closeButtonListener;
        this.scanButtonListener = builder.scanButtonListener;
        this.confirmButtonListener = builder.confirmButtonListener;
        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiNetworks = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, wifiNetworks);
        context.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        binding = DialogWifiPickerBinding.inflate(LayoutInflater.from(context));
        this.setContentView(binding.getRoot());
        this.getWindow().setBackgroundDrawable(new GradientDrawable() { public GradientDrawable gd() { this.setColor(Color.TRANSPARENT); return this; } }.gd());

        binding.title.setText(title);
        binding.closeButton.setOnClickListener(v->{
            if(closeButtonListener != null){
                closeButtonListener.onClick();
            }
            this.dismiss();
        });

        binding.scanBtn.setOnClickListener(v->{
            if(scanButtonListener != null) {
                scanButtonListener.onClick();
                wifiManager.startScan();
            }
        });

        binding.confirmBtn.setOnClickListener(v->{
            if(confirmButtonListener != null) {
                confirmButtonListener.onClick();
            }
            this.dismiss();
        });

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    public void setCloseButtonListener(ButtonClickListener closeButtonListener) {
        this.closeButtonListener = closeButtonListener;
    }

    public void setScanButtonListener(ButtonClickListener scanButtonListener) {
        this.scanButtonListener = scanButtonListener;
    }

    public void setConfirmButtonListener(ButtonClickListener confirmButtonListener) {
        this.confirmButtonListener = confirmButtonListener;
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
        private ButtonClickListener closeButtonListener;
        private ButtonClickListener scanButtonListener;
        private ButtonClickListener confirmButtonListener;

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

        public Builder setOnCloseButtonClick(ButtonClickListener closeButtonListener) {
            this.closeButtonListener = closeButtonListener;
            return this;
        }

        public Builder setOnScanButtonClick(ButtonClickListener scanButtonListener) {
            this.scanButtonListener = scanButtonListener;
            return this;
        }

        public Builder setOnConfirmButtonClick(ButtonClickListener confirmButtonListener) {
            this.confirmButtonListener = confirmButtonListener;
            return this;
        }

        public WiFiPickerDialog build() {

            return new WiFiPickerDialog(this.context, this);

        }

    }

    public interface ButtonClickListener {

        void onClick();

    }

}
