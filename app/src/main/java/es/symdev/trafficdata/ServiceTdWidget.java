package es.symdev.trafficdata;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class ServiceTdWidget extends Service{

    private static long mStartRX = 0;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        for (int widgetId : allWidgetIds) {

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new MyTime(this.getApplicationContext(), appWidgetManager), 1, 1000);


        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyTime extends TimerTask {
        RemoteViews remoteViews;
        AppWidgetManager appWidgetManager;
        ComponentName thisWidget;
        DateFormat format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());


        ConnectivityManager connManager;
        NetworkInfo mWifi;

        public MyTime(Context context, AppWidgetManager appWidgetManager){
            this.appWidgetManager = appWidgetManager;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
            thisWidget = new ComponentName(context, TdWidget.class);

            connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }


        @Override
        public void run() {
            try {
                float dwBytes = TrafficStats.getTotalRxBytes() - mStartRX;
                float dwBytesf = dwBytes / 1048576;
                Log.d("RUUUUUUNmStartRX", "RUUUUUUNmStartRX: " + mStartRX);

                //remoteViews.setTextViewText(R.id.txtshow, "MB Dwn= " + String.format("%.2f", dwBytesf));
                appWidgetManager.updateAppWidget(thisWidget, remoteViews);

            }
            catch(Exception ex){
                Log.e("ERROR RUN", "----->>>>" + ex.getMessage());
            }

        }
    }//fin mytime


}
