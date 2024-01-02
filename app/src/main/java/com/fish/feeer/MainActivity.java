package com.fish.feeer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.fish.feeer.adapter.PageAdapter;
import com.fish.feeer.databinding.ActivityMainBinding;
import com.fish.feeer.fragment.HistoryFragment;
import com.fish.feeer.fragment.HomeFragment;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<Fragment> fragments;
    private HomeFragment homeFragment;
    private HistoryFragment historyFragment;

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

}