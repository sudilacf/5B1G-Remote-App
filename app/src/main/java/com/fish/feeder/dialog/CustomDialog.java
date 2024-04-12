package com.fish.feeder.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;

import com.fish.feeder.databinding.DialogCustomBinding;

public class CustomDialog extends Dialog {

    private Context context;
    private String title;
    private String message;
    private String cancelButton;
    private String confirmButton;
    private OnClickListener cancelListener;
    private OnClickListener confirmListener;

    private DialogCustomBinding binding;

    private CustomDialog(Context context, Builder builder) {

        super(context);
        this.context = context;
        this.title = builder.title;
        this.message = builder.message;
        this.cancelButton = builder.cancelButton;
        this.confirmButton = builder.confirmButton;
        this.cancelListener = builder.cancelListener;
        this.confirmListener = builder.confirmListener;

        binding = DialogCustomBinding.inflate(LayoutInflater.from(context));
        binding.title.setText(title == null ? "" : title);
        binding.subtitle.setText(message == null ? "" : message);
        binding.cancelButton.setText(cancelButton);
        binding.confirmButton.setText(confirmButton);
        binding.cancelButton.setOnClickListener(v->{
            this.dismiss();
            if(cancelListener != null)
                cancelListener.onClick();
        });
        binding.confirmButton.setOnClickListener(v->{
            this.dismiss();
            if(confirmListener != null)
                confirmListener.onClick();
        });

        this.setContentView(binding.getRoot());
        this.setCancelable(builder.cancelable);
        this.getWindow().setBackgroundDrawable(new GradientDrawable() { public GradientDrawable gd() { this.setColor(Color.TRANSPARENT); return this; } }.gd());

    }

    public CustomDialog(Context context){

        super(context);
        this.setContentView(binding.getRoot());
        binding = DialogCustomBinding.inflate(LayoutInflater.from(context));
        binding.title.setText(title == null ? "" : title);
        binding.subtitle.setText(message == null ? "" : message);
        binding.cancelButton.setText(cancelButton);
        binding.confirmButton.setText(confirmButton);
        binding.cancelButton.setOnClickListener(v->{
            this.dismiss();
            if(cancelListener != null)
                cancelListener.onClick();
        });
        binding.confirmButton.setOnClickListener(v->{
            this.dismiss();
            if(confirmListener != null)
                confirmListener.onClick();
        });
        this.getWindow().setBackgroundDrawable(new GradientDrawable() { public GradientDrawable gd() { this.setColor(Color.TRANSPARENT); return this; } }.gd());

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCancelButton(String cancelButton, OnClickListener listener) {
        this.cancelButton = cancelButton;
        this.cancelListener = listener;
    }

    public void setConfirmButton(String confirmButton, OnClickListener listener) {
        this.confirmButton = confirmButton;
        this.confirmListener = listener;
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
            return new CustomDialog(this.context, this);
        }

    }

    public interface OnClickListener {
        public void onClick();
    }

}
