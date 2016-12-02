package com.honeywell.printer.util.events;

import java.io.OutputStream;

/**
 * Created by zhujunyu on 16/9/27.
 */
public class PostDataToPrinterEvent extends PrinterEvent {

    public OutputStream mmOutStream;
    public byte[] bytes;

    public PostDataToPrinterEvent(OutputStream mmOutStream, byte[] buffer) {
        this.mmOutStream = mmOutStream;
        this.bytes = buffer;
    }
}
