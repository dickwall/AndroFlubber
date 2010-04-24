package com.dickwallcommunications.androflubber;

import java.text.DecimalFormat;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AndroFlubber extends Activity {
	
	private static final String FLUB_LIST = "flublist";
	private static final String START_TIME = "starttime";
	private static final String FLUB_COUNT = "flubcount";
	
	private TextView mTimerView;
	private EditText mFlubList;
	private Handler mHandler = new Handler();
	
	private long mStartTime = 0L;
	private int mFlubCount = 0;
	
	private DecimalFormat twoPlaces = new DecimalFormat("0.00");
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // check to see if the savedInstanceState has a start time and flub list to restore
        String flubListText =
        	savedInstanceState == null ? null : savedInstanceState.getString(FLUB_LIST);
        long startTime =
        	savedInstanceState == null ? 0L : savedInstanceState.getLong(START_TIME, 0L);
        int flubCount = 
        	savedInstanceState == null ? 0 : savedInstanceState.getInt(FLUB_COUNT, 0);
        
        mTimerView = (TextView)findViewById(R.id.TimerView);
        mFlubList = (EditText)findViewById(R.id.FlubList);
        
        final Button flubButton = ((Button)findViewById(R.id.FlubButton));
        final MediaPlayer whips[] = 
        	{ 
        		MediaPlayer.create(this, R.raw.whip1),
        		MediaPlayer.create(this, R.raw.whip2),
        		MediaPlayer.create(this, R.raw.whip3),
        		MediaPlayer.create(this, R.raw.whip4),
        		MediaPlayer.create(this, R.raw.whip5)
        	};
        
        if (flubListText != null && flubListText.length() > 0) mFlubList.setText(flubListText);
        
        if (startTime != 0) {
        	mStartTime = startTime;
        	// and we need to start the timer handler again here
			mHandler.removeCallbacks(mUpdateTimeTask);
			mHandler.postDelayed(mUpdateTimeTask, 100);
        }
        
        if (flubCount != 0) mFlubCount = flubCount;
        
        Button startButton = ((Button)findViewById(R.id.StartButton));
        
        startButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (mStartTime == 0L) {
						mStartTime = SystemClock.elapsedRealtime();
						mFlubCount = 1;
						mHandler.removeCallbacks(mUpdateTimeTask);
						mHandler.postDelayed(mUpdateTimeTask, 100);
						flubButton.requestFocus();
					}
				}
			});

        ((Button)findViewById(R.id.StopButton))
            .setOnClickListener(new OnClickListener() {
            	public void onClick(View v) {
            		mHandler.removeCallbacks(mUpdateTimeTask);
            		mStartTime = 0L;
            		mFlubCount = 0;
            		mTimerView.setText("0:00 ");
            	}
            });
        
        ((Button)findViewById(R.id.ClearButton))
            .setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (mStartTime == 0L) { // only if not running - prevent accidental wiping
						mFlubList.setText("");
					}
				}
			});
        
        ((Button)findViewById(R.id.EmailButton))
            .setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					emailIntent.setType("plain/text");
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Flubs from AndroFlubber");
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mFlubList.getText().toString());
					startActivity(Intent.createChooser(emailIntent, "Send Flubs via Email..."));
				}
			});
        
        ((Button)findViewById(R.id.WhipButton))
        	.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					playRandomWhip(v, whips);
				}
			});
        
        flubButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					flubButtonPressed(v);
				}
			});
        
        if (startTime == 0L) {
        	startButton.requestFocus();
        }
        else {
        	flubButton.requestFocus();
        }
    }

    private void flubButtonPressed(View v) {
    	long startTime = mStartTime;
    	if (startTime == 0L) return; 	// bail if the timer is not running
    	v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    	double seconds = 0.0;
    	long millis = SystemClock.elapsedRealtime() - startTime;
    	seconds = (double)millis / 1000.0;
    	
    	String label = twoPlaces.format(seconds) + " \t" + twoPlaces.format(seconds) + " \t" + mFlubCount + "\n";
    	mFlubList.append(label);
    	mFlubCount++;
    }
    
    private void playRandomWhip(View v, MediaPlayer[] whips) {
    	v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    	int whipNo = (new Random()).nextInt(5);
    	whips[whipNo].start();
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString(FLUB_LIST, mFlubList.getText().toString());
		outState.putLong(START_TIME, mStartTime);
		outState.putInt(FLUB_COUNT, mFlubCount);
	}
    
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			final long start = mStartTime;
			long millis = SystemClock.elapsedRealtime() - start;
			int seconds = (int)(millis / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;
			
			if (seconds < 10) {
				mTimerView.setText("" + minutes + ":0" + seconds + " ");
			}
			else {
				mTimerView.setText("" + minutes + ":" + seconds + " ");
			}
			
			mHandler.postDelayed(this, 1000);
		}
	};
    
}