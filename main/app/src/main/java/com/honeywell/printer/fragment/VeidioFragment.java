package com.honeywell.printer.fragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.honeywell.printer.R;


/**
 * 特效页面
 * @author zhujunyu
 */
public class VeidioFragment extends MyFragment  {

    private View view;


    private boolean isRefresh = true;// 获取数据成功还是失败 为后面执行刷新还是加载 成功或者失败


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.printer_vedio_fragment, container, false);

        // 缓存的view需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个view已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }


    @Override
    protected void onVisible(boolean isInit) {

        if(isInit){
            initView();//初始化控件
        }
    }

    /**
     * 适配器填充listView数据
     */
    private void initView() {
        String html1 ="http://v.youku.com/v_show/id_XMTUzMjQ5MjA3Ng==.html";
        TextView textView1 = (TextView) view.findViewById(R.id.tv_vedio_1);
        textView1.setText(html1);
        textView1.setAutoLinkMask(Linkify.ALL);
        textView1.setMovementMethod(LinkMovementMethod.getInstance());

        String html2 = "http://v.youku.com/v_show/id_XMTUzMjQ5MTM0NA==.html";
        TextView textView2 = (TextView) view.findViewById(R.id.tv_vedio_2);
        textView2.setText(html2);
        textView2.setAutoLinkMask(Linkify.ALL);
        textView2.setMovementMethod(LinkMovementMethod.getInstance());

        String html3 = "http://v.youku.com/v_show/id_XMTUzMjQ5MTM0NA==.html";
        TextView textView3 = (TextView) view.findViewById(R.id.tv_vedio_3);
        textView3.setText(html3);
        textView3.setAutoLinkMask(Linkify.ALL);
        textView3.setMovementMethod(LinkMovementMethod.getInstance());

        String html4 = "http://v.youku.com/v_show/id_XMTUzMjQ5MTM0NA==.html";
        TextView textView4 = (TextView) view.findViewById(R.id.tv_vedio_4);
        textView4.setText(html4);
        textView4.setAutoLinkMask(Linkify.ALL);
        textView4.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
