package fivemoreminutes.a5moreminutes.Charts;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fivemoreminutes.a5moreminutes.Entity.App;
import fivemoreminutes.a5moreminutes.R;
import fivemoreminutes.a5moreminutes.Settings.SharPreferences;

public class CombinedChartsSingle {
    public CombinedChart chart;
    public  Context context;


    public CombinedChartsSingle(CombinedChart chart, Context context) {
        this.chart = chart;
        this.context = context;
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

    public void mainLoad(Map<Integer,App> map){

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
        XAxis xa = chart.getXAxis();
        xa.setDrawGridLines(false);
        xa.setDrawAxisLine(false);
        xa.setDrawGridLinesBehindData(true);
        xa.setDrawLimitLinesBehindData(true);
       // xa.setDrawLabels(false);
        xa.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xa.setAxisMinimum(0.5f);
        xa.setGranularity(0.5f);
        xa.setAxisMaximum(7.5f);

        CombinedData Cdata = new CombinedData();

        ArrayList<BarEntry> MonV = new ArrayList<BarEntry>();
        ArrayList<BarEntry> TueV = new ArrayList<BarEntry>();
        ArrayList<BarEntry> WenV = new ArrayList<BarEntry>();
        ArrayList<BarEntry> ThuV = new ArrayList<BarEntry>();
        ArrayList<BarEntry> FriV = new ArrayList<BarEntry>();
        ArrayList<BarEntry> SatV = new ArrayList<BarEntry>();
        ArrayList<BarEntry> SunV = new ArrayList<BarEntry>();
        float sumM = 0f,sumT = 0f,sumW = 0f,sumTh = 0f,sumF = 0f,sumSa = 0f,sumSu = 0f;
        List<App> list = new ArrayList<>();
        for (Map.Entry<Integer, App> app : map.entrySet()) {
            switch (app.getKey()){
               case 0: {sumM+=app.getValue().CalcTimeBar();break;}
                case 1: {sumT+=app.getValue().CalcTimeBar();break;}
                case 2:{sumW+=app.getValue().CalcTimeBar();break;}
                case 3: {sumTh+=app.getValue().CalcTimeBar();break;}
                case 4: {sumF+=app.getValue().CalcTimeBar();break;}
                case 5: {sumSa+=app.getValue().CalcTimeBar();break;}
                case 6: {sumSu+=app.getValue().CalcTimeBar();break;}

            }
            list.add(app.getValue());
        }

        MonV.add(new BarEntry(1,sumM));
        TueV.add(new BarEntry(2,sumT));
        WenV.add(new BarEntry(3,sumW));
        ThuV.add(new BarEntry(4,sumTh));
        FriV.add(new BarEntry(5,sumF));
        SatV.add(new BarEntry(6,sumSa));
        SunV.add(new BarEntry(7,sumSu));

        final ArrayList<String> xLabel = new ArrayList<>();
        xLabel.add("");
        for (int i=0;i<7;i++)
            xLabel.add(list.get(i).getCategory());


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int)value);
            }
        });

        BarDataSet Mbardataset = new BarDataSet(MonV,list.get(0).getCategory());
        BarDataSet Tbardataset = new BarDataSet(TueV,list.get(1).getCategory());
        BarDataSet Wbardataset = new BarDataSet(WenV,list.get(2).getCategory());
        BarDataSet Thbardataset = new BarDataSet(ThuV,list.get(3).getCategory());
        BarDataSet Fbardataset = new BarDataSet(FriV,list.get(4).getCategory());
        BarDataSet Sabardataset = new BarDataSet(SatV,list.get(5).getCategory());
        BarDataSet Subardataset = new BarDataSet(SunV,list.get(6).getCategory());

        int color = ContextCompat.getColor(context, R.color.chart_maps);
        Mbardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_social);
        Tbardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_video);
        Wbardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_news);
        Thbardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_audio);
        Fbardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_product);
        Sabardataset.setColors(color);
        color = ContextCompat.getColor(context, R.color.chart_image);
        Subardataset.setColors(color);

        BarData data = new BarData(Mbardataset);
        data.addDataSet(Tbardataset);
        data.addDataSet(Wbardataset);
        data.addDataSet(Thbardataset);
        data.addDataSet(Fbardataset);
        data.addDataSet(Sabardataset);
        data.addDataSet(Subardataset);

       /* Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);*/
        chart.getLegend().setEnabled(false);

        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
        });

        chart.animateY(1000);
        Cdata.setData(data);
        chart.setData(Cdata);
        chart.invalidate();

    }

}
