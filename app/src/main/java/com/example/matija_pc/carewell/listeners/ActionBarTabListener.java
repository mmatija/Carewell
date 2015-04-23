package com.example.matija_pc.carewell.listeners;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

/**
 * Created by Matija-PC on 13.4.2015..
 */
public class ActionBarTabListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;

    public ActionBarTabListener(Activity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;

    }

    public void onTabSelected (ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        //Check if fragment is already initialized
        //FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
        if (mFragment == null) {
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            fragmentTransaction.replace(android.R.id.content, mFragment, mTag);
            //ft.add(android.R.id.content, mFragment, mTag);
        }
        else {
            fragmentTransaction.attach(mFragment);
            //ft.attach(mFragment);
        }

        //fragmentTransaction.commit();
        //ft.commit();
    }

    public void onTabUnselected (ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        //FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
        if (mFragment != null) {
            fragmentTransaction.detach(mFragment);
            //ft.detach(mFragment);
        }
        //ft.commit();
    }

    public void onTabReselected (ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

}
