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
import android.util.Log;
import android.widget.Toast;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

public class TdWidgetBck extends AppWidgetProvider {

    private static final String MyOnClick = "es.symdev.trafficdata.btnclick";
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

        this.appWidgetManager = appWidgetManager;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
        thisWidget = new ComponentName(context, TdWidget.class);

        //Para el click del boton
        remoteViews.setOnClickPendingIntent(R.id.txtRefresh, getPendingSelfIntent(context, MyOnClick));
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



        remoteViews.setImageViewBitmap(R.id.txtView, buildUpdate(tx + "MB", context, false));
        remoteViews.setImageViewBitmap(R.id.txtRefresh, buildUpdate("Refresh", context, false));
        remoteViews.setImageViewBitmap(R.id.txtReset, buildUpdate("Reset", context, false));
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

    }

    public Bitmap buildUpdate(String text,Context context,boolean click)
    {
        Bitmap myBitmap;
        Typeface mytypeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(),"QuicksandR.ttf");

        if(text.equals("Refresh") || text.equals("Reset")){
            myBitmap = Bitmap.createBitmap(200, 80, Bitmap.Config.ARGB_4444);
            //myBitmap = Bitmap.createBitmap(300, 100, Bitmap.Config.ARGB_4444);
        }
        else {
            myBitmap = Bitmap.createBitmap(400, 80, Bitmap.Config.ARGB_4444);
            //myBitmap = Bitmap.createBitmap(300, 100, Bitmap.Config.ARGB_4444);
        }

        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        if(click ==true)
        {
            paint.setTypeface(Typeface.create(mytypeface, Typeface.BOLD));
        }else
        {
            paint.setTypeface(mytypeface);
        }
        paint.setTypeface(mytypeface);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);
        if(text.equals("Refresh")){
            myCanvas.drawText(text,80, 50, paint);
        }
        else if(text.equals("Reset")){
            myCanvas.drawText(text,120, 50, paint);
        }
        else {
            myCanvas.drawText(text,200, 50, paint);
        }

        return myBitmap;
    }

    //Metodo para el evento click
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(MyOnClick);
        intent.setAction(action);
        Log.d("Pending","Pending");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);

        if (MyOnClick.equals(intent.getAction())){
            Log.d("MyOnClick","MyOnClick");
            Intent intent1 = new Intent(context, TdWidget.class);
            intent1.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = {R.xml.td_widget_provider};
            intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent1);
        }


    }





}
