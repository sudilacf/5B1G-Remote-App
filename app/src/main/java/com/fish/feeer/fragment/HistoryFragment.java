package com.fish.feeer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fish.feeer.adapter.HistoryAdapter;
import com.fish.feeer.databinding.FragmentHistoryBinding;
import com.fish.feeer.model.History;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;

    List<History> histories;
    private HistoryAdapter adapter;

    private FirebaseDatabase database;
    private DatabaseReference historyReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);

        histories = new ArrayList<>();
        adapter = new HistoryAdapter(histories);
        database = FirebaseDatabase.getInstance();
        historyReference = database.getReference("history");

        binding.historyRecycler.setAdapter(adapter);
        historyReference.addValueEventListener(valueEventListener);

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                binding.refreshLayout.setRefreshing(false);
            }
        });

        return binding.getRoot();

    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            histories.clear();

            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                histories.add(dataSnapshot.getValue(History.class));

            }

            Collections.reverse(histories);
            adapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

}
