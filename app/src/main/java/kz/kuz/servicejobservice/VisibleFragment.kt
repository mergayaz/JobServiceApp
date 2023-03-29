package kz.kuz.servicejobservice

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.fragment.app.Fragment

// этот класс будет скрывать оповещения при активном экране, здесь мы создаём динамический
// широковещательный приёмник, его можно создать и в манифесте, но нам такой вариант не подходит,
// так как в манифесте он будет принимать все интенты, а нам нужно получать только те,
// что приходят при активном экране, поэтому мы создаём приёмник в коде
abstract class VisibleFragment : Fragment() {
    // VisibleFragment делаем субклассом Fragment, а MainFragment - субклассом VisibleFragment
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(MyService.ACTION_SHOW_NOTIFICATION)
        // чтобы передать объект в IntentFilter, он должен быть создан в коде
        // при этом его значение соответствует фильтру, какой был бы в манифесте, если бы он
        // был создан там: "kz.kuz.fragmentapplication.SHOW_NOTIFICATION"
        activity?.registerReceiver(mOnShowNotification, filter,
                "kz.kuz.fragmentapplication.PRIVATE", null)
        // этот вызов используется для регистрации приёмника, указываем разрешение
        // null означает, что больше никакое другое приложение не сможет отправить наш интент
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(mOnShowNotification)
        // этот вызов используется для отмены приёмника
        // приёмник должен отменяться в методе завершения, соответствующем методу запуска, где он
        // был зарегистрирован (onStart()-onStop(), onActivityCreated()-onActivityDestroyed())
        // однако нужно быть осторожным с onCreate() и onDestroy() при удержании фрагментов
        // метод getActivity() будет возвращать разные значения, если экран был повёрнут
        // при использовании Fragment.onCreate() и Fragment.onDestroy() нужно использовать
        // getActivity().getApplicationContext()
    }

    // пришлось присвоить приёмник переменной экземпляра, так как его нужно использовать
    // и в registerReceiver, и в unregisterReceiver
    private val mOnShowNotification: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i("VisibleFragment", "cancelling notification")
            resultCode = Activity.RESULT_CANCELED
            // получение означает, что пользователь видит приложение, поэтому оповещение отменяется
            // так как нас интересует только сигнал "да/нет", нам достаточно лишь кода результата
            // если требуется получить более сложные данные, можно использовать
            // setResultData(String) или setResultExtras(Bundle), а для передачи всех трёх значений
            // setResult(int, String, Bundle), последующий приёмник эти данные сможет увидеит
            // и изменить
        }
    }
}