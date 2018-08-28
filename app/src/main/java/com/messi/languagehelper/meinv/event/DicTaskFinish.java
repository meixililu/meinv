package com.messi.languagehelper.meinv.event;

/**
 * Created by luli on 25/02/2018.
 */

public class DicTaskFinish {

    private int mesg;

    public DicTaskFinish(int mesg){
        this.mesg = mesg;
    }

    public int getMesg(){
        return mesg;
    }
}
