package fivemoreminutes.a5moreminutes.Charts;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import Database.DatabaseAccess;
import fivemoreminutes.a5moreminutes.Entity.App;
import fivemoreminutes.a5moreminutes.Settings.SharPreferences;
import fivemoreminutes.a5moreminutes.R;

public class CombinedCharts {
    public CombinedChart chart;
    public  Context context;
    public static final String APP_PREFERENCES = "mysettings";
    public SharPreferences pref;
    public Intent intent;
    private DatabaseAccess databaseAccess;


    public CombinedCharts(CombinedChart chart, Context context,Intent intent) {
        this.chart = chart;
        this.context = context;
        this.intent = intent;
        this.databaseAccess = DatabaseAccess.getInstance(context);
    }

    public CombinedChart getChart() {
        return chart;
    }

    public void setChart(CombinedChart chart) {
        this.chart = chart;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public void mainLoad(Map<String,App> map, App app_all){
        pref = new SharPreferences(context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE));
        pref.loadLimitSwitch(intent);
        pref.loadLimit(intent);

        YAxis ya = chart.getAxisRight();
        ya.setDrawGridLines(false);
        ya.setDrawAxisLine(false);
        ya.setDrawZeroLine(false);
        ya.setDrawTopYLabelEntry(false);
        ya.setDrawGridLinesBehindData(false);
        ya.setDrawLimitLinesBehindData(false);
        ya.setDrawLabels(false);
        chart.getDescription().setText("");
        ya = chart.getAxisLeft();
        ya.setDrawGridLines(false);
        ya.setDrawZeroLine(true);
        ya.setDrawTopYLabelEntry(false);
        ya.setDrawGridLinesBehindData(false);
        ya.setDrawLimitLinesBehindData(true);
        ya.setDrawLabels(false);
        ya.removeAllLimitLines();
        if (pref.isAllLimit()) {
            ya.addLimitLine(new LimitLine(pref.getLimit_hour()*60 + pref.getLimit_min(), "Total limit"));
            ya.setAxisMaxValue(pref.getLimit_hour()*60 + pref.getLimit_min()+100);
        }

        XAxis xa = chart.getXAxis();
        xa.setDrawGridLines(false);
        xa.setDrawAxisLine(false);
        xa.setDrawGridLinesBehindData(true);
        xa.setDrawLimitLinesBehindData(true);
        xa.setDrawLabels(false);
        xa.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xa.setAxisMinimum(0.5f);
        xa.setGranularity(0.5f);
        xa.setAxisMaximum(9.5f);

        CombinedData Cdata = new CombinedData();

        ArrayList<BarEntry> UVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> SVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> GVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> VVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> IVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> NVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> PVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> MVals = new ArrayList<BarEntry>();
        ArrayList<BarEntry> AVals = new ArrayList<BarEntry>();
        float sumU = 0f,sumS = 0f,sumG = 0f,sumV = 0f,sumI = 0f,sumN = 0f,sumP = 0f,sumM = 0f,sumA = 0f;

        for (Map.Entry<String, App> app : map.entrySet()) {
            switch (app.getValue().getNumCategory()){
                case -1: {sumU+=app.getValue().CalcTimeBar();break;}
               case 0: {sumG+=app.getValue().CalcTimeBar();break;}
                case 1: {sumA+=app.getValue().CalcTimeBar();break;}
                case 2:{sumV+=app.getValue().CalcTimeBar();break;}
                case 3: {sumI+=app.getValue().CalcTimeBar();break;}
                case 4: {sumS+=app.getValue().CalcTimeBar();break;}
                case 5: {sumN+=app.getValue().CalcTimeBar();break;}
                case 6: {sumM+=app.getValue().CalcTimeBar();break;}
                case 7: {sumP+=app.getValue().CalcTimeBar();break;}
            }
        }

        UVals.add(new BarEntry(1,sumU));
        SVals.add(new BarEntry(2,sumS));
        AVals.add(new BarEntry(3,sumA));
        GVals.add(new BarEntry(4,sumG));
        IVals.add(new BarEntry(5,sumI));
        NVals.add(new BarEntry(6,sumN));
        PVals.add(new BarEntry(7,sumP));
        MVals.add(new BarEntry(8,sumM));
        VVals.add(new BarEntry(9,sumV));





        ArrayList<BarEntry>  xVals = new ArrayList<BarEntry>();
        BarDataSet Ubardataset = new BarDataSet(UVals,"UNDEFINED");
        BarDataSet Sbardataset = new BarDataSet(SVals,"SOCIAL");
        BarDataSet Abardataset = new BarDataSet(AVals,"AUDIO");
        BarDataSet Gbardataset = new BarDataSet(GVals,"GAME");
        BarDataSet Ibardataset = new BarDataSet(IVals,"IMAGE");
        BarDataSet Nbardataset = new BarDataSet(NVals,"NEWS");
        BarDataSet Pbardataset = new BarDataSet(PVals,"PRODUCTIVITY");
        BarDataSet Vbardataset = new BarDataSet(VVals,"VIDEO");
        BarDataSet Mbardataset = new BarDataSet(MVals,"MAPS");

      //  ArrayList<LegendEntry> legend = new ArrayList<>();

        int color = ContextCompat.getColor(context, R.color.chart_undef);
        Ubardataset.setColors(color);

        color = ContextCompat.getColor(context, R.color.chart_social);
        Sbardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_product);
        Pbardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_games);
        Gbardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_audio);
        Abardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_video);
        Vbardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_image);
        Ibardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_news);
        Nbardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_maps);
        Mbardataset.setColors(color);

        BarData data = new BarData(Ubardataset);
        data.addDataSet(Sbardataset);
        data.addDataSet(Abardataset);
        data.addDataSet(Gbardataset);
        data.addDataSet(Ibardataset);
        data.addDataSet(Nbardataset);
        data.addDataSet(Pbardataset);
        data.addDataSet(Vbardataset);
        data.addDataSet(Mbardataset);

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);


        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
        });

        chart.animateY(1000);
        Cdata.setData(data);
        chart.setData(Cdata);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                List<App> list = null;
                int max = 0;
                YAxis  ya = chart.getAxisLeft();
                ya.removeAllLimitLines();
                switch ((int)e.getX()){
                    case 1:{

                        databaseAccess.open();
                        list = databaseAccess.getAllAppsByCategoty(-1);
                        databaseAccess.close();
                    }break;
                    case 2:{
                        databaseAccess.open();
                        list = databaseAccess.getAllAppsByCategoty(4);
                        databaseAccess.close();
                    }break;
                    case 3:{
                        databaseAccess.open();
                        list = databaseAccess.getAllAppsByCategoty(1);
                        databaseAccess.close();
                    }break;
                    case 4:{
                        databaseAccess.open();
                        list = databaseAccess.getAllAppsByCategoty(0);
                        databaseAccess.close();
                    }break;
                    case 5:{
                        databaseAccess.open();
                        list = databaseAccess.getAllAppsByCategoty(3);
                        databaseAccess.close();
                    }break;
                    case 6:{
                        databaseAccess.open();
                        list = databaseAccess.getAllAppsByCategoty(5);
                        databaseAccess.close();
                    }break;
                    case 7:{
                        databaseAccess.open();
                        list = databaseAccess.getAllAppsByCategoty(7);
                        databaseAccess.close();
                    }break;
                    case 8:{
                        databaseAccess.open();
                        list = databaseAccess.getAllAppsByCategoty(2);
                        databaseAccess.close();
                    }break;
                    case 9:{
                        databaseAccess.open();
                        list = databaseAccess.getAllAppsByCategoty(6);
                        databaseAccess.close();
                    }break;
                }

                Map<Integer, String> limits = new TreeMap<>();
                for (App app: list){

                    int current_limit = app.getLimit_h()*60 + app.getLimit_m();
                    if (max<current_limit)
                        max = current_limit;
                    String value = limits.get(current_limit);
                    if (value!=null){
                            limits.put(current_limit, value + ", " + app.getAppName());
                    }
                    else
                    {
                        limits.put(current_limit,app.getAppName());
                    }
                }

                for (Map.Entry<Integer, String> limit : limits.entrySet()) {
                    String[] str_arr = limit.getValue().split(",");
                    if(str_arr.length<=3)
                      ya.addLimitLine(new LimitLine(limit.getKey(), limit.getValue()));
                    else
                       ya.addLimitLine(new LimitLine(limit.getKey(), str_arr[0]+", "+str_arr[1]+", "+str_arr[2]+", "+str_arr[4]+" +"+(str_arr.length-3)));
                    ya.setAxisMaxValue(max+100);
                }
                chart.invalidate();
            }

            @Override
            public void onNothingSelected() {
                YAxis  ya = chart.getAxisLeft();
                ya.removeAllLimitLines();
                if (pref.isAllLimit()) {
                    ya.addLimitLine(new LimitLine(pref.getLimit_hour()*60 + pref.getLimit_min(), "Total limit"));
                    ya.setAxisMaxValue(pref.getLimit_hour()*60 + pref.getLimit_min()+100);
                }
                chart.invalidate();
            }
        });
        chart.invalidate();

    }

 /*   private LineData generateLineData() {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<Entry>();

        entries.add(new Entry(0, pref.getLimit_hour()*60 + pref.getLimit_min(),"qqq"));
        entries.add(new Entry(9.5f, pref.getLimit_hour()*60 + pref.getLimit_min(),"111"));
        int color = ContextCompat.getColor(context, R.color.colorAccent);


        LineDataSet set = new LineDataSet(entries,"label");
        set.setColors(color);
        set.setLineWidth(2.5f);
        set.setFillColor(color);

        set.setMode(LineDataSet.Mode.LINEAR);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        d.addDataSet(set);

        return d;
    }*/
}
