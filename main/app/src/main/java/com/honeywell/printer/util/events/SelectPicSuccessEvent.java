package com.honeywell.printer.util.events;

/**
 * Created by zhujunyu on 16/9/22.
 */
public class SelectPicSuccessEvent extends PrinterEvent {



    public SelectStatus selectStatus;
    public Object object;

    public enum SelectStatus {
        SELECT_SUCCESS,
        SELECT_FAILED
    }

    public SelectPicSuccessEvent(SelectStatus selectStatus,Object object) {
        this.selectStatus = selectStatus;
        this.object = object;
    }
}
