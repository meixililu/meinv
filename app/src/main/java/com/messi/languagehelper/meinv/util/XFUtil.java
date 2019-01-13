package com.messi.languagehelper.meinv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.event.ProgressEvent;
import com.messi.languagehelper.meinv.task.PublicTask;

import org.greenrobot.eventbus.EventBus;

public class XFUtil {
	
	public static final String SpeakerEn = "vimary";
	public static final String SpeakerZh = "xiaoqi";//vixy xiaoqi  xiaoyan
	public static final String SpeakerHk = "xiaomei";
	
	private static final String VoiceEngineZH = "zh_cn";
	public static final String VoiceEngineEN = "en_us";
	public static final String VoiceEngineMD = "mandarin";
	public static final String VoiceEngineHK = "cantonese";
	
	public static final String PlayResultOnline = "PlayResultOnline";
	public static final String PlayResultOffline = "PlayResultOffline";
	public static final String PlayQueryOnline = "PlayQueryOnline";
	public static final String PlayQueryOffline = "PlayQueryOffline";
	
//	中文：
//	domain=iat,language=zh_cn,accent=mandarin
//	英文:
//	domain=iat，language=en_us
//	粤语：
//	domain=iat,language=zh_cn,accent=cantonese
//	这个需要你自己设置，合成使用tts_audio_path设置，识别转写使用asr_audio_path

	public static String getVoiceEngineText(String voiceEngine){
		if(voiceEngine.equals(VoiceEngineMD)){
			return "中";
		}else if(voiceEngine.equals(VoiceEngineEN)){
			return "En";
		}else if(voiceEngine.equals(VoiceEngineHK)){
			return "粤";
		}else {
			return "中";
		}
	}
	
	/**语音转写**/
	public static void showSpeechRecognizer(Context mContext, SharedPreferences mSharedPreferences,
                                            SpeechRecognizer iatRecognizer, RecognizerListener recognizerListener, String voiceEngine) {
		LogUtil.DefalutLog("voiceEngine:"+voiceEngine);
		if (null == iatRecognizer) {
			iatRecognizer = SpeechRecognizer.createRecognizer(mContext,null);
		}
		//获取引擎参数
		String domain = mSharedPreferences.getString(
				mContext.getString(R.string.preference_key_iat_engine),
				mContext.getString(R.string.preference_default_iat_engine));
		if(voiceEngine.equals(VoiceEngineMD)){
			iatRecognizer.setParameter(SpeechConstant.LANGUAGE, VoiceEngineZH);
			iatRecognizer.setParameter(SpeechConstant.ACCENT, VoiceEngineMD);
		}else if(voiceEngine.equals(VoiceEngineHK)){
			iatRecognizer.setParameter(SpeechConstant.LANGUAGE, VoiceEngineZH);
			iatRecognizer.setParameter(SpeechConstant.ACCENT, VoiceEngineHK);
		}else if(voiceEngine.equals(VoiceEngineEN)){
			iatRecognizer.setParameter(SpeechConstant.LANGUAGE, VoiceEngineEN);
			iatRecognizer.setParameter(SpeechConstant.ACCENT, "");
		}
		//清空Grammar_ID，防止识别后进行听写时Grammar_ID的干扰
		iatRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
		iatRecognizer.setParameter(SpeechConstant.DOMAIN, domain);
		//设置采样率参数，支持8K和16K 
		String rate = mSharedPreferences.getString(
				mContext.getString(R.string.preference_key_iat_rate),
				mContext.getString(R.string.preference_default_iat_rate));
		if(rate.equals("rate8k")){
			iatRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
		}else {
			iatRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
		}
		iatRecognizer.startListening(recognizerListener);
	}
	
	/**合成语音**/
	public static void showSpeechSynthesizer(Context mContext,
											 SharedPreferences mSharedPreferences,
											 SpeechSynthesizer mSpeechSynthesizer,
											 String source,
											 SynthesizerListener mSynthesizerListener) {
		if (null == mSpeechSynthesizer) {
			mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext,null);
		}
		LogUtil.DefalutLog("role:"+Setings.role+"--source:"+source);
		StringUtils.setSpeaker(source);
		mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, Setings.role);
		mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, String.valueOf( mSharedPreferences.getInt("tts_speed", 50) ));
		mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "100");
		mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");//离线 local
		mSpeechSynthesizer.startSpeaking(source, mSynthesizerListener);
	}
	
	/**合成语音**/
	public static void showSpeechSynthesizer(Context mContext,
											 SharedPreferences mSharedPreferences,
											 SpeechSynthesizer mSpeechSynthesizer,
											 String source,
											 String speaker,
											 SynthesizerListener mSynthesizerListener) {
		if (null == mSpeechSynthesizer) {
			mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext,null);
		}
		mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, speaker);
		mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, String.valueOf( mSharedPreferences.getInt("tts_speed", 50) ));
		mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "100");
		mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");//离线 local
		mSpeechSynthesizer.startSpeaking(source, mSynthesizerListener);
	}
	
	public static void playVideoInBackground(Context mContext, SpeechSynthesizer mSpeechSynthesizer, SharedPreferences mSharedPreferences,
                                             String filepath, String speakContent){
		LogUtil.DefalutLog("filepath:"+filepath);
		if(!AudioTrackUtil.isFileExists(filepath)){
			mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, filepath);
			XFUtil.showSpeechSynthesizer(mContext,mSharedPreferences,mSpeechSynthesizer,speakContent,
					new SynthesizerListener() {
				@Override
				public void onSpeakResumed() {
				}
				@Override
				public void onSpeakProgress(int arg0, int arg1, int arg2) {
				}
				@Override
				public void onSpeakPaused() {
				}
				@Override
				public void onSpeakBegin() {
				}
				@Override
				public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
				}
				@Override
				public void onCompleted(SpeechError arg0) {
				}
				@Override
				public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
				}
			});
		}else{
			playPcm(mContext, filepath);
		}
	}
	
	public static void playPcm(Context mContext, final String filepath){
		PublicTask mPublicTask = new PublicTask(mContext);
		mPublicTask.setmPublicTaskListener(new PublicTask.PublicTaskListener() {
			@Override
			public void onPreExecute() {
			}
			@Override
			public Object doInBackground() {
				AudioTrackUtil.createAudioTrack(filepath);
				return null;
			}
			@Override
			public void onFinish(Object resutl) {
			}
		});
		mPublicTask.execute();
	}

	public static void play(final String content, String speaker){
		PlayUtil.playOnline(speaker, content, new SynthesizerListener() {
			@Override
			public void onSpeakResumed() {
			}

			@Override
			public void onSpeakProgress(int arg0, int arg1, int arg2) {
			}

			@Override
			public void onSpeakPaused() {
			}

			@Override
			public void onSpeakBegin() {
				EventBus.getDefault().post(new ProgressEvent(1));
			}

			@Override
			public void onCompleted(SpeechError arg0) {
				EventBus.getDefault().post(new ProgressEvent(1));
				if (arg0 != null) {
					LogUtil.DefalutLog(arg0.getErrorDescription());
				}
			}

			@Override
			public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
				if (arg0 < 10) {
					EventBus.getDefault().post(new ProgressEvent(0));
				}
			}

			@Override
			public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			}
		});
	}
	
}
