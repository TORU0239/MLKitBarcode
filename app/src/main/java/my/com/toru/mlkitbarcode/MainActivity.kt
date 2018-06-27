package my.com.toru.mlkitbarcode

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        @JvmStatic
        private val REQUESTCODE_BARCODE = 1001
    }

    private lateinit var qrScan:IntentIntegrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        qrScan = IntentIntegrator(this@MainActivity)
        with(qrScan){
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            setBeepEnabled(false)
            setPrompt("Scan a QR Code!!")
            setBarcodeImageEnabled(true)
            setOrientationLocked(true)
        }
        btn_for_scan_with_zxing.setOnClickListener {
            qrScan.initiateScan()
        }

        btn_for_scan_with_vision.setOnClickListener {

        }

        btn_for_scan.setOnClickListener {
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUESTCODE_BARCODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUESTCODE_BARCODE->{
                if(resultCode == Activity.RESULT_OK){
                    val bitmap:Bitmap = data!!.extras["data"] as Bitmap
                    barcodeRecognition(bitmap)
                }
            }
            IntentIntegrator.REQUEST_CODE->{
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                result.contents?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun barcodeRecognition(bitmap:Bitmap){
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build()
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val barcodeDetector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)
        barcodeDetector.detectInImage(image).addOnSuccessListener {
            for(barcode in it){
                val bounds:Rect? = barcode.boundingBox
                val corners:Array<Point>? = barcode.cornerPoints
                val rawValue:String? = barcode.rawValue

                when(barcode.valueType){
                    FirebaseVisionBarcode.TYPE_WIFI ->{
                        val ssid = barcode.wifi?.ssid
                        val password = barcode.wifi?.password
                        val type = barcode.wifi?.encryptionType

                        Log.w("MainActivity", "ssid:: $ssid, password: $password, type: $type")
                    }
                    FirebaseVisionBarcode.TYPE_URL->{
                        val title = barcode.url?.title
                        val url = barcode.url?.url
                        val stringBuilder = StringBuilder()
                        result_text_for_qr.text = stringBuilder.append("msg::").append(barcode.rawValue).toString()
                        Log.w("MainActivity", "title:: $title, url: $url")
                        val intent = Intent(MainActivity@this, WebActivity::class.java)
                                    .putExtra("url", barcode.rawValue)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent)

//                        val intent = Intent(Intent.ACTION_VIEW)
//                        intent.data = Uri.parse(barcode.rawValue)
//                        startActivity(intent)
                    }
                    FirebaseVisionBarcode.TYPE_TEXT->{
                        val stringBuilder = StringBuilder()
                        Toast.makeText(MainActivity@this, stringBuilder.append("msg::").append(barcode.displayValue).toString(), Toast.LENGTH_SHORT)
                                .show()
                    }

                    FirebaseVisionBarcode.TYPE_PHONE->{
                        val stringBuilder = StringBuilder()
                        Toast.makeText(MainActivity@this, stringBuilder.append("telephone::").append(barcode.phone?.number).toString(), Toast.LENGTH_SHORT)
                                .show()
                    }

                    else-> Log.w("MainActivity", "WTF? ${barcode.valueType}")

                }

            }
        }
        .addOnFailureListener {
            it.printStackTrace()
        }
    }
}