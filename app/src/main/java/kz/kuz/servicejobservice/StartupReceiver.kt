package kz.kuz.servicejobservice

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import kz.kuz.servicejobservice.MyPreferences.isAlarmOn
import kz.kuz.servicejobservice.MyService

class StartupReceiver : BroadcastReceiver() {
    // BroadcastReceiver - широковещательный приёмник, получающий интенты
    // его также нужно зарегистрировать в манифесте
    // StartupReceiver будет просыпаться при каждой отправке интента BOOT_COMPLETED
    // (даже если приложение не выполняется)
    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onReceive(context: Context, intent: Intent) {
        // этот метод будет вызван при получении интента, после чего StartupReceiver прекратит своё
        // существование, а приложение закроется, так как сразу после загрузки оно закрыто
        // принимается Context данного приложения
        val isOn = isAlarmOn(context)
        // заново устанавливаем сервис, если в хранилище сохранено isAlarmOn=true
        if (isOn) {
            val scheduler = context.getSystemService(
                    Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val jobInfo = JobInfo.Builder(1, ComponentName(context,
                    MyService::class.java))
                    .setPeriodic((1000 * 60 * 15).toLong(), (1000 * 60 * 5).toLong())
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                    .setRequiresDeviceIdle(true)
//                    .setRequiresCharging(true)
                    .build()
            scheduler.schedule(jobInfo)
        }
    }
}