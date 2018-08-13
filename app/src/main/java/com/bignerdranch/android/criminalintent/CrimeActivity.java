package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CrimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);

        FragmentManager fm = getSupportFragmentManager(); // inherited by AppCompatActivity
        Fragment frag = fm.findFragmentById(R.id.fragment_container);
        //null returned if not in the list,
        // otherwise the fragment corresponding to the id// is returned

        //if the fragment is not in the list, i.d. , not created, then add one to the list of FragmentManager
        if (frag == null) {
            frag = new CrimeFragment();
            fm.beginTransaction().add(R.id.fragment_container, frag).commit();
        }
    }
}
