package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by djn on 18-8-14.
 */

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab mCrimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = mCrimeLab.getCrimes();

        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.notifyDataSetChanged();
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mCrimeTitleText;
        private TextView mCrimeDateText;
        private ImageView mCrimeSolvedImage;

        private Crime mCrime;


        public void Bind(Crime crime) {
            mCrime = crime;

            mCrimeTitleText.setText(mCrime.getTitle());
            mCrimeDateText.setText(mCrime.getDate().toString());
            mCrimeSolvedImage.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.GONE);
        }

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));

            mCrimeTitleText = (TextView) itemView.findViewById(R.id.crime_title);
            mCrimeDateText = (TextView) itemView.findViewById(R.id.crime_date);
            mCrimeSolvedImage = (ImageView) itemView.findViewById(R.id.crime_solved);

            itemView.setOnClickListener(this);
        } // define CrimeHolder's own constructor

        @Override
        public void onClick(View view) {
            Intent intent = new CrimePagerActivity().newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            return new CrimeHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            holder.Bind(mCrimes.get(position));
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}