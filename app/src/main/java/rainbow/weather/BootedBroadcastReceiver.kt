package rainbow.weather

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action != Intent.ACTION_BOOT_COMPLETED) return
        context!!.startForegroundService(Intent(context, AlertService::class.java))
    }
}