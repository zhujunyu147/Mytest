package com.honeywell.printer.util.events;

import com.honeywell.printer.model.BaseItem;

import java.util.List;

/**
 * Created by zhujunyu on 16/9/18.
 */
public class PrinterDateEvent extends PrinterEvent {


    public enum PrinterDataType {
        SYSTEM_INFO, GET_CONFIG
    }

    public List<BaseItem> mBaseItemList;

    public  PrinterDataType dataType;
    public PrinterDateEvent(List<BaseItem> mBaseItemList,PrinterDataType dataType) {

        this.mBaseItemList = mBaseItemList;
        this.dataType = dataType;
    }
}
