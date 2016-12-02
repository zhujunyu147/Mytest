package com.honeywell.printer.model;

import java.io.Serializable;

/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/22.
 */
public class BaseItem implements Serializable {

    public String key;
    public String value;


    public BaseItem(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
