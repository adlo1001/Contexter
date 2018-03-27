package se.sensiblethings.app.chitchato.context;

import android.media.MediaRecorder;
import android.os.Handler;

import java.util.ArrayList;

public class SoundPressureLevel {

	MediaRecorder nRecorder;
	public String value1;
	private static String volumeVisual = "";
	public int volumeToSend;
	private Handler handler;
	public MediaRecorder mRecorder;
	Thread runner, sender;
	public static boolean FLAG = false;

	private static double mEMA = 0.0;
	static final private double EMA_FILTER = 0.6;
	double ampl = 1 * Math.pow(10, -4);
	ArrayList<Double> last_ten_sound_captures = new ArrayList<Double>();

	public SoundPressureLevel(String user_id) {
	}

	public void startRecorder() {
		/**
		 * set the audio source to microphone,the format to 3gpp and audio
		 * encoder to ADAPTIVE MULTI RATE NAROW BAND out put file is stored
		 * dev/null we don't need to store
		 */
		if (mRecorder == null) {
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile("/dev/null");
			try {
				mRecorder.prepare();
			} catch (java.io.IOException ioe) {
				android.util.Log.e("[prepare]", "IOException: "
						+ android.util.Log.getStackTraceString(ioe));

			} catch (java.lang.SecurityException e) {
				android.util.Log.e("[prepare]", "SecurityException: "
						+ android.util.Log.getStackTraceString(e));
			}
			try {
				mRecorder.start();
			} catch (java.lang.SecurityException e) {
				android.util.Log.e("[prepare]", "SecurityException: "
						+ android.util.Log.getStackTraceString(e));
			}catch(IllegalStateException ie)
			{
				ie.printStackTrace();
			}


		}
	}

	public void stopRecorder() {
		if (mRecorder != null) {
			FLAG = true;

			mRecorder.stop();
			mRecorder.reset();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public double soundDb(double ampl) {

		return 20.0 * Math.log10(getAmplitudeEMA() / ampl);
	}

	public double getSoundDb() {
		double sound_level = Math.round(soundDb(ampl) * 100.0) / 100.0;
		if (sound_level < 0) {
			sound_level = 0.0;

		} else {
			sound_level = soundDb(ampl);

		}
		if (last_ten_sound_captures.size() > 10) {
			last_ten_sound_captures.remove(0);
			last_ten_sound_captures.add(sound_level);
		} else {
			last_ten_sound_captures.add(sound_level);
		}
		double sum = 0.0;
		for (double d : last_ten_sound_captures) {
			sum = sum + d;
		}
		sound_level = sum / last_ten_sound_captures.size();
		return sound_level;
	}

	public double getAmplitude() {
		if (mRecorder != null)
			return (mRecorder.getMaxAmplitude() / 2700.0);
		else
			return 0;

	}

	public double getTheAmplitude() {
		if (mRecorder != null)
			return (mRecorder.getMaxAmplitude());
		else
			return 1;

	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}

	public void startListening() {
		handler = new Handler();
		final Runnable updater = new Runnable() {

			@Override
			public void run() {
				handler.postDelayed(this, 250);

				if (runner == null && !FLAG) {
					runner = new Thread() {
						@Override
						public void run() {
							while (runner != null && !FLAG) {
								try {
									getAmplitudeEMA();
									//System.out.println("SPL running");
									if (FLAG) {
										this.interrupt();
									}
									Thread.sleep(1000);
								} catch (InterruptedException e) {
								}
								;
							}
						}
					};

					if (!FLAG) {
						runner.start();
						startRecorder();
					} else {
						runner.interrupt();
					}

				} else {
				}
			}
		};
		handler.postDelayed(updater, 250);

	}

}
