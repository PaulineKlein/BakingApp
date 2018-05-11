package com.pklein.bakingapp;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pklein.bakingapp.data.step;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class StepsListAdapter  extends RecyclerView.Adapter<StepsListAdapter.StepsListAdapterViewHolder> {

    private static final String TAG = StepsListAdapter.class.getSimpleName();
    private List<step> mStepsData;
    CustomStepClickListener listener;

    //To create the Listener : with the Help of this documentation  :https://gist.github.com/riyazMuhammad/1c7b1f9fa3065aa5a46f
    public interface CustomStepClickListener {
        public void onItemClick(View v, step StepSelected);
    }

    public StepsListAdapter() {}

    public StepsListAdapter(CustomStepClickListener listener) {
        this.listener = listener;
    }

    public class StepsListAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_step_name) TextView stepNameTv;

        public StepsListAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public StepsListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i(TAG, "Start StepsListAdapterViewHolder");
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.step_card;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        final StepsListAdapterViewHolder mViewHolder = new StepsListAdapterViewHolder(view);

        return new StepsListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepsListAdapterViewHolder StepsListAdapterViewHolder, final int position) {

        final Context context = StepsListAdapterViewHolder.itemView.getContext();
        final step StepSelected = mStepsData.get(position);

        StepsListAdapterViewHolder.stepNameTv.setText(StepSelected.getmShortDescription());
       // StepsListAdapterViewHolder.stepNameTv.setPaintFlags(StepsListAdapterViewHolder.stepNameTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        StepsListAdapterViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               listener.onItemClick(v, StepSelected);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mStepsData) return 0;
        return mStepsData.size();
    }

    public void setStepsData(List<step> stepData) {
        mStepsData = stepData;
        notifyDataSetChanged();
    }
}