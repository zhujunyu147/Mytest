package com.honeywell.printer.util.events;

/**
 * Created by zhujunyu on 16/9/18.
 */
public class CommondEvent extends PrinterEvent{


   public enum CommondExcudeState{
        CLC_SUCCESS,CLC_FAILED,
       CNN_SUCCESS,
       CNN_FAILED,
       END_SUCCESS,
       END_FAILED,
       SYSTEM_INFO_SUCCESS,
       SYSTEM_INFO_FAILED,
       GET_CONFIG_SUCCESS,
       GET_CONFIG_FAILED,
       SET_CONFIG_SUCCESS,
       SET_CONFIG_FAILED,
       RESTART_SUCCESS,
       RESTART_FAILED,
       UPLOAD_CONFIG_SUCCESS,
       UPLOAD_CONFIG_FAILED,
       PIC_PRINTE_SUCCESS,
       PIC_PRINTE_FAILED,
       PRINT_CONFIG_SUCCESS,
       PRINT_CONFIG_FAILED,
       PRINT_WIFI_CONFIG_SUCCESS,
       PRINT_WIFI_CONFIG_FAILED,
       MEDIUM_CHECK_SUCCESS,
       MEDIUM_CHECK_FAILED
    }

    public enum CommondExcuter{
        CLC_COMMOND,
        CNN_COMMOND,
        SYSTEM_COMMOND,
        SEAECH_CONFIG_COMMOND,
        SET_CONFIG_COMMOND,
        RESTAET_COMMOND,
        UPLOAD_CONFIG_COMMOND,
        PIC_PRINTER,
        PRINT_CONFIG,
        PRINT_WIFI_CONFIG,
        MEDIUM_CHECK
    }


    public CommondExcudeState state;
    public CommondExcuter excuter;
    public CommondEvent(CommondExcudeState state,CommondExcuter excuter) {
        this.state = state;
        this.excuter = excuter;
    }
}
