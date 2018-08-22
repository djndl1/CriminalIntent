package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by djn on 18-8-16.
 */

public class CrimePagerActivity extends AppCompatActivity {

    private ViewPager mCrimePager;
    private List<Crime> mCrimes;

    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

        public static Intent newIntent(Context PackageContext, UUID crime_id) {
        Intent intent = new Intent(PackageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crime_id);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mCrimePager = (ViewPager) findViewById(R.id.crime_view_pager);

        mCrimes = CrimeLab.get(this).getCrimes();

        FragmentManager fm = getSupportFragmentManager();

        mCrimePager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        for (int i=0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId))
            {
                mCrimePager.setCurrentItem(i);
                break;
            }
        }

    }
}
