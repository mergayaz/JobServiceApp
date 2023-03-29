package kz.kuz.servicejobservice

import android.R
import android.app.*
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kz.kuz.servicejobservice.MainActivity.Companion.newIntent
import java.util.concurrent.Executors

class MyService : JobService() {
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onStartJob(params: JobParameters): Boolean {
        // срабатывает, когда сервис выполняется
        val executorService = Executors.newSingleThreadExecutor()
        executorService.execute(BackgroundTask())
        // запускаем сервис в параллельном потоке
        return false
    }

    override fun onStopJob(params: JobParameters): Boolean {
        // срабатывает, когда перестают выполняться условия существования данного сервиса
        // либо когда сервис отключается
        return false
    }

    private inner class BackgroundTask : Runnable {
        @RequiresApi(api = Build.VERSION_CODES.O) // для оповещений нужен API 26
        override fun run() {
            val i = newIntent(applicationContext)
            val pi = PendingIntent.getActivity(applicationContext, 0, i,0)
            // создаём notification, всплывающее оповещение

            // создаём ChannelId
            val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val id = "notification1" // id of the channel
            val name: CharSequence = "ChannelName" // user-visible name of the channel
            val description = "ChannelDescription" // user-visible description of the channel
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)
            mChannel.description = description // configure the notification channel
            mChannel.enableLights(true)
            // sets the notification light color for notifications posted to this channel,
            // if the device supports this feature
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mNotificationManager.createNotificationChannel(mChannel)
            val notification = NotificationCompat.Builder(applicationContext,
                    "notification1")
                    .setSmallIcon(R.drawable.ic_menu_report_image)
                    .setContentTitle("contentTitle")
                    .setContentText("ContentText")
                    .setOngoing(true) // без этого флага оповещение не показывается на Samsung
                    .setContentIntent(pi) // pi будет вызываться при нажатии на оповещении
                    .setAutoCancel(true) // оповещение будет удаляться после нажатия
                    .build()
//            val notificationManager = NotificationManagerCompat.from(
//                    applicationContext)
//            notificationManager.notify(0, notification)
            // идентификатор оповещения уникальный в пределах приложения
            // если будет отправлено оповещение с тем же идентификатором, оно заменит предыдущее
            // так реализуются индикаторы прогресса и другие динамические визуальные эффекты
//            sendBroadcast(Intent(ACTION_SHOW_NOTIFICATION),
//                    "kz.kuz.fragmentapplication.PRIVATE")
            // отправляем широковещательный интент (самому себе), в параметре указываем разрешение
            // теперь любое приложение сможет получить этот интент, только если укажет такое же
            // разрешение

            // вместо отправки уведомления NotificationManager, отправляем его широковещательным
            // интентом, чтобы его поймал NotificationReceiver
            ShowBackgroundNotification(0, notification)
        }

        private fun ShowBackgroundNotification(requestCode: Int, notification: Notification) {
            val i = Intent(ACTION_SHOW_NOTIFICATION)
            i.putExtra("REQUEST_CODE", requestCode)
            i.putExtra("NOTIFICATION", notification)
            sendOrderedBroadcast(i, "kz.kuz.fragmentapplication.PRIVATE",
                    null, null, Activity.RESULT_OK, null,
                    null)
            // интент зашифрован ключом, указанным в манифесте, чтобы его могли получить только
            // получатели из нашего приложения
        }
    }

    companion object {
        const val ACTION_SHOW_NOTIFICATION = "kz.kuz.fragmentapplication.SHOW_NOTIFICATION"
    }
}