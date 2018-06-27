package my.com.toru.mlkitbarcode

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_web.*
import android.webkit.WebSettings
import android.os.Build



class WebActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun newIntentForWebActivity(ctx: Context, url:String):Intent{
            return Intent(ctx, WebActivity@this::class.java)
                    .putExtra("url", url)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        Log.w("WebActivity", "intent url:: ${intent.getStringExtra("url")}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webview.settings.safeBrowsingEnabled = false
        }

        val settings = webview.settings
        settings.javaScriptEnabled = true
        webview.loadUrl(intent.getStringExtra("url"))
    }
}
