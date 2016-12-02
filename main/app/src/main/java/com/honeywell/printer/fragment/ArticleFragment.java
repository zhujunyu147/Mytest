package com.honeywell.printer.fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.honeywell.printer.R;


/**
 * 文章页面
 * @author zhujunyu
 */
public class ArticleFragment extends MyFragment   {

    private Context context;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.printer_text_fragment, container, false);

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
//            netGetData();//网络访问，获取列表数据
        }
    }

    /**
     * 适配器填充listView数据
     */
    private void initView() {
        TextView textView = (TextView) view.findViewById(R.id.tv_text);
        textView.setText("一、打印机输出空白纸\n对于针式打印机，引起打印纸空白的原因大多是由于色带油墨干涸、色带拉断、打印头损坏等，应及时更换色带或维修打印头；对于喷墨打印机，引起打印空白的故障大多是由于喷嘴堵塞、墨盒没有墨水等，应清洗喷头或更换墨盒；而对于激光打印机，引起该类故障的原因可能是显影辊未吸到墨粉(显影辊的直流偏压未加上)，也可能是感光鼓未接地，使负电荷无法向地释放，激光束不能在感光鼓上起作用。" +
                "\n二、打印纸输出变黑\n对于针式打印机，引起该故障的原因是色带脱毛、色带上油墨过多、打印头脏污、色带质量差和推杆位置调得太近等，检修时应首先调节推杆位置，如故障不能排除，再更换色带，清洗打印头，一般即可排除故障；对于喷墨打印机，应重点检查喷头是否损坏、墨水管是否破裂、墨水的型号是否正常等；对于激光打印机，则大多是由于电晕放电丝失效或控制电路出现故障，使得激光一直发射，造成打印输出内容全黑。因此，" +
                "应检查电晕放电丝是否已断开或电晕高压是否存在、激光束通路中的光束探测器是否工作正常。\n三、打印字符不全或字符不清晰\n对于喷墨打印机，可能有两方面原因，墨盒墨尽、打印机长时间不用或受日光直射而导致喷嘴堵塞。解决方法是可以换新墨盒或注墨水，如果墨盒未用完，可以断定是喷嘴堵塞：取下墨盒（对于墨盒喷嘴不是一体的打印机，需要取下喷嘴），把喷嘴放在温水中浸泡一会儿，注意一定不要把电路板部分浸在水中，否则后果不堪设想。");
    }


    /**
     * /网络访问，获取列表数据
     */
    private void netGetData() {

    }


}
