package com.example.inventory;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Map;

// TODO: 2018-07-08 add "tags" fields to the database
public class MainScreenActivity extends AppCompatActivity{


    TabLayout tabLayout;
    ViewPager2 viewPager;

    Map<Integer, String> tabNames = Map.ofEntries(
            Map.entry(0, "Товари"),
            Map.entry(1, "Полички"),
            Map.entry(2, "Недостача")
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabNames.get(position))).attach();

    }
    private ViewPagerAdapter createCardAdapter() {
        return new ViewPagerAdapter(this);
    }
}

