package com.example.permission_app

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : AppCompatActivity() {

    val date = getCurrentDateTime()
    val dateInString = date.toString("yyyy/MM/dd  HH:mm:ss")
    val stringFiles: MutableList<Any> = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),101)

        }

//        val path = Environment.getExternalStorageDirectory().toString() + "/Download"
//        Log.d("Files", "Path: $path")
//        val directory = File(path)
//        val files = directory.listFiles()
//        Log.d("Files", "Size: " + files.size)
//        for (i in files.indices) {
//            Log.d("Files", "FileName:" + files[i].name)
//        }

        val path = this.getExternalFilesDir(null)
        val folder = File(path,"Android_Writer")
        for (file in folder.list()){
            Log.d("Files","$file")
            stringFiles.add(file)

        }
        Log.d("Files", " ${stringFiles}")
        findViewById<TextView>(R.id.txt_result).setText(stringFiles.toString())




    }


    private fun readFile() {
        val file = File(getExternalFilesDir(null), "tp1.txt")
        if(!file.exists()) {
            Toast.makeText(this@ResultActivity,
                "Arquivo não encontrado",
                Toast.LENGTH_SHORT).show()
            return
        }
        val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                text.append('\n')
            }
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        findViewById<TextView>(R.id.txt_result).setText(text)
        Toast.makeText(this@ResultActivity,
            text.toString(),
            Toast.LENGTH_SHORT).show()
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun openFile(){

        val path = this.getExternalFilesDir(null)
        val folder = File(path,"Android_Writer")
        folder.mkdirs()
        val file = File(folder,"writer_test.txt")

        if(file.exists()){
            val inputAsString = FileInputStream(file).bufferedReader().use { it.readText() }
            findViewById<TextView>(R.id.txt_result).setText(inputAsString)
        }else{
            findViewById<TextView>(R.id.txt_result).setText("Lista Vazia")
        }

    }
}