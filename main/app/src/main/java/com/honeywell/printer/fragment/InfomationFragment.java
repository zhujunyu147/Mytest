package com.honeywell.printer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.honeywell.printer.R;

/**
 * 信息
 *
 * @author zhujunyu
 */
public class InfomationFragment extends MyFragment implements View.OnClickListener {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_list, container, false);

        return view;
    }


    /**
     * onClick事件
     */
    @Override
    public void onClick(View view) {
    }


    @Override
    protected void onVisible(boolean isInit) {
        if (isInit) {
            initView();
        }
    }

    private void initView() {
        TextView textView = (TextView) view.findViewById(R.id.device_about);
        textView.setText("Intermec是世界上唯一的一家在自动识别领域提供全面解决方案的公司。它的产品范围包括条码打印机、条码扫描器、手持数据采集终端、固定式工业终端、车载数据终端、无线网络产品、数据采集服务器、条码标签与色带、移动电脑、移动打印机、通讯服务器、无线射频（RFID）标签、无线射频（RFID）标签打印机、无线射频（RFID）阅读器和各种应用软件和工具软件。自1966成立以来，Intermec在自动识别领域具有许多重要的技术发明和创新，领导和推动了自动识别技术在全世界的应用和发展。在无线射频（RFID）技术正如火如荼地推向全世界的时候，Intermec以拥有137项关键技术发明和知识产权再次傲视全雄，引领这场技术革命。");
    }
}
