package com.example.inventory;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import kotlin.NotImplementedError;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 2;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull @Override public Fragment createFragment(int position) {
        switch (position) {
            case (0):
                return new ShelvesCatalogueFragment();
            case (1):
                return new ItemsCatalogueFragment();
            default:
                throw new NotImplementedError();
        }

    }
    @Override public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}
