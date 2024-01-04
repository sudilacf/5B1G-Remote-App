package com.fish.feeder.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.fish.feeder.R;
import com.fish.feeder.databinding.FragmentHomeBinding;
import com.fish.feeder.dialog.CustomDialog;
import com.fish.feeder.dialog.CustomProgressDialog;
import com.fish.feeder.model.History;
import com.fish.feeder.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Calendar;

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

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

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

        binding.scheduleSettings.setOnClickListener(v->{

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day =  calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.DialogPickerTheme, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int _year, int _month, int _day) {

                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.DialogPickerTheme, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int _hour, int _minute) {

                            boolean isPM = _hour > 12;
                            String meridian = isPM ? "PM" : "AM";
                            int hour12Format = isPM ? _hour - 12 : _hour;
                            String newSchedule = (_month + 1) + "/" + _day + "/" + _year + " " + hour12Format + ":" + _minute + ":00" + " " + meridian;

                            CustomProgressDialog progressDialog1 = new CustomProgressDialog.Builder(getContext())
                                    .setCancelable(false)
                                    .setMessage("Setting schedule time...")
                                    .build();
                            progressDialog1.show();

                            feedReference.child("on").setValue(newSchedule).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    progressDialog1.dismiss();

                                    if(task.isSuccessful()) {

                                        Toast.makeText(getContext(), "Schedule set successfully!", Toast.LENGTH_LONG).show();

                                    } else {

                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                        }
                    }, hour, minute, false);
                    timePickerDialog.show();

                }
            }, year, month, day);
            datePickerDialog.show();

        });

        binding.wifiSettings.setOnClickListener(v->{

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_PERMISSION_REQUEST_CODE);

                } else {

                    if(Util.isLocationEnabled(getContext())) {

                        if(Util.isConnectedToMachine(getContext())) {

                            Toast.makeText(getContext(), "Connected", Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(getContext(), "Please connect to \"5B1G\" WiFi to configure!", Toast.LENGTH_LONG).show();

                        }

                    } else {

                        new CustomDialog.Builder(getContext())
                                .setTitle("Warning!")
                                .setMessage("Please enable location to continue!")
                                .setCancelButton("Cancel", null)
                                .setConfirmButton("Cofirm", null)
                                .build()
                                .show();

                    }

                }
            }

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

                Toast.makeText(getContext(), "Fed successfully!", Toast.LENGTH_LONG).show();
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
