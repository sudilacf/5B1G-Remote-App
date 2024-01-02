package com.fish.feeer.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fish.feeer.databinding.ItemHistoryBinding;
import com.fish.feeer.model.History;
import com.fish.feeer.util.Util;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> histories;
    private Handler handler;

    public HistoryAdapter(List<History> histories) {
        this.histories = histories;
        handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                //notifyDataSetChanged();
                handler.postDelayed(this, 1000);
            }
        });

    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {

        ItemHistoryBinding binding = holder.binding;
        History history = histories.get(position);
        binding.historyTxt.setText(Util.getLastFedTime(history.getEpoch_time()));

    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemHistoryBinding binding;

        public ViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}
