package com.example.btcontroller.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.btcontroller.buetooth.BluetoothStreamsInterface;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {
    private BluetoothStreamsInterface BSI;

    public SectionsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, BluetoothStreamsInterface BSI) {
        super(fragmentManager, lifecycle);
        this.BSI = BSI;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = ClientFragment.newInstance(BSI);
                break;
            default:
                fragment = GraphFragment.newInstance(BSI);
                break;
        }
        return fragment;
    }



    @Override
    public int getItemCount() {
        return 2;
    }
}