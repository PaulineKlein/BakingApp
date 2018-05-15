package com.pklein.bakingapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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

import com.pklein.bakingapp.data.recipe;
import com.pklein.bakingapp.data.step;
import com.pklein.bakingapp.tools.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OneRecipeStepActivity extends AppCompatActivity  implements View.OnClickListener, ExoPlayer.EventListener {

    private static final String TAG = OneRecipeStepActivity.class.getSimpleName();
    private int mstepId;
    private recipe mrecipe;
    private step mstep;

    // Initialize the player :
    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    @BindView(R.id.playerView) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.card_view)   CardView mCard_view;
    @BindView(R.id.tv_step_desc)   TextView mtvTextDesc;
    @BindView(R.id.buttonPrevious)    Button mButtonPrevious;
    @BindView(R.id.buttonHome) Button mButtonHome;
    @BindView(R.id.buttonNext) Button mButtonNext;
    @BindView(R.id.image_iv_replaceVideo)    ImageView mReplaceVideoImageIv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_recipe_step);
        ButterKnife.bind(this);

        Intent intentThatStarted = this.getIntent();

        if(intentThatStarted.hasExtra("StepPos")) {
            mstepId = intentThatStarted.getIntExtra("StepPos",0);

            if(intentThatStarted.hasExtra("Recipe")) {
                mrecipe = intentThatStarted.getExtras().getParcelable("Recipe");
                mstep = mrecipe.getmStep().get(mstepId);

                this.setTitle(mrecipe.getmName());
                mtvTextDesc.setText(mstep.getmDescription());

                if(!mstep.getmVideoURL().equals("")){
                    initializeMediaSession(); // Initialize the Media Session.
                    initializePlayer(NetworkUtils.getvideoURI(mstep.getmVideoURL()));

                }else{
                    mPlayerView.setVisibility(View.INVISIBLE);
                    mReplaceVideoImageIv.setVisibility(View.VISIBLE);

                    String img = "";
                    if(!mstep.getmThumbnailURL().equals("")) {
                        img=mstep.getmThumbnailURL();
                    }
                    else if(!mrecipe.getmImage().equals(""))
                    {
                        img = mrecipe.getmImage();
                    }

                    Picasso.with(this)
                            .load(NetworkUtils.getvideoURI(img))
                            .error(R.drawable.cooking)
                            .into(mReplaceVideoImageIv);
                }

                //button to go to previous step : (if it is the first step, then Hide button)
                int size = mrecipe.getmStep().size()-1;
                Log.i(TAG, "mstepId : "+mstepId+" and size :"+size);
                if(mstepId>0){
                    mButtonPrevious.setOnClickListener(this);
                    mButtonPrevious.setText("Previous");
                }
                else{
                    mButtonPrevious.setVisibility(View.INVISIBLE);
                }

                // button to go to next step (if it is the last step, then Hide button)
                if(mstepId<size){
                    mButtonNext.setOnClickListener(this);
                    mButtonNext.setText("Next");
                }
                else{
                    mButtonNext.setVisibility(View.INVISIBLE);
                }

                //button to return to Steps selection (AllRecipeStepsActivity)
                mButtonHome.setOnClickListener(this);
                mButtonHome.setText("Home");

            }
        }

    }

    /**
     * For landscape View : hide evereything except ExoPlayer
     */
    private void setAllInvisible(){
        mButtonNext.setVisibility(View.INVISIBLE);
        mButtonPrevious.setVisibility(View.INVISIBLE);
        mButtonHome.setVisibility(View.INVISIBLE);
        mReplaceVideoImageIv.setVisibility(View.INVISIBLE);
        mCard_view.setVisibility(View.INVISIBLE);
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

            //check for landscape : (with the help of : https://stackoverflow.com/questions/46713761/how-to-play-video-full-screen-in-landscape-using-exoplayer)
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mPlayerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                mPlayerView.setLayoutParams(params);
                setAllInvisible();
            }

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
        mMediaSession.setCallback(new MySessionCallback());

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


    /**
     * The OnClick method for all of the buttons.
     *
     * @param v The button that was clicked.
     */
    @Override
    public void onClick(View v) {

        // Get the button that was pressed.
        Button pressedButton = (Button) v;
        Intent startChildActivityIntent;

        switch(pressedButton.getId())
        {
            case R.id.buttonPrevious:
                startChildActivityIntent = new Intent(this, OneRecipeStepActivity.class);
                if(mrecipe != null)
                {
                    int StepId = mstepId -1;
                    startChildActivityIntent.putExtra("StepId", StepId);
                    startChildActivityIntent.putExtra("Recipe", mrecipe);
                }
                releasePlayer();
                this.startActivity(startChildActivityIntent);

                break;

            case R.id.buttonNext:
                startChildActivityIntent = new Intent(this, OneRecipeStepActivity.class);
                if(mrecipe != null)
                {
                    int StepId = mstepId +1;
                    startChildActivityIntent.putExtra("StepId", StepId);
                    startChildActivityIntent.putExtra("Recipe", mrecipe);
                }
                releasePlayer();
                this.startActivity(startChildActivityIntent);

                break;

            case R.id.buttonHome:
                startChildActivityIntent = new Intent(this, AllRecipeStepsActivity.class);
                if(mrecipe != null)
                {
                    startChildActivityIntent.putExtra("Recipe", mrecipe);
                }
                releasePlayer();
                this.startActivity(startChildActivityIntent);

                break;

            default:
                Log.e(TAG, "ERROR while clicking on the button :" + pressedButton.getId());
                break;
        }
    }
}
