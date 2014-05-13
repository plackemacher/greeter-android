package pala.co.greeter;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends Activity implements SensorEventListener {

	private static final int COLOR_HAPPY = Color.GREEN;
	private static final int COLOR_SCARED = Color.RED;

	private static final int[] SOUND_RESOURCE_IDS = new int[]{
			R.raw.hello1,
			R.raw.hello2,
			R.raw.hello3,
			R.raw.hello4
	};

	private TextView mTextView;

	private SensorManager mSensorManager;
	private MediaPlayer mMediaPlayer;

	private boolean mSoundPlayed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTextView = (TextView)findViewById(android.R.id.primary);
		mTextView.setTextColor(COLOR_SCARED);

		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

		final Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		if(sensor != null) {
			mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
		}

		createSound();
	}

	@Override
	protected void onDestroy() {
		mSensorManager.unregisterListener(this);

		destroySound();

		super.onDestroy();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		final float lightLevel = event.values[0];

		mTextView.setText(String.format("%.0f lux", lightLevel));

		if(lightLevel < 50.0f) {
			mTextView.setTextColor(COLOR_SCARED);

			stopSound();
		} else {
			mTextView.setTextColor(COLOR_HAPPY);

			playSound();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	private static int randomSoundResId() {
		final int index = new Random().nextInt(SOUND_RESOURCE_IDS.length);

		return SOUND_RESOURCE_IDS[index];
	}

	private void createSound() {
		mMediaPlayer = MediaPlayer.create(this, randomSoundResId());
	}

	private void destroySound() {
		mMediaPlayer.release();
	}

	private void playSound() {
		if(!mSoundPlayed) {
			mSoundPlayed = true;

			mMediaPlayer.start();
		}
	}

	private void stopSound() {
		if(mSoundPlayed) {
			mSoundPlayed = false;

			mMediaPlayer.stop();
			AssetFileDescriptor afd = getResources().openRawResourceFd(randomSoundResId());

			mMediaPlayer.reset();
			try {
				mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				afd.close();
				mMediaPlayer.prepare();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
