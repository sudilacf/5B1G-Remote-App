package com.fish.feeder.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.fish.feeder.databinding.DialogCustomBinding;

public class CustomDialog {

    private final Context context;
    private final String title;
    private final String message;
    private final String cancelButton;
    private final String confirmButton;
    private final boolean cancelable;
    private final OnClickListener cancelListener;
    private final OnClickListener confirmListener;

    private DialogCustomBinding binding;
    private AlertDialog dialog;

    private CustomDialog(Builder builder) {
        this.context = builder.context;
        this.title = builder.title;
        this.message = builder.message;
        this.cancelButton = builder.cancelButton;
        this.confirmButton = builder.confirmButton;
        this.cancelable = builder.cancelable;
        this.cancelListener = builder.cancelListener;
        this.confirmListener = builder.confirmListener;
        binding = DialogCustomBinding.inflate(LayoutInflater.from(context));
        binding.title.setText(title == null ? "" : title);
        binding.subtitle.setText(message == null ? "" : message);
        binding.cancelButton.setText(cancelButton);
        binding.confirmButton.setText(confirmButton);
        binding.cancelButton.setOnClickListener(v->{
            dialog.dismiss();
            if(cancelListener != null)
                cancelListener.onClick();
        });
        binding.confirmButton.setOnClickListener(v->{
            dialog.dismiss();
            if(confirmListener != null)
                confirmListener.onClick();
        });

        dialog = new AlertDialog.Builder(context)
                .setView(binding.getRoot())
                .setCancelable(cancelable)
                .create();

    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public static class Builder {

        private Context context;
        private String title;
        private String message;
        private String cancelButton;
        private String confirmButton;
        private OnClickListener cancelListener;
        private OnClickListener confirmListener;
        private boolean cancelable = true;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setCancelButton(String cancelButton, OnClickListener listener) {
            this.cancelButton = cancelButton;
            this.cancelListener = listener;
            return this;
        }

        public Builder setConfirmButton(String confirmButton, OnClickListener listener) {
            this.confirmButton = confirmButton;
            this.confirmListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public CustomDialog build() {
            return new CustomDialog(this);
        }

    }

    public interface OnClickListener {
        public void onClick();
    }

}
