package com.jyodroid.barcodescanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.jyodroid.livebarcodescan.LiveBarcodeScanningActivity
import com.jyodroid.livebarcodescan.barcodedetection.BarcodeResultFragment

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.canonicalName

    private var barcodeText: TextView? = null

    private val onErrorDialog by lazy {
        this?.let {
            AlertDialog.Builder(it).apply {
                setTitle("Error")
                setCancelable(true)
                setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
            }.create()
        }
    }

    private val starScanForResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val barcodeValue =
                    it.data?.getStringExtra(BarcodeResultFragment.BARCODE_VALUE_EXTRA)
                if (barcodeValue.isNullOrBlank()) {
                    Log.e(TAG, "onScanResults result empty or null")
                    onErrorDialog?.apply {
                        setMessage("No barcode found")
                        show()
                    }
                } else {
                    Log.d(TAG, "barcode found = $barcodeValue")
                    barcodeText?.text = "Barcode value = $barcodeValue"
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        barcodeText = findViewById(R.id.barcode_text)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
                this.requestPermissions(arrayOf(Manifest.permission.CAMERA), 3)
            } else {
                starScanForResult.launch(Intent(this, LiveBarcodeScanningActivity::class.java))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == -1) {
            Log.e(TAG, "onRequestPermissionsResult no camera permissions granted $grantResults")
            onErrorDialog.apply {
                setMessage("No camera permission")
                show()
            }
        } else {
            starScanForResult.launch(Intent(this, LiveBarcodeScanningActivity::class.java))
        }
    }
}