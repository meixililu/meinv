package com.messi.languagehelper.meinv.event;

/**
 * Created by luli on 02/03/2018.
 */

public class ProgressEvent {

    public int status;//0 show,1 hide

    public ProgressEvent(int status){
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
