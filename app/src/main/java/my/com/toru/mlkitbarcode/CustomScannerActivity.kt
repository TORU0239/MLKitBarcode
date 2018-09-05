package my.com.toru.mlkitbarcode

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView



class CustomScannerActivity: AppCompatActivity(), DecoratedBarcodeView.TorchListener{
    override fun onTorchOn() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTorchOff() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var capture:CaptureManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_scanner)

        val barcodeScannerView = findViewById(R.id.zxing_barcode_scanner) as DecoratedBarcodeView
        barcodeScannerView.setTorchListener(this)

        capture = CaptureManager(this, barcodeScannerView)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.decode()
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }
}