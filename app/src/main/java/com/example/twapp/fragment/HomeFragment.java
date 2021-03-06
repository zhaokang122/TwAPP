package com.example.twapp.fragment;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.twapp.R;
import com.example.twapp.adapter.TwDatasAdapter;
import com.example.twapp.base.BaseFragment;
import com.example.twapp.been.PeopleInfor;
import com.example.twapp.been.TwDataInfo;
import com.example.twapp.db.TwBody;
import com.example.twapp.swipe.SwipeAdapter;
import com.example.twapp.swipe.SwipeLayoutManager;
import com.example.twapp.utils.ChartUtils;
import com.example.twapp.utils.ChartView;
import com.example.twapp.utils.DBUitl;
import com.example.twapp.utils.DataConvertUtil;
import com.example.twapp.utils.DialogChange;
import com.example.twapp.utils.MyEventBus;
import com.example.twapp.utils.PlaySound;
import com.example.twapp.utils.ReadSerialPort;
import com.example.twapp.utils.SharedPreferencesUitl;
import com.example.twapp.utils.TWManager;
import com.example.twapp.utils.Vibrator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.view.LineChartView;

/**
 * ----------Dragon be here!----------/
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑
 * 　　　　┃　　　┃代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━神兽出没━━━━━━
 *
 * @author :孙天伟 in  2018/2/6   11:15.
 * 联系方式:QQ:420401567
 * 功能描述:
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener, SwipeAdapter.OnSwipeControlListener {
    private TextView tvFlag;
    private LineChartView chartView;
    private ChartView chartViews;
    private ImageView imageStart;
    private Vibrator vibrator;
    private TwDatasAdapter listAdapter;
    private ListView listPeople;
    private SwipeLayoutManager swipeLayoutManager;
    private SwipeAdapter swipeAdapter;
    private RecyclerView RecyclerView;
    private List<TwDataInfo> list = new ArrayList<>();
    private List<PeopleInfor> inforList = new ArrayList<PeopleInfor>();
    private ToggleButton toggleButton;
    private LinearLayout lyTable;
    private DBUitl dBtable;
    private String runingNumber = "";
    private static SharedPreferencesUitl preferencesUitl;
    private PeopleInfor peopleInfor;
    private boolean isFlag = true;

    @Override
    protected void create() {
        EventBus.getDefault().register(this);
        preferencesUitl = SharedPreferencesUitl.getInstance(getActivity(), "tw");
        vibrator = new Vibrator(getActivity());
        dBtable = new DBUitl();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.update");
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.update".equals(intent.getAction())) {
                initInfo();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        initInfo();
    }

    @Override
    protected void resuem() {

    }

    @Override
    protected void psuse() {

    }

    private List<Entry> getData(List<String> templeat) {
        List<Entry> values = new ArrayList<>();
        for (int i = 0; i < templeat.size(); i++) {
            if (templeat.get(i).equals("up") ||
                    templeat.get(i).equals("erro")
                    || templeat.get(i).equals("low")) {
            } else {
                if (Float.parseFloat(templeat.get(i)) < 35.0) {
                    values.add(new Entry(i, Float.parseFloat("35.0")));
                } else if (Float.parseFloat(templeat.get(i)) > 42.0) {
                    values.add(new Entry(i, Float.parseFloat("42.0")));
                } else {
                    values.add(new Entry(i, Float.parseFloat(templeat.get(i))));
                }
            }
        }
        return values;
    }

    LineChart mLineChart;

    @Override
    protected int getViewID() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View conteView) {
        tvFlag = conteView.findViewById(R.id.tv_flag);
        chartView = conteView.findViewById(R.id.chartView);

        mLineChart = conteView.findViewById(R.id.chart);
        ChartUtils.initChart(mLineChart);
        listPeople = conteView.findViewById(R.id.list_people);
        imageStart = conteView.findViewById(R.id.image_start);
        imageStart.setOnClickListener(this);
        toggleButton = conteView.findViewById(R.id.tbtn_change);
        lyTable = conteView.findViewById(R.id.table);
        chartViews = new ChartView(chartView, getActivity());
        chartViews.onClick();
        chartViews.initZhexian();
        RecyclerView = conteView.findViewById(R.id.recyclerview);
        listAdapter = new TwDatasAdapter(getActivity(), R.layout.list_item_layout, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.setLayoutManager(layoutManager);
        RecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        layoutManager.setRecycleChildrenOnDetach(true);
        RecyclerView.setAdapter(listAdapter);
        initSwipView();
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
//                    chartView.setVisibility(View.VISIBLE);
                    mLineChart.setVisibility(View.VISIBLE);
                    lyTable.setVisibility(View.GONE);
                    if (!"".equals(runingNumber)) {
                        TwBody twBody = dBtable.queryTwBody(runingNumber);
                        if (twBody.getTemperatures() != null) {
                            try {
                                ChartUtils.notifyDataSetChanged(mLineChart, twBody.getTwTime(), getData(twBody.getTemperatures()));
//                                chartViews.setKLine(twBody.getTemperatures(), twBody.getTwTime());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
//                    chartView.setVisibility(View.GONE);
                    mLineChart.setVisibility(View.GONE);
                    lyTable.setVisibility(View.VISIBLE);

                }
            }
        });

        conteView.findViewById(R.id.btn_db).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyDBToSDcrad();
            }
        });
    }

    @Override
    protected void setListener() {

    }

    private void initSwipView() {
        swipeLayoutManager = SwipeLayoutManager.getInstance();
        swipeAdapter = new SwipeAdapter(getActivity(), inforList);

        listPeople.setAdapter(swipeAdapter);
        listPeople.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                swipeLayoutManager.closeUnCloseSwipeLayout();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        swipeAdapter.setOnSwipeControlListener(this);
    }

    private void initInfo() {
        List<TwBody> list = dBtable.queryAll();
        inforList.clear();
        for (int i = 0; i < list.size(); i++) {


            peopleInfor = new PeopleInfor(list.get(i).getRunningNumber(), list.get(i).getPeopleNun(), list.get(i).
                    getPName(), list.get(i).getPaAge(), list.get(i).getPGender(),
                    list.get(i).getPBedNumber(), list.get(i).getIsLowBattery(), list.get(i).getPassId());

            inforList.add(peopleInfor);
            swipeAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 接收串口数据
     *
     * @param myEventBus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getSerilportData(MyEventBus myEventBus) {
        byte[] SerilportDatas = myEventBus.getTemperature();
        parseDatas(SerilportDatas);
    }

    public void parseDatas(byte[] b) {
        if (TWManager.isValid(b)) {
            TWManager.assembleData().parseFlag().decodeSNandpayload().parseSN().parsePayload();
            initInfo();//更新主界面各个 病人信息是否接受成功状态
            tvFlag.setText(TWManager.getBody().getDate());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    /**
     * 高低温报警
     *
     * @param d
     */
    public void isHights(float d) {
        if (d > preferencesUitl.read("hight", 38.0f)) {
            boolean cc = preferencesUitl.read("thightSound", false);
            if (cc) {
                PlaySound.play(PlaySound.HIGHT_SOUND, PlaySound.NO_CYCLE);
            }
            if (preferencesUitl.read("thightShake", false)) {
                vibrator.setVibrator();
            }
        } else if (d < preferencesUitl.read("low", 36.0f)) {
            boolean cc = preferencesUitl.read("thightSound", false);
            if (cc) {
                PlaySound.play(PlaySound.HIGHT_SOUND, PlaySound.NO_CYCLE);
            }
            if (preferencesUitl.read("thightShake", false)) {
                vibrator.setVibrator();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(broadcastReceiver);
        ReadSerialPort.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        if (isFlag) {
//            String s = "5A17C004D9205FC0FFBFFFBFFFBFFFBFFFBFFFBFFFBFE667";
//            byte[] s = {0x5A, 0x1B, (byte) 0xB0, 0x10, 0x67, (byte) 0x80, 0x7B, (byte) 0x80, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF,
//                    (byte) 0xBF, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF, (byte)
//                    0xBF, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF, (byte) 0x95, 0x44};
//            if (TWManager.isValid(DataConversionUtils.HexString2Bytes(s))) {
//                TWManager.assembleData().parseFlag().decodeSNandpayload().parseSN().parsePayload();
//            }
            isFlag = false;
            dBtable.ChagePassIDs();
            initInfo();
            imageStart.setBackground(getActivity().getDrawable(R.drawable.stop));
            ReadSerialPort.startReader();
        } else {
            isFlag = true;
            imageStart.setBackground(getActivity().getDrawable(R.drawable.start));
            ReadSerialPort.onDestroy();
        }
    }


    @Override
    public void onChangen(int position) {
        DialogChange.showCustomizeDialog(getActivity(), inforList.get(position).getRunNum());
    }

    @Override
    public void onDelete(int position) {
        dBtable.delete(inforList.get(position).getRunNum());
        inforList.remove(position);
        swipeAdapter.notifyDataSetChanged();
        runingNumber = "";
    }

    private long resultTimes = 0;

    @Override
    public void onItemClick(int position) {
        runingNumber = inforList.get(position).getRunNum();
        TwBody twBody = dBtable.queryTwBody(runingNumber);
        try {
            if (twBody != null) {
                if (twBody.getTemperatures() != null) {
                    list.clear();
                    for (int i = twBody.getTemperatures().size() - 1; i >= 0; i--) {
                        TwDataInfo twClass = new TwDataInfo();
                        if (twBody.getTemperatures().get(i).equals("up") ||
                                twBody.getTemperatures().get(i).equals("erro")
                                || twBody.getTemperatures().get(i).equals("low")) {
                            twClass.setTwData(twBody.getTemperatures().get(i) + "");
                            twClass.setTwTime(twBody.getTwTime().get(i));
                        } else {
                            isHights(Float.parseFloat(twBody.getTemperatures().get(i)));
                        }

                        if (twBody.getTemperatures().size() <= 16) {
                            twClass.setTwData(twBody.getTemperatures().get(i) + "");
                            twClass.setTwTime(twBody.getTwTime().get(i));
                        } else {
                            if (DataConvertUtil.timesLong(twBody.getTwTime().get(i))
                                    - DataConvertUtil.timesLong(twBody.getTwTime().get(i - 1)) < 14 * 60 * 1000) {
                                twClass.setTwData(twBody.getTemperatures().get(i) + "");
                                twClass.setTwTime(twBody.getTwTime().get(i));
                            } else {
                                long l = DataConvertUtil.timesLong(twBody.getTwTime().get(twBody.getTemperatures().size() - 1));
                                int i1 = (Integer.parseInt(DataConvertUtil.testTime_mm(l)) + 7) / 15 * 15;
                                if (i == twBody.getTemperatures().size() - 1) {
                                    if (i1 < 10) {
                                        resultTimes = DataConvertUtil.timesLong(DataConvertUtil.testTime_hh(l) + i1 + "0:00");
                                        twClass.setTwTime(DataConvertUtil.testTime(resultTimes));
                                    } else {
                                        String longtime = DataConvertUtil.testTime_hh(l) + i1 + ":00";
                                        resultTimes = DataConvertUtil.timesLong(longtime);
                                        twClass.setTwTime(DataConvertUtil.testTime(resultTimes));
                                    }
                                } else {
                                    twClass.setTwTime(DataConvertUtil.testTime(resultTimes -= 900 * 1000));
                                }
                                twClass.setTwData(twBody.getTemperatures().get(i) + "");
                                if (DataConvertUtil.timesLong(twClass.getTwTime()) - System.currentTimeMillis() > 0) {
                                    continue;
                                }
                            }
                        }
                        twClass.setNum(i);
                        list.add(twClass);
                        listAdapter.notifyDataSetChanged();
                        //                    ChartUtils.notifyDataSetChanged(mLineChart, twBody.getTwTime(), getData(twBody.getTemperatures()));
                    }
//                    for (int i = twBody.getTemperatures().size() - 1; i >= 0; i--) {
//                        if (twBody.getTemperatures().get(i).equals("up") ||
//                                twBody.getTemperatures().get(i).equals("erro")
//                                || twBody.getTemperatures().get(i).equals("low")) {
//                        } else {
//                            isHights(Float.parseFloat(twBody.getTemperatures().get(i)));
//                        }
//                        TwDataInfo twClass = new TwDataInfo();
//
//                        long l = DataConvertUtil.timesLong(twBody.getTwTime().get(i));
//                        int i1 = (Integer.parseInt(DataConvertUtil.testTime_mm(l)) + 7) / 15 * 15;
//                        if (i1 < 10) {
//                            twClass.setTwTime(DataConvertUtil.testTime_hh(l) + i1 + "0:00");
//                        } else {
//                            twClass.setTwTime(DataConvertUtil.testTime_hh(l) + i1 + ":00");
//                        }
//                        twClass.setTwData(twBody.getTemperatures().get(i) + "");
////                        twClass.setTwTime(twBody.getTwTime().get(i));
//                        if (DataConvertUtil.timesLong(twClass.getTwTime()) - System.currentTimeMillis() > 0) {
//                            continue;
//                        }
//                        twClass.setNum(i);
//                        list.add(twClass);
//                        listAdapter.notifyDataSetChanged();
                    ChartUtils.notifyDataSetChanged(mLineChart, twBody.getTwTime(), getData(twBody.getTemperatures()));
//                    }
                    //导出Excel表格


                    //***********导出数据************************
//                    Map<String, String> titleMap = new LinkedHashMap<String, String>();
//                    titleMap.put("num", "编号");
//                    titleMap.put("twTime", "时间");
//                    titleMap.put("twData", "体温");
//                    ExcelUtil.excelExport(getActivity(), list, titleMap, "体温数据" + runingNumber);
                    //***********导出数据************************
                } else {
                    Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}