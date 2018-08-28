package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;


/**
 * Created by djn on 18-8-14.
 */

public class CrimeListActivity extends SingleFragmentActivity
    implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    /**
     * Use aliases to choose layouts for different devices
      * @return a single-fragment layout for phones, a double-panel layout for tablets
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    /**
     * The is the implementation of CrimeListFragment.Callbacks. It checks whether the hosting
     * activity has the detail_fragment_container, that is, whether the device is a tablet or
     * a phone, to determine whether to start a new CrimePagerActivity to host the CrimeFragment
     * or to put a CrimeFragment in detail_fragment_container.
     * @param crime the Crime object to display and modify in the CrimeFragment
     */
    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction().
                    replace(R.id.detail_fragment_container, newDetail).commit();
        }
    }

    /**
     * The implementation of CrimeFragment.Callbacks. It is designed to update the crime list. It
     * actually calls the updateUI() method of CrimeListFragment to reload all crimes to update the
     * list and the subtitle.
     * @param crime the Crime object to be updated
     */
    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFrag = (CrimeListFragment) getSupportFragmentManager()
                                                    .findFragmentById(R.id.fragment_container);
        listFrag.updateUI(); // update the crime list
    }

}
