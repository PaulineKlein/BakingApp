package com.pklein.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.pklein.bakingapp.data.ingredient;
import com.pklein.bakingapp.data.recipe;
import com.pklein.bakingapp.data.step;
import com.pklein.bakingapp.tools.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllRecipeStepsActivity extends AppCompatActivity implements AllRecipeStepsFragment.OnStepClickListener, ExoPlayer.EventListener{

    private static final String TAG = AllRecipeStepsActivity.class.getSimpleName();
    private boolean mTwoPane; // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private String mRecipeName;
    private recipe mrecipe;

    // Initialize the player :
    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private SimpleExoPlayerView mPlayerView;
    private  TextView mtvTextDesc;
    private  ImageView mReplaceVideoImageIv;

    private int mSavedPosState = 0;
    private static final String LIFECYCLE_STEP_POS = "Step_Pos";

    @BindView(R.id.tv_ingredient_name)    TextView mingredientNameTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recipe_steps);
        ButterKnife.bind(this);

        if(getIntent().hasExtra("Recipe")) {
            mrecipe = getIntent().getExtras().getParcelable("Recipe");
            mRecipeName = mrecipe.getmName();

            ingredient ing=mrecipe.getmIngredients().get(0);
            String ingredients="<b>"+ing.getmQuantity()+" "+ ing.getmMeasure()+"</b> "+ing.getmIngredient();

            for (int i = 1; i < mrecipe.getmIngredients().size (); i++)
            {
                ing = mrecipe.getmIngredients().get(i);
                ingredients=ingredients+"<br/><b>"+ing.getmQuantity()+" "+ ing.getmMeasure()+"</b> "+ing.getmIngredient();
            }

            mingredientNameTV.setText(Html.fromHtml(ingredients));

            // Determine if you're creating a two-pane or single-pane display
            if(findViewById(R.id.twoPane_one_recipe_step_linear_layout) != null) {
                mTwoPane = true;

                mPlayerView = (SimpleExoPlayerView)findViewById(R.id.playerView);
                mtvTextDesc = (TextView)findViewById(R.id.tv_step_desc);
                mReplaceVideoImageIv = (ImageView)findViewById(R.id.image_iv_replaceVideo);

                // Getting rid of the button that appears on phones for launching separate activities
                Button buttonPrevious = (Button) findViewById(R.id.buttonPrevious);
                Button buttonHome = (Button) findViewById(R.id.buttonHome);
                Button buttonNext = (Button) findViewById(R.id.buttonNext);
                buttonPrevious.setVisibility(View.GONE);
                buttonHome.setVisibility(View.GONE);
                buttonNext.setVisibility(View.GONE);

                if(savedInstanceState == null) {
                    initializeStepView(0);
                }
                else{
                    if (savedInstanceState.containsKey(LIFECYCLE_STEP_POS)) {
                        mSavedPosState = savedInstanceState.getInt(LIFECYCLE_STEP_POS);
                    }
                    initializeStepView(mSavedPosState);
                }
            }
            else {
                mTwoPane = false;
            }
        }
    }

    public void onStepSelected(int StepPos) {

        if(!mTwoPane) {
            Intent startChildActivityIntent = new Intent(this, OneRecipeStepActivity.class);
            startChildActivityIntent.putExtra("StepPos", StepPos);
            startChildActivityIntent.putExtra("Recipe", mrecipe);
            Log.i(TAG, "NAME  :"+mRecipeName);
            this.startActivity(startChildActivityIntent);
        }else{
            releasePlayer();
            initializeStepView(StepPos);
        }
    }

    private void initializeStepView(int stepId) {
        mSavedPosState = stepId;
        mtvTextDesc.setText(mrecipe.getmStep().get(stepId).getmDescription());
        mtvTextDesc.setTextSize(25);
        if (!mrecipe.getmStep().get(stepId).getmVideoURL().equals("")) {
            mReplaceVideoImageIv.setVisibility(View.INVISIBLE);
            mPlayerView.setVisibility(View.VISIBLE);
            initializeMediaSession(); // Initialize the Media Session.
            initializePlayer(NetworkUtils.getvideoURI(mrecipe.getmStep().get(stepId).getmVideoURL()));

        } else {
            mPlayerView.setVisibility(View.INVISIBLE);
            mReplaceVideoImageIv.setVisibility(View.VISIBLE);

            String img = "";
            if (!mrecipe.getmStep().get(stepId).getmThumbnailURL().equals("")) {
                img = mrecipe.getmStep().get(stepId).getmThumbnailURL();
            } else if (!mrecipe.getmImage().equals("")) {
                img = mrecipe.getmImage();
            }

            Picasso.with(this)
                    .load(NetworkUtils.getvideoURI(img))
                    .error(R.drawable.cooking)
                    .into(mReplaceVideoImageIv);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LIFECYCLE_STEP_POS, mSavedPosState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "bakingapp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(this, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new AllRecipeStepsActivity.MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    // ExoPlayer Event Listeners
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {}

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) { }

    @Override
    public void onLoadingChanged(boolean isLoading) {}

    @Override
    public void onPlayerError(ExoPlaybackException error) {}

    @Override
    public void onPositionDiscontinuity() {}

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync.
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }


    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
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


    private void releasePlayer() {
        if(mExoPlayer!=null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
        if(mMediaSession!=null){
            mMediaSession.setActive(false);
        }
    }
}