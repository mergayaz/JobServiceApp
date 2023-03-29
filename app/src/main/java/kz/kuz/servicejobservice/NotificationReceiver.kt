package kz.kuz.servicejobservice

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.core.app.NotificationManagerCompat

// данный получатель используется для получения широковещательного интента с уведомлением
// из MyService и его отправки к NotificationManager, но только в том случае, если приложение
// не активно. При активном приложении VisibleFragment перехватывает интент и отменяет их
// для того, чтобы NotificationReceiver получил интент позже всех, ему в манифесте присваивается
// низкий приоритет
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("NotificationReceiver", "received result: $resultCode")
        if (resultCode != Activity.RESULT_OK) {
            // Активность переднего плана отменила рассылку
            return
        }
        val requestCode = intent.getIntExtra("REQUEST_CODE", 0)
        val notification = intent.getParcelableExtra<Parcelable>("NOTIFICATION") as Notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(requestCode, notification)
        // отправляем уведомление к NotificationManager, полученное по широковещательному интенту
        // от MyService, при условии, что его не изменил VisibleFragment
    }
}