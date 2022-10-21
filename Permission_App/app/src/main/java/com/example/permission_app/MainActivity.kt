package com.example.permission_app

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.location.LocationListener
import android.net.Uri
import android.nfc.Tag
import android.os.Build
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.AttributeSet
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.jar.Manifest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    val CREATE_FILE = 0
    var lat: Double = 0.0
    var long: Double = 0.0
    val date = getCurrentDateTime()
    val dateInString = date.toString("yyyy/MM/dd  HH:mm:ss")
    val onlydate = date.toString("yyyy/MM/dd")
    var permissionCode = 0


//    val btn_save_doc = findViewById<Button>(R.id.btn_save_loc)
//    val btn_save_file = findViewById<Button>(R.id.btn_save_file)
//    val btn_result = findViewById<Button>(R.id.btn_result)


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onStart() {
        super.onStart()

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),101)

        }

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_save_loc).isEnabled = false
        findViewById<Button>(R.id.btn_save_file).isEnabled = false
        findViewById<Button>(R.id.btn_result).isEnabled = false


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        findViewById<Button>(R.id.btn_get_location).setOnClickListener {
            getLastLocation()

            findViewById<Button>(R.id.btn_save_loc).isEnabled = true
            findViewById<Button>(R.id.btn_save_file).isEnabled = true
        }

        findViewById<Button>(R.id.btn_save_loc).setOnClickListener {

            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),101)

            }

            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
                putExtra(Intent.EXTRA_TITLE, "${dateInString}")
                putExtra(DocumentsContract.EXTRA_INITIAL_URI,"")

            }
            startActivityForResult(intent,CREATE_FILE)
            findViewById<Button>(R.id.btn_result).isEnabled = true
        }

        findViewById<Button>(R.id.btn_result).setOnClickListener {
            val intent = Intent(this,ResultActivity::class.java)
            startActivity(intent)
        }


        findViewById<Button>(R.id.btn_save_file).setOnClickListener {
            createFile()
            findViewById<Button>(R.id.btn_result).isEnabled = true


        }






    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if(requestCode == CREATE_FILE && resultCode == RESULT_OK){
            val url = data!!.data
            try {
                val outputStream = this.contentResolver.openOutputStream(url!!)
                outputStream?.write("Latitude:${lat}\nLongitude:${long}".toByteArray())
                outputStream?.close()
            } catch (e:Exception){
                print(e.localizedMessage)
            }
        }


    }

    private fun getLastLocation() {
        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            return
        }
     task.addOnSuccessListener {
         if(it != null){
             lat = it.latitude
             long = it.longitude
             Toast.makeText(applicationContext,"${it.latitude} ${it.longitude}",Toast.LENGTH_SHORT).show()
         }
     }


    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun createDeleteFile() {
        val file = File(getExternalFilesDir(null), "tp1.txt")
        val os: OutputStream = FileOutputStream(file)
        os.write("Data:${onlydate}\nLatitude:${lat}\nLongitude:${long}".toByteArray())
        os.close()



    }


    private fun createFile(){

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ){

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),101)
            return
        }


        val path = this.getExternalFilesDir(null)
        val folder = File(path,"Android_Writer")
        folder.mkdirs()
        val file = File(folder,"${date.toString("yyyy_MM_dd__HH_mm_ss")}.crd")
        file.appendText("Data:${onlydate}\nLatitude:${lat}\nLongitude:${long}\n")
        Toast.makeText(this,"Arquivo Salvo",Toast.LENGTH_SHORT).show()



    }


}