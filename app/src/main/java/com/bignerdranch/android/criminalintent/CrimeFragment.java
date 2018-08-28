package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment; // encouraged
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
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
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
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
    private static final int REQUEST_PHOTO = 2;

    private Crime mCrime;
    private File mPhotoFile;
    private Callbacks mCallbacks;

    private EditText mTitleText;
    private Button mCrimeDate;
    private CheckBox mSolved;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    public static CrimeFragment newInstance(UUID crime_id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crime_id);

        CrimeFragment crimeFrag = new CrimeFragment();
        crimeFrag.setArguments(args);

        return crimeFrag;
    }

    /**
     * This interface is defined for the hosting activity to implement. It updates the crime list.
     */
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Taking the crime item needed out of the CrimeLab
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
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
                updateCrime(); //whenever the title is changed, the database and the list should be updated.
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
                updateCrime(); //whenever the crime is modified, the list should be updated.
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

        // If there's no Contact app, set mSuspectButton disabled
        PackageManager packMan = getActivity().getPackageManager();
        if (packMan.resolveActivity(PickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
            //No activity match the given intent
        }

        //Set a phototaking button and an intent for photo taking
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Determine if a photo can be taken
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packMan) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.criminalintent.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // passing a Uri pointing to where the file in MediaStore.EXTRA_OUTPUT is saved for
                // a full-resolution output

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                            PackageManager.MATCH_DEFAULT_ONLY);
                // get a List of all activities that can handle the captureImage intent

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                // grant all activities that can handle captureImage a write permission to uri

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        if (resultcode != Activity.RESULT_OK)
            return;

        if (requestcode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();// whenever the date is changed, the database and the list should be updated.
            updateDate();
        } else if (requestcode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData(); //get a URI from the intent
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            // query fields of contactUri specified in queryFileds
            try {
                if (c.getCount() == 0) {
                    return;
                }

                c.moveToNext();
                String suspect = c.getString(0); // return the first column
                mCrime.setSuspect(suspect);
                updateCrime(); // whenever the suspect is changed, the database and the list should be updated
                mSuspectButton.setText(suspect);
                } finally {
                c.close();
            }
        } else if (requestcode == REQUEST_PHOTO) {
           Uri uri = FileProvider.getUriForFile(getActivity(),
                   "com.bignerdranch.android.criminalintent.fileprovider",
                   mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updateCrime();// whenever the photo is changed, the database should be updated.
            updatePhotoView();
        }
    }

    private void updateDate() {
        mCrimeDate.setText(mCrime.getDate().toString());
    }

    /** Generates a crime report containing the information of whether it has been solved, its date, suspect and crime title.
     *
     * @return
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

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    /**
     * update the currently modified crime in the database, and then update the crime list by using
     * a callback to reload the crime list of the recyclerview.
     */
    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }
}
