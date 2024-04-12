package com.fish.feeder.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.fish.feeder.databinding.DialogSettingsBinding;

public class SettingsDialog extends Dialog {

    private Context context;
    private final DialogSettingsBinding binding;
    private String title;
    private OnCancelListener onCancelListener;
    private OnSaveListener onSaveListener;

    public SettingsDialog(@NonNull Context context) {
        super(context);

        this.context = context;
        binding = DialogSettingsBinding.inflate(LayoutInflater.from(context));
        this.setContentView(binding.getRoot());
        this.getWindow().setBackgroundDrawable(new GradientDrawable() { public GradientDrawable gd() { this.setColor(Color.TRANSPARENT); return this; } }.gd());
        binding.title.setText(title);
        binding.cancelBtn.setOnClickListener(v->{
            this.dismiss();
            onCancelListener.onCancel();
        });
        binding.saveBtn.setOnClickListener(v->{
            int quantity = Integer.valueOf(binding.foodQuantity.getText().toString());
            int frequency = Integer.valueOf(binding.foodFrequency.getText().toString());
            onSaveListener.onSaved(quantity, frequency);
        });
    }

    private SettingsDialog(Context context, Builder builder){

        super(context);

        this.context = builder.context;
        this.title = builder.title;
        this.onCancelListener = builder.onCancelListener;
        this.onSaveListener = builder.onSaveListener;
        binding = DialogSettingsBinding.inflate(LayoutInflater.from(context));
        this.setContentView(binding.getRoot());
        this.getWindow().setBackgroundDrawable(new GradientDrawable() { public GradientDrawable gd() { this.setColor(Color.TRANSPARENT); return this; } }.gd());
        binding.title.setText(title);
        binding.cancelBtn.setOnClickListener(v->{
            this.dismiss();
            onCancelListener.onCancel();
        });
        binding.saveBtn.setOnClickListener(v->{
            int quantity = Integer.valueOf(binding.foodQuantity.getText().toString());
            int frequency = Integer.valueOf(binding.foodFrequency.getText().toString());
            onSaveListener.onSaved(quantity, frequency);
        });

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnSaveListener(OnSaveListener onSaveListener) {
        this.onSaveListener = onSaveListener;
    }

    public static class Builder {

        private Context context;
        private String title;
        private OnCancelListener onCancelListener;
        private OnSaveListener onSaveListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.onCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnSaveListener(OnSaveListener onSaveListener) {
            this.onSaveListener = onSaveListener;
            return this;
        }

        public SettingsDialog create() {
            return new SettingsDialog(context, this);
        }

    }

    public interface OnCancelListener {
        void onCancel();
    }

    public interface OnSaveListener {
        void onSaved(int quantity, int frequency);
    }

}
