package com.honeywell.printer.dom4parse;

import android.content.Context;
import android.text.TextUtils;


import com.honeywell.printer.model.BaseItem;
import com.honeywell.printer.util.ShowToastUtil;

import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/24.
 */
public class CoreXmlParse {
    private static List<BaseItem> mBaseItemList;


    public static void parseClcXml(Context context) {
        ShowToastUtil.getInstance(context).shwoToast("CLC Connected ");
    }

    public static void parseCnnXml(Context context, Element element) {
        if (element == null) {
            return;
        }
        String tag = element.attributeValue("Action");
        if ("ACK_CONN".equals(tag)) {
            ShowToastUtil.getInstance(context).shwoToast("CONN执行成功");

        }
    }

    public static void parseSetLocationXml(Context context, Element element) {
        if (element == null) {
            return;
        }
        String tag = element.attributeValue("Action");
        if ("SetLocalization".equals(tag)) {
            ShowToastUtil.getInstance(context).shwoToast("SetLocalization执行成功");
        }
    }

    public static List<BaseItem> parseSystemXml(Element element) {
        if (element == null) {
            return null;
        }
        mBaseItemList = new ArrayList<BaseItem>();
        String tag = element.attributeValue("Action");
        if ("SystemInfo".equals(tag)) {
            Iterator it = element.elementIterator();
            while (it.hasNext()) {
                // 取得 item
                Element item = (Element) it.next();
                System.out.println(item.getName());
                System.out.println(item.getData());
                BaseItem baseItem = new BaseItem(item.getName(), (String) item.getData());
                mBaseItemList.add(baseItem);
            }
        }
        return mBaseItemList;
    }


    public static List<BaseItem> parseGetConfigXml(Element element) {
        if (element == null) {
            return null;
        }
        mBaseItemList = new ArrayList<BaseItem>();
        String tag = element.attributeValue("Action");
        if ("GetConfig".equals(tag)) {
            getNodes(element);
        }
        return mBaseItemList;
    }

    public static void getNodes(Element node){
        System.out.println("--------------------");

        //当前节点的名称、文本内容和属性
        System.out.println("当前节点名称："+node.getName());//当前节点名称
        System.out.println("当前节点的内容："+node.getTextTrim());//当前节点名称
        if(!TextUtils.isEmpty(node.getTextTrim())){

            List<Attribute> listAttr=node.attributes();//当前节点的所有属性的list
            String value = (String) node.getData();//属性的值
            for(Attribute attr:listAttr){//遍历当前节点的所有属性
                String name=attr.getName();//属性名称
                String key=attr.getValue();//属性的值
                System.out.println("属性名称："+name+"属性值："+value);
                BaseItem baseItem = new BaseItem(key, value);
                mBaseItemList.add(baseItem);
            }

        }


        //递归遍历当前节点所有的子节点
        List<Element> listElement=node.elements();//所有一级子节点的list
        for(Element e:listElement){//遍历所有一级子节点
            getNodes(e);//递归
        }
    }
}
