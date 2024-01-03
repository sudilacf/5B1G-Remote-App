package com.fish.feeer.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.fish.feeer.databinding.FragmentHomeBinding;
import com.fish.feeer.dialog.CustomDialog;
import com.fish.feeer.dialog.CustomProgressDialog;
import com.fish.feeer.model.History;
import com.fish.feeer.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private FirebaseDatabase database;
    private DatabaseReference historyReference;
    private DatabaseReference feedReference;
    private DatabaseReference settingsReference;

    private Handler handler;
    private CustomProgressDialog progressDialog;
    private long lastEpochTime;
    private String feedOnValue;
    private String pushKeyValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        historyReference = database.getReference("history");
        feedReference = database.getReference("feed");
        settingsReference = database.getReference("settings");
        handler = new Handler(Looper.getMainLooper());
        progressDialog = new CustomProgressDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("Please wait!...")
                .build();

        historyReference.limitToLast(1).addValueEventListener(historyEventListener);
        feedReference.child("on").addValueEventListener(feedEventListener);
        settingsReference.child("push_key").addListenerForSingleValueEvent(pushKeyEventListener);
        handler.post(runnable);

        Util.setGradientBackground(binding.feedNow, "#A8FF78", "#78FFD6", 24, true);
        Util.setGradientBackground(binding.scheduleSettings, "#FDC830", "#f37335", 24, true);
        Util.setGradientBackground(binding.wifiSettings, "#654EA3", "#EAAFC8", 24, true);
        Util.setGradientBackground(binding.feedSettings, "#B58ECC", "#5DE6DE", 24, true);

        binding.feedNow.setOnClickListener(v->{

            CustomDialog dialog = new CustomDialog.Builder(getContext())
                    .setTitle("CONFIRMATION!")
                    .setMessage("Would you like to feed now?")
                    .setCancelButton("No", null)
                    .setConfirmButton("Yes", new CustomDialog.OnClickListener() {
                        @Override
                        public void onClick() {

                            progressDialog.show();

                            feedReference.child("now")
                                    .setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {

                                                progressDialog.show();

                                            }

                                        }
                                    });

                        }
                    })
                    .build();

            dialog.show();

        });

        return binding.getRoot();

    }

    private final ValueEventListener historyEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            DataSnapshot data= snapshot.getChildren().iterator().next();

            History history = data.getValue(History.class);
            lastEpochTime = history.getEpoch_time();

            if(pushKeyValue != null && !pushKeyValue.equals(data.getKey())) {

                progressDialog.dismiss();
                pushKeyValue = data.getKey();

            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final ValueEventListener feedEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            feedOnValue = snapshot.getValue(String.class);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final ValueEventListener pushKeyEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            pushKeyValue = snapshot.getValue(String.class);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private Runnable runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            binding.lastFed.setText(Util.getLastFedTime(lastEpochTime));
            try {
                binding.nextFeed.setText((Util.getFeedTime(feedOnValue)));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            handler.postDelayed(this, 1000);
        }
    };

}
