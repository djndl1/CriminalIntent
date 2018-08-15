package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalIntent.crime_id";

    public Intent newIntent(Context PackageContext, UUID crime_id) {
        Intent intent = new Intent(PackageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crime_id);

        return intent;
    }
    @Override
    protected Fragment createFragment() {

        UUID crime_id = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        CrimeFragment crifrag = CrimeFragment.newInstance(crime_id);

        return crifrag;

    }
}