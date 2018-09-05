package my.com.toru.mlkitbarcode

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.android.synthetic.main.activity_custom_scanner.*


class CustomScannerActivity: AppCompatActivity(), DecoratedBarcodeView.TorchListener{


    private lateinit var capture:CaptureManager
    private lateinit var barcodeScannerView:DecoratedBarcodeView
    private var isTorchOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_scanner)

        barcodeScannerView= findViewById(R.id.zxing_barcode_scanner) as DecoratedBarcodeView
        barcodeScannerView.setTorchListener(this)

        capture = CaptureManager(this, barcodeScannerView)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.decode()

        torch.setOnClickListener{
            if(isTorchOn){
                barcodeScannerView.setTorchOff()
            }
            else{
                barcodeScannerView.setTorchOn()
            }
        }
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

    override fun onTorchOn() {
        isTorchOn = true
        torch.setImageResource(R.drawable.btn_flash_light_on)
    }

    override fun onTorchOff() {
        isTorchOn = false
        torch.setImageResource(R.drawable.btn_flash_light_off)
    }
}