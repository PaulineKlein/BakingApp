package com.pklein.bakingapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import com.pklein.bakingapp.data.recipe;
import com.pklein.bakingapp.data.step;
import com.pklein.bakingapp.tools.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OneRecipeStepActivity extends AppCompatActivity {

    private step mstep;
    private String mRecipeName;
    private static final String TAG = OneRecipeStepActivity.class.getSimpleName();
    // Initialize the player view.
    private SimpleExoPlayer mExoPlayer;
    @BindView(R.id.playerView) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.tv_step_desc)   TextView mtvTextDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_recipe_step);
        ButterKnife.bind(this);

        Intent intentThatStarted = this.getIntent();
        mRecipeName =getResources().getString(R.string.title_OneRecipeStepActivity);

        if(intentThatStarted.hasExtra("Step")) {
            mstep = intentThatStarted.getExtras().getParcelable("Step");
          //  mPlayerView.setDefaultArtwork(mstep.getmThumbnailURL());
            mtvTextDesc.setText(mstep.getmDescription());

            if(!mstep.getmVideoURL().equals("")){
                initializePlayer(NetworkUtils.getvideoURI(mstep.getmVideoURL()));

            }else{
                if(!mstep.getmThumbnailURL().equals("")) {
                    initializePlayer(NetworkUtils.getvideoURI(mstep.getmThumbnailURL()));
                }
                else{
                    mPlayerView.setVisibility(View.INVISIBLE);
                }
            }
        }
        if(intentThatStarted.hasExtra("Name")) {
            mRecipeName = intentThatStarted.getStringExtra("Name");
        }
        this.setTitle(mRecipeName);
    }

    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if(mExoPlayer!=null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
