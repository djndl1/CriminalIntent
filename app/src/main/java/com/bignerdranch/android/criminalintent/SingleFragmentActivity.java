package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by djn on 18-8-14.
 * An abstract class characterized by its property of containing a single fragment
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager(); // inherited by AppCompatActivity
        Fragment frag = fm.findFragmentById(R.id.fragment_container);
        //null returned if not in the list,
        // otherwise the fragment corresponding to the id// is returned

        //if the fragment is not in the list, i.d. , not created, then add one to the list of FragmentManager
        if (frag == null) {
            frag = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, frag).commit();
        }
    }
}