package com.messi.languagehelper.meinv.task;

import android.media.AudioFormat;
import android.os.Handler;
import android.os.Message;

import com.messi.languagehelper.meinv.util.AudioTrackUtil;
import com.messi.languagehelper.meinv.util.MyAudioTrack;

public class MyThread implements Runnable {

	public static final int EVENT_PLAY_OVER = 0x100;
	public static boolean isPlaying;
	private byte[] data;
	private Handler mHandler;
	public MyAudioTrack myAudioTrack;
	private Object object = new Object();

	public MyThread() {}

	public MyThread(Handler handler) {
		this.mHandler = handler;
	}
	
	public MyThread(byte[] data) {
		this.data = data;
	}

	public MyThread(byte[] data, Handler handler) {
		this.data = data;
		mHandler = handler;
	}

	public void setDataUri(String path){
		byte[] data = AudioTrackUtil.getBytes(path);
		setData(data);
		isPlaying = true;
	}

	public void run() {
		synchronized (object) {
			try {
				if (data == null || data.length == 0) {
					return;
				}
				myAudioTrack = new MyAudioTrack(8000,
						AudioFormat.CHANNEL_OUT_STEREO,
						AudioFormat.ENCODING_PCM_16BIT);
				myAudioTrack.init();
				int playSize = myAudioTrack.getPrimePlaySize();
				int index = 0;
				int offset = 0;
				while (!Thread.currentThread().isInterrupted() && isPlaying) {
					try {
	//					Thread.sleep(0);
						offset = index * playSize;
						if (offset >= data.length) {
							isPlaying = false;
							break;
						}
						myAudioTrack.playAudioTrack(data, offset, playSize);
					} catch (Exception e) {
						e.printStackTrace();
						isPlaying = false;
						break;
					}
					index++;
				}
				myAudioTrack.release();
				if(mHandler != null){
					Message msg = Message.obtain(mHandler, EVENT_PLAY_OVER);
					mHandler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void stopPlaying(){
		isPlaying = false;
		if(myAudioTrack != null){
			myAudioTrack.release();
		}
	}


}