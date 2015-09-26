package es.symdev.trafficdata;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

public class TdWidget extends AppWidgetProvider {

    private static final String MyOnClickRefresh = "es.symdev.trafficdata.btnclickrefresh";
    private static final String MyOnClickReset = "es.symdev.trafficdata.btnclickreset";
    private static long mStartRX = TrafficStats.getTotalRxBytes();
    RemoteViews remoteViews;
    AppWidgetManager appWidgetManager;
    ComponentName thisWidget;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        long dwupload = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
        MyVariable.globalVar.setValorInicial(dwupload);
        SharedPreferences prefs = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putLong("valor",MyVariable.globalVar.getValorInicial());
        edit.putBoolean("reset", false);
        edit.commit();


        iniciar(context);
        Log.d("onEnableeeeed", "globalVar: " + dwupload + " TotalRTxBytes();" + (TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()));
        Toast.makeText(context, R.string.press_refresh, Toast.LENGTH_SHORT).show();

    }
    public void iniciar(Context context)
    {
        mStartRX = MyVariable.globalVar.getValorInicial() ;
        Log.d("Iniciar", "globalVar: " + MyVariable.globalVar.getValorInicial() + " TotalRTxBytes();" + (TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()));

        if (TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ||
                TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        }


    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("Updateeeeeeee", "globalVar: " + MyVariable.globalVar.getValorInicial() + " TotalRTxBytes();" + (TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()));

        actualizar(context, appWidgetManager, appWidgetIds);
    }

    public void actualizar(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {

        SharedPreferences prefs1 = context.getSharedPreferences("SETTINGS", 0);
        boolean temp_bool = prefs1.getBoolean("reset", false);
        Log.d("valor",""+temp_bool);

        this.appWidgetManager = appWidgetManager;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
        thisWidget = new ComponentName(context, TdWidget.class);

        //Para el click del boton

        Intent active = new Intent(context,TdWidget.class);
        active.setAction(MyOnClickRefresh);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context,0,active,0);
        remoteViews.setOnClickPendingIntent(R.id.txtRefresh,actionPendingIntent);

        active = new Intent(context,TdWidget.class);
        active.setAction(MyOnClickReset);
        actionPendingIntent = PendingIntent.getBroadcast(context,0,active,0);
        remoteViews.setOnClickPendingIntent(R.id.txtReset,actionPendingIntent);

        long temp_valor;
        if(MyVariable.globalVar.getValorInicial() == 0)
        {
            SharedPreferences prefs = context.getSharedPreferences("SETTINGS", 0);
            temp_valor = prefs.getLong("valor",99);
            Log.d("valor",""+temp_valor);
            MyVariable.globalVar.setValorInicial(temp_valor);

        }
        else
        {
            temp_valor = MyVariable.globalVar.getValorInicial();
        }

        float dwBytes = (TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes())- temp_valor;
        float dwBytesf = dwBytes/1048576;
        String tx = String.format("%.2f", dwBytesf);
        if(temp_bool == true){
            long dwupload = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
            MyVariable.globalVar.setValorInicial(dwupload);
            SharedPreferences prefs = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putLong("valor",MyVariable.globalVar.getValorInicial());
            edit.putBoolean("reset", false);
            edit.commit();
            tx = "0,00";
            remoteViews.setTextViewText(R.id.txtView, tx + " MB");
        }else {
            remoteViews.setTextViewText(R.id.txtView, tx + " MB");
        }
        remoteViews.setTextViewText(R.id.txtRefresh, "Refresh");
        remoteViews.setTextViewText(R.id.txtReset, "Reset");
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

    }


    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);

        if (MyOnClickRefresh.equals(intent.getAction())){
            Log.d("MyOnClickRefresh", "MyOnClickRefresh");
            Intent intent1 = new Intent(context, TdWidget.class);
            intent1.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = {R.xml.td_widget_provider};
            intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            SharedPreferences prefs = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("reset",false);
            edit.commit();
            context.sendBroadcast(intent1);
        }
        else if(MyOnClickReset.equals(intent.getAction())){
            Log.d("MyOnClickReset", "MyOnClickReset");
            Intent intent1 = new Intent(context,TdWidget.class);
            intent1.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = {R.xml.td_widget_provider};
            intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            SharedPreferences prefs = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("reset", true);
            edit.commit();
            context.sendBroadcast(intent1);
        }


    }

    //Metodo para el evento click
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(MyOnClickRefresh);
        intent.setAction(action);
        Log.d("Action", action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }






}
