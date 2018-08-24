package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment; // encouraged
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import static android.widget.CompoundButton.*;

/**
 * Created by djn on 18-8-13.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;

    private Crime mCrime;

    private EditText mTitleText;
    private Button mCrimeDate;
    private CheckBox mSolved;
    private Button mReportButton;
    private Button mSuspectButton;

    public static CrimeFragment newInstance(UUID crime_id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crime_id);

        CrimeFragment crimeFrag = new CrimeFragment();
        crimeFrag.setArguments(args);

        return crimeFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Taking the crime item needed out of the CrimeLab
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }
    // Wiring up views and  have the fragment instantiate its user interface view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        //Input crime title
        mTitleText = (EditText) v.findViewById(R.id.crime_title);
        mTitleText.setText(mCrime.getTitle());
        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCrimeDate = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mCrimeDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);

            }
        });

        mSolved = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolved.setChecked(mCrime.isSolved());
        mSolved.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCrime.isSolved())
                    mCrime.setSolved(false);
                else
                    mCrime.setSolved(true);
            }
        });

        //mReportButton is for generating an intent to send the crime report
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);//set intent action
                i.setType("text/plain");//set MIME type
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());//fill in text to be sent
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));// fill in a subject
                i = Intent.createChooser(i, getString(R.string.send_report));// forces a chooser
                startActivity(i);
            }
        });

        //mSuspectButton is for choosing a person as the suspect of the crime
        final Intent PickContact = new Intent(Intent.ACTION_PICK,
                            ContactsContract.Contacts.CONTENT_URI);
        //The suspect may be picked more than once, so PickContact is placed outside the listener.

        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(PickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        return v;
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        if (resultcode != Activity.RESULT_OK)
            return;

        if (requestcode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
    }

    private void updateDate() {
        mCrimeDate.setText(mCrime.getDate().toString());
    }

    /*
    Generates a crime report containing the information of whether it has been solved,
    its date, suspect and crime title.
     */
    private String getCrimeReport() {
        //Check if the crime is solved
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        //Determine the date
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        //Utility class for producing strings with formatted date/time.

        //Check if there's a suspect, if there is, add it.
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        //fill all information needed in the report
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
