package my.com.toru.mlkitbarcode

import android.app.Application
import com.google.firebase.FirebaseApp

class BarCodeApp:Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}