package com.example.matija_pc.carewell.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.matija_pc.carewell.fragments.CallsFragment;
import com.example.matija_pc.carewell.fragments.ContactsFragment;
import com.example.matija_pc.carewell.fragments.ConversationsFragment;

/**
 * Created by Matija-PC on 15.4.2015..
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new ContactsFragment();

            case 1:
                return new CallsFragment();

            case 2:
                return new ConversationsFragment();

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
