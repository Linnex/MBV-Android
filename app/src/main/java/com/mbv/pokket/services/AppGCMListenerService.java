package com.mbv.pokket.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.mbv.pokket.ActivityLoanDetails;
import com.mbv.pokket.R;

/**
 * Created by arindamnath on 06/04/16.
 */
public class AppGCMListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Bundle notification = data.getBundle("notification");
        String notificationTitle = notification.getString("title");
        String notificationBody = notification.getString("body");
        String loanId = data.getString("loanId");
        sendNotification(notificationTitle, notificationBody, Long.valueOf(loanId));
    }

    private void sendNotification(String title, String body, Long loanId) {
        Intent intent = new Intent(this, ActivityLoanDetails.class);
        intent.putExtra("loanId", loanId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_gavel_white_48dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher))
                .setContentTitle(title)
                .setContentText(body)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}