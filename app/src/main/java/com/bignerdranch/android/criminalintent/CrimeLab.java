package com.bignerdranch.android.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by djn on 18-8-14.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab; // unique instance

    private List<Crime> mCrimes;

    // The only instance of the class should be got only through get() method
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);

        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();

        for (int i=1; i<=100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Set Title #" + i);
            crime.setSolved(i % 2 == 0);
            mCrimes.add(crime);
        }
    } // instance constructed only inside the class

    public List<Crime> getCrimes(){
        return mCrimes;
    }

    public Crime getCrime(UUID id){
        for (Crime c : mCrimes) {
            if (c.getId().equals(id))
            {
                return c;
            }
        }
        return null;
    }
}
