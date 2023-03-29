package kz.kuz.servicejobservice

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi

// устанавливаем сервис с использованием библиотеки JobService (класс MyService)
// в манифест добавлен сервис, а также разрешение на BIND_JOB_SERVICE
// в коде используются инструменты, добавденные в API 24, поэтому строка ниже
@RequiresApi(api = Build.VERSION_CODES.N)
class MainFragment : VisibleFragment() {
    // MainFragment делаем субклассом VisibleFragment, а VisibleFragment - субклассом Fragment
    private var scheduler // инициируем планировщик
            : JobScheduler? = null
    private var jobInfo // инициируем сервис
            : JobInfo? = null
    private var serviceScheduled // переменная для сохранения статуса сервиса
            = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // создаём в меню кнопку, которая будет запускать и отключать сервис
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        activity?.setTitle(R.string.toolbar_title)
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        scheduler = context?.getSystemService(
                Context.JOB_SCHEDULER_SERVICE) as JobScheduler // создаём планировщик
        jobInfo = JobInfo.Builder(1, ComponentName(context!!,  // создаём сервис
                MyService::class.java))
                .setPeriodic((1000 * 60 * 15).toLong(), (1000 * 60 * 5).toLong()) // .setPeriodic устанавливает периодичность сервиса
                // минимальный интервал должен быть 15 мин (как здесь), минимальный флекс 5 мин
                // сервис будет исполняться один раз в 15 мин (+/- 5 мин), на усмотрение Android
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                .setRequiresDeviceIdle(true)
//                .setRequiresCharging(true)
                .build()
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // определяет реакцию на нажатие меню
        serviceScheduled = scheduler?.getPendingJob(1) != null
        return when (item.itemId) {
            R.id.menu_item -> {
                if (serviceScheduled) {
                    scheduler?.cancel(1)
                    MyPreferences.setAlarmOn(context, false)
                } else {
                    scheduler?.schedule(jobInfo!!)
                    MyPreferences.setAlarmOn(context, true)
                }
                activity?.invalidateOptionsMenu() // обновление меню
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // создание меню
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_main, menu)
        serviceScheduled = scheduler!!.getPendingJob(1) != null
        // проверяем, установлен ли сервис с id=1, в зависимости от этого устанавливаем надпись меню
        if (serviceScheduled) {
            menu.findItem(R.id.menu_item).title = "Stop Service"
        } else {
            menu.findItem(R.id.menu_item).title = "Start Service"
        }
    }
}