package com.fish.feeder.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fish.feeder.R;
import com.fish.feeder.databinding.FragmentHomeBinding;
import com.fish.feeder.dialog.CustomDialog;
import com.fish.feeder.dialog.CustomProgressDialog;
import com.fish.feeder.dialog.SettingsDialog;
import com.fish.feeder.dialog.WiFiPickerDialog;
import com.fish.feeder.model.Data;
import com.fish.feeder.model.History;
import com.fish.feeder.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private FirebaseDatabase database;
    private DatabaseReference historyReference;
    private DatabaseReference dataReference;

    private Handler handler;
    private CustomProgressDialog progressDialog;
    private long lastEpochTime;
    private String nextFeedValue;
    private String pushKeyValue;
    private int foodQuantityValue;
    private int frequencyValue;
    private boolean needToUpdateFrequency = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        historyReference = database.getReference("history");
        dataReference = database.getReference("data");
        handler = new Handler(Looper.getMainLooper());
        progressDialog = new CustomProgressDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("Please wait!...")
                .build();

        historyReference.limitToLast(1).addValueEventListener(historyEventListener);
        dataReference.addValueEventListener(dataEventListener);
        dataReference.child("offset").addListenerForSingleValueEvent(frequencyEventListener);
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

                            dataReference.child("now")
                                    .setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {

                                                progressDialog.show();

                                            }

                                        }
                                    });

//                            feedReference.child("now")
//                                    .runTransaction(new Transaction.Handler() {
//                                        @NonNull
//                                        @Override
//                                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
//
//                                            Long currentValue = currentData.getValue(Long.class);
//                                            if(currentValue == null) {
//                                                currentValue = 0L;
//                                            }
//                                            currentData.setValue(currentValue + 1);
//
//                                            return Transaction.success(currentData);
//
//                                        }
//
//                                        @Override
//                                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
//
//
//
//                                        }
//                                    });

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

                            dataReference.child("next_feed").setValue(newSchedule).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                    /*if(Util.isLocationEnabled(getContext())) {

                        if(Util.isConnectedToMachine(getContext())) {*/

                            new WiFiPickerDialog.Builder(getContext())
                                    .setTitle("Select Network")
                                    .setCancelable(false)
                                    .onConfirmLister(new WiFiPickerDialog.OnConfirmListener() {
                                        @Override
                                        public void onConfirmed(String SSID, String password) {

                                            JSONObject body = new JSONObject();
                                            RequestQueue queue = Volley.newRequestQueue(getContext());

                                            try {
                                                body.put("ssid", "Isildur");
                                                body.put("password", "password");
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }

                                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                                    "http://192.168.1.1",
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {

                                                            try {
                                                                Toast.makeText(getContext(), new JSONObject(response).getString("message"), Toast.LENGTH_LONG).show();
                                                            } catch (JSONException e) {
                                                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {

                                                            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                                                            error.printStackTrace();

                                                        }
                                                    }) {

                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError {

                                                    Map<String, String> params = new HashMap<>();
                                                    params.put("ssid", SSID);
                                                    params.put("password", password);

                                                    return params;

                                                }
                                            };

                                            queue.add(stringRequest);

                                        }
                                    })
                                    .build()
                                    .show();

                        /*} else {

                            Toast.makeText(getContext(), "Please connect to \"5B1G\" WiFi to configure!", Toast.LENGTH_LONG).show();
                        }

                    } else {

                        new CustomDialog.Builder(getContext())
                                    .setTitle("Warning!")
                                    .setMessage("Due to Android restrictions, location need to be enabled to access WiFi state")
                                    .setCancelButton("Cancel", null)
                                    .setConfirmButton("Confirm", null)
                                    .build()
                                    .show();

                    }*/

                }
            }

        });
        
        binding.feedSettings.setOnClickListener(v->{

            SettingsDialog settingsDialog = new SettingsDialog.Builder(getContext())
                    .setTitle("Feed Settings")
                    .setFoodQuantity(foodQuantityValue)
                    .setFrequency(frequencyValue)
                    .create();
            settingsDialog.setOnSaveListener(new SettingsDialog.OnSaveListener() {
                @Override
                public void onSaved(int quantity, int frequency) {

                    progressDialog.show();
                    settingsDialog.dismiss();
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("food_quantity", quantity);
                    updates.put("offset", frequency);
                    dataReference.child("food_quantity").setValue(quantity)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {

                                        dataReference.child("offset").setValue(frequency)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {

                                                            Toast.makeText(getContext(), "Settings saved!", Toast.LENGTH_SHORT).show();

                                                            progressDialog.dismiss();
                                                            needToUpdateFrequency = frequencyValue != frequency;

                                                            if(needToUpdateFrequency) {

                                                                frequencyValue = frequency;

                                                                CustomDialog dialog = new CustomDialog.Builder(getContext())
                                                                        .setTitle("Confirmation")
                                                                        .setMessage("Feed frequency was changed. Would you like to update next feed schedule?")
                                                                        .setCancelButton("No", null)
                                                                        .setCancelable(false)
                                                                        .build();

                                                                dialog.setConfirmButton("Yes", new CustomDialog.OnClickListener() {
                                                                    @Override
                                                                    public void onClick() {

                                                                        progressDialog.show();
                                                                        dialog.dismiss();
                                                                        try {
                                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                                                                dataReference.child("next_feed").setValue(Util.getFormattedTime(nextFeedValue, frequency))
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                progressDialog.dismiss();
                                                                                                if(task.isSuccessful()) {
                                                                                                    Toast.makeText(getContext(), "Feed schedule updated successfully!", Toast.LENGTH_LONG).show();
                                                                                                } else {
                                                                                                    task.getException().printStackTrace();
                                                                                                }

                                                                                            }
                                                                                        });

                                                                            }
                                                                        } catch (ParseException e) {
                                                                            throw new RuntimeException(e);
                                                                        }

                                                                    }
                                                                });

                                                                dialog.show();

                                                            }

                                                        } else {
                                                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                            task.getException().printStackTrace();
                                                        }
                                                    }
                                                });

                                    } else {
                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        task.getException().printStackTrace();
                                    }

                                }
                            });

                }
            });

            settingsDialog.show();

        });

        return binding.getRoot();

    }

    private final ValueEventListener dataEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            nextFeedValue = snapshot.child("next_feed").getValue(String.class);
            pushKeyValue = snapshot.child("push_key").getValue(String.class);
            foodQuantityValue = snapshot.child("food_quantity").getValue(Integer.class);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final ValueEventListener frequencyEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            frequencyValue = snapshot.getValue(Integer.class);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

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

    private final Runnable runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            binding.lastFed.setText(Util.getLastFedTime(lastEpochTime));
            try {
                binding.nextFeed.setText((Util.getFeedTime(nextFeedValue)));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            handler.postDelayed(this, 1000);
        }
    };

}
