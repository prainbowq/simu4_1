package rainbow.weather

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rainbow.weather.data.AssetsRepository
import rainbow.weather.data.SharedPreferencesRepository
import rainbow.weather.data.WeatherRepository

class AlertService : Service() {
    private lateinit var job: Job

    override fun onCreate() {
        super.onCreate()
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val permanenceChannel =
            NotificationChannel("permanence", "常駐", NotificationManager.IMPORTANCE_NONE)
        manager.createNotificationChannel(permanenceChannel)
        val permanenceNotification = Notification.Builder(this, permanenceChannel.id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("警報功能運作中")
            .build()
        startForeground(1, permanenceNotification)
        val alertChannel =
            NotificationChannel("alert", "警報", NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(alertChannel)
        job = MainScope().launch {
            val weatherRepository = WeatherRepository(AssetsRepository(this@AlertService))
            val sharedPreferencesRepository = SharedPreferencesRepository(this@AlertService)
            while (true) {
                val codes = sharedPreferencesRepository.getAlertCodes()
                val alerts = weatherRepository.getAlerts()
                alerts.forEach {
                    if ("${it.hashCode()}" !in codes) {
                        val alertNotification =
                            Notification.Builder(this@AlertService, alertChannel.id)
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle(
                                    "【${it.description}・${it.issueTime.toAlertString()}】"
                                )
                                .setContentText("${it.content}(~ ${it.endTime.toAlertString()})")
                                .build()
                        manager.notify(0, alertNotification)
                    }
                }
                sharedPreferencesRepository.setAlertsCodes(
                    alerts
                        .map { "${it.hashCode()}" }
                        .toSet()
                )
                delay(60000)
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}