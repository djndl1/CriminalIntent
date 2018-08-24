package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeCursorWrapper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by djn on 18-8-14.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab; // unique instance

    private Context mContext;
    private SQLiteDatabase mDatabase;

    // The only instance of the class should be got only through get() method
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);

        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase(); // onCreate() if not created, onUpdate() if already created

    } // instance constructed only inside the class

    /* getting a list of Crimes by querying all rows of CrimeTable
       using a CursorWrapper moving down each row to add it to the list
     */
    public List<Crime> getCrimes(){

        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper curWrap = queryCrimes(null, null); // query all rows

        try {
            curWrap.moveToFirst();
            while (!curWrap.isAfterLast()) {
                // move the cursor down row by row until it iterates all rows
                crimes.add(curWrap.getCrime());
                curWrap.moveToNext();
            }
        } finally {
                curWrap.close(); // cleaning up
            }

        return crimes;
        }

    /* getting a Crime by its UUID using a CursorWrapper obtained by querying */
    public Crime getCrime(UUID id){
        // Query by UUID
        CrimeCursorWrapper curWrap = queryCrimes(CrimeTable.Cols.UUID + " = ?",
                                                new String[] {id.toString()}
                                                );

        try{
            if (curWrap.getCount() == 0) {
                return null;
            }

            curWrap.moveToFirst();
            return curWrap.getCrime();
        } finally {
            curWrap.close();
        }

    }

    /*
        adding a Crime to the database using a ContentValues
     */
    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    // returning a ContentValues to insert into the database a record
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    // updating a row in the database
    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    /*
        Returning a Cursor (CursorWrapper is an implementation of Cursor)
       for query of certain conditions specified by whereClause and whereArgs
    */
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, //columns: null implies all columns
                whereClause,
                whereArgs,
                null, //groupBy
                null, //having
                null //orderBy
        );
        return new CrimeCursorWrapper(cursor);
    }

}
