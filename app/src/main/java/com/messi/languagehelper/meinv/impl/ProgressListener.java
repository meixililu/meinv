package com.messi.languagehelper.meinv.impl;

public interface ProgressListener {

	void update(long bytesRead, long contentLength, boolean done);
	
}
