package se.sensiblethings.app.chitchato.extras;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import se.sensiblethings.app.R;


public class SoundEffectManager {
	
	static private SoundEffectManager _instance;
	private static SoundPool mSoundPool; 
	private static HashMap<Integer, Integer> mSoundPoolMap; 
	private static AudioManager  mAudioManager;
	private static Context mContext;
	
	private SoundEffectManager()
	{   
	}

	static synchronized public SoundEffectManager getInstance()
	{
	    if (_instance == null) 
	      _instance = new SoundEffectManager();
	    return _instance;
	 }
	

	public static  void initSounds(Context theContext) 
	{ 
		 mContext = theContext;
	     mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
	     mSoundPoolMap = new HashMap<Integer, Integer>(); 
	     mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE); 	    
	} 
	
	
	public static void addSound(int Index,int SoundID)
	{
		mSoundPoolMap.put(Index, mSoundPool.load(mContext, SoundID, 1));
	}
	

	public static void loadSounds()
	{
		mSoundPoolMap.put(1, mSoundPool.load(mContext, R.raw.pin_drop, 1));
		mSoundPoolMap.put(2, mSoundPool.load(mContext, R.raw.sms_alert_daniel_simon, 1));
		mSoundPoolMap.put(3, mSoundPool.load(mContext, R.raw.tiny_button_push, 1));
		mSoundPoolMap.put(4, mSoundPool.load(mContext, R.raw.chooser_astrix, 1));
	}
	
	
	public static void playSound(int index,float speed) 
	{ 		
		     float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		     streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, speed); 
	}

	public static void stopSound(int index)
	{
		mSoundPool.stop(mSoundPoolMap.get(index));
	}
	
	public static void cleanup()
	{
		mSoundPool.release();
		mSoundPool = null;
	    mSoundPoolMap.clear();
	    mAudioManager.unloadSoundEffects();
	    _instance = null;
	    
	}

	
}