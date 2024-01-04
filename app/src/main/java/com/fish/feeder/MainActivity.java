package com.fish.feeder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.fish.feeder.adapter.PageAdapter;
import com.fish.feeder.databinding.ActivityMainBinding;
import com.fish.feeder.fragment.HistoryFragment;
import com.fish.feeder.fragment.HomeFragment;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<Fragment> fragments;
    private HomeFragment homeFragment;
    private HistoryFragment historyFragment;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar2);

        fragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        historyFragment = new HistoryFragment();
        fragments.add(homeFragment);
        fragments.add(historyFragment);

        binding.viewPager.setAdapter(new PageAdapter(this, fragments));
        binding.viewPager.setOffscreenPageLimit(2);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if(position == 0)
                    binding.bottomNav.setSelectedItemId(R.id.home);
                else
                    binding.bottomNav.setSelectedItemId(R.id.settings);
            }
        });

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.home)
                    binding.viewPager.setCurrentItem(0);
                else
                    binding.viewPager.setCurrentItem(1);
                return true;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if(grantResults[0] == PackageManager.PERMISSION_DENIED) {

                Toast.makeText(getApplicationContext(), "Please allow the permission to configure WiFi!", Toast.LENGTH_LONG).show();

            }

        }

    }
}