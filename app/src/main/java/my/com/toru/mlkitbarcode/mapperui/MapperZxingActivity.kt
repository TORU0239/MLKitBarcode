package my.com.toru.mlkitbarcode.mapperui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_custom_scanner.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import my.com.toru.mlkitbarcode.R

class MapperZxingActivity : AppCompatActivity(), ZXingScannerView.ResultHandler{

    private lateinit var scannerView: ZXingScannerView
    private var isFlashOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapper)
        scannerView = findViewById(R.id.mapper_scanner)

        torch.setOnClickListener{
            isFlashOn = !isFlashOn
            setFlashMode(isFlashOn)
        }
    }

    fun setFlashMode(flashOn:Boolean){
        if(flashOn){
            isFlashOn = true
            torch.setImageResource(R.drawable.btn_flash_light_on)

        }
        else{
            isFlashOn = false
            torch.setImageResource(R.drawable.btn_flash_light_off)
        }
        scannerView.flash = isFlashOn
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this@MapperZxingActivity)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }


    override fun handleResult(rawResult: Result?) {
        Log.w("Mapper", "result:: ${rawResult?.text}")
        Toast.makeText(this@MapperZxingActivity, "${rawResult?.text}", Toast.LENGTH_SHORT).show()
        finish()
    }
}