package com.fish.feeder.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;

import com.fish.feeder.databinding.DialogProgressCustomBinding;

public class CustomProgressDialog extends Dialog {

    private Context context;
    private String message;

    private final DialogProgressCustomBinding binding;

    public CustomProgressDialog(Context context) {

        super(context);
        binding = DialogProgressCustomBinding.inflate(LayoutInflater.from(context));
        this.setContentView(binding.getRoot());
        this.getWindow().setBackgroundDrawable(new GradientDrawable() { public GradientDrawable gd() { this.setColor(Color.TRANSPARENT); return this; } }.gd());
        binding.message.setText(this.message);

    }

    private CustomProgressDialog(Context context, Builder builder) {

        super(context);
        this.context = context;
        this.message = builder.message;
        binding = DialogProgressCustomBinding.inflate(LayoutInflater.from(context));
        this.setContentView(binding.getRoot());
        this.getWindow().setBackgroundDrawable(new GradientDrawable() { public GradientDrawable gd() { this.setColor(Color.TRANSPARENT); return this; } }.gd());
        this.setCancelable(builder.cancelable);
        binding.message.setText(this.message);

    }

    public static class Builder {

        private final Context context;
        private String message;
        private boolean cancelable = true;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public CustomProgressDialog build() {
            return new CustomProgressDialog(context, this);
        }

    }

}
