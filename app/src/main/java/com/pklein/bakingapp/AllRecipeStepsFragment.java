package com.pklein.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pklein.bakingapp.data.recipe;

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
    private Parcelable mSavedRecyclerViewState;
    private static final String RECYCLER_STATE = "recycler";

    public AllRecipeStepsFragment(){}

    // interface, calls a method in AllRecipeStepsActivity named onStepSelected
    public interface OnStepClickListener {
        void onStepSelected(int StepPos);
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

        mLayoutManager = new GridLayoutManager(getContext(), 1);

         return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();

        // if a scroll position is saved, read it.
        if(mSavedRecyclerViewState!=null) {
            mLayoutManager.onRestoreInstanceState(mSavedRecyclerViewState);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);

        mStepsListAdapter = new StepsListAdapter(new StepsListAdapter.CustomStepClickListener() {
            @Override
            public void onItemClick(View v, int StepPos) {
                Log.d(TAG, "clicked position:" + StepPos);
                mCallback.onStepSelected(StepPos);
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

    }

    // with the help of https://inthecheesefactory.com/blog/fragment-state-saving-best-practices/en
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // recyclerview position :
        outState.putParcelable(RECYCLER_STATE,mLayoutManager.onSaveInstanceState());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)  {
        super.onActivityCreated(savedInstanceState);
        //with the help of https://stackoverflow.com/questions/27816217/how-to-save-recyclerviews-scroll-position-using-recyclerview-state
        if (savedInstanceState != null) {
            mSavedRecyclerViewState = savedInstanceState.getParcelable(RECYCLER_STATE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSavedRecyclerViewState != null) {
            mLayoutManager.onRestoreInstanceState(mSavedRecyclerViewState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSavedRecyclerViewState = mLayoutManager.onSaveInstanceState();
    }
}
