package my.com.toru.mlkitbarcode

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

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
            if(et_qrcode_text.editableText.toString().isNotBlank()){
                Handler().post{
                    img_generated_qr.setImageBitmap(generateQRCode(et_qrcode_text.editableText.toString()))
                }
            }
            else{
                Toast.makeText(this@MainActivity, "No Text!", Toast.LENGTH_SHORT).show()
            }
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

    private fun generateQRCode(str:String):Bitmap{
        val result = MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 200,200, null)
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height){
            val offset = y * width
            for(x in 0 until width){
                pixels[offset + x] = if(result.get(x,y)){
                    ContextCompat.getColor(this@MainActivity, android.R.color.black)
                }
                else{
                    ContextCompat.getColor(this@MainActivity, android.R.color.white)
                }
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0,200, 0,0, width, height)
        return bitmap
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
                        Log.w("MainActivity", "title:: $title, url: $url")
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(barcode.rawValue)
                        startActivity(intent)
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