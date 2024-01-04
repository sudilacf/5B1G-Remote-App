package com.fish.feeder.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.fish.feeder.databinding.DialogProgressCustomBinding;

public class CustomProgressDialog {

    private final Context context;
    private final String message;
    private final boolean cancelable;

    private DialogProgressCustomBinding binding;
    private AlertDialog dialog;

    private CustomProgressDialog(Builder builder) {
        this.context = builder.context;
        this.message = builder.message;
        this.cancelable = builder.cancelable;
        binding = DialogProgressCustomBinding.inflate(LayoutInflater.from(context));
        binding.message.setText(this.message);
        dialog = new AlertDialog.Builder(context)
                .setView(binding.getRoot())
                .setCancelable(this.cancelable)
                .create();
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public boolean isShowing() { return dialog.isShowing(); }

    public static class Builder {

        private Context context;
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
            return new CustomProgressDialog(this);
        }

    }

}
