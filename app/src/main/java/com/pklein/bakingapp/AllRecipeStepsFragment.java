package com.pklein.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.pklein.bakingapp.data.ingredient;
import com.pklein.bakingapp.data.recipe;
import com.pklein.bakingapp.data.step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllRecipeStepsFragment extends Fragment {

    private static final String TAG= AllRecipeStepsFragment.class.getSimpleName();
    OnStepClickListener mCallback;  // Define a new interface : triggers a callback in AllRecipeStepsActivity

    @BindView(R.id.recyclerview_steps)    RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display_step)    TextView mErrorMessageDisplay;

    private StepsListAdapter mStepsListAdapter;
    private GridLayoutManager mLayoutManager;
    private recipe mrecipe;

    public AllRecipeStepsFragment(){}

    // interface, calls a method in AllRecipeStepsActivity named onStepSelected
    public interface OnStepClickListener {
        void onStepSelected(int StepId);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStepClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.all_steps_fragment, container, false);
        ButterKnife.bind(this, rootView);

        mLayoutManager= new GridLayoutManager(getContext(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);

        mStepsListAdapter = new StepsListAdapter(new StepsListAdapter.CustomStepClickListener() {
            @Override
            public void onItemClick(View v, int StepId) {
                Log.d(TAG, "clicked position:" + StepId);
                mCallback.onStepSelected(StepId);
            }
        });
        mRecyclerView.setAdapter(mStepsListAdapter);

        Intent intentThatStarted = getActivity().getIntent();
        String title = getResources().getString(R.string.title_AllRecipeStepsActivity);

        if(intentThatStarted.hasExtra("Recipe")) {
            mrecipe = intentThatStarted.getExtras().getParcelable("Recipe");
            title = mrecipe.getmName();
            mStepsListAdapter.setStepsData(mrecipe.getmStep());
        }
        getActivity().setTitle(title);

        return rootView;
    }
}
