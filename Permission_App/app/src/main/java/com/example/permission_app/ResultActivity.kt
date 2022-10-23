package com.example.permission_app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : AppCompatActivity() {

    val date = getCurrentDateTime()
    val stringFiles: MutableList<Any> = mutableListOf()
    val TAG = "ReadWrite"
    var nomeArquivo = ""

    private lateinit var singlePermissionLauncher: ActivityResultLauncher<String>

    override fun onStart() {
        super.onStart()
        requestReadPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        listFiles()




    }

    // METODO #1 para ler arquivos
    private fun readfile() {
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

    fun isExternalStorageReadable(): Boolean {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            ||
            Environment.MEDIA_MOUNTED_READ_ONLY == Environment.getExternalStorageState()
        ) {
            Log.i(TAG, "Pode ler do diretorio externo.")
            return true
        } else {
            Log.i(TAG, "Não pode ler do diretorio externo.")
        }
        return false
    }

    private fun requestReadPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                readFile()
            }
            else -> {
                singlePermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    // METODO #2 para ler arquivos
    fun readFile() {
        if (isExternalStorageReadable()) {
            val endereco =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            nomeArquivo = date.toString("yyyy_MM_dd__HH_mm_ss")

            val path = "$endereco/$nomeArquivo"
            Log.i(TAG, "Lendo do arquivo em")
            Log.i(TAG, "${path}")

            try {
                findViewById<TextView>(R.id.txt_result).text = File(path).readText()
            } catch (e: Exception) {
                Log.i(TAG, e.message!!)
            }
        } else {
            Toast.makeText(
                this,
                "Não foi possível ler do disco",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    // METODO #1 para listar arquivos
    fun listFiles() {
        if (isExternalStorageReadable()) {
            val endereco =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            var listaArquivos = ""
            for (f in endereco.listFiles()) {
                listaArquivos += "${f.name} \n"
            }
            try {
                val msg = "Lista de arquivos da pasta downloads no SD CARD:\n $listaArquivos"
                findViewById<TextView>(R.id.txt_result).text = msg
            } catch (e: Exception) {
                Log.i(TAG, e.message!!)
            }
        } else {
            Toast.makeText(
                this,
                "Não foi possível ler do disco",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    // METODO #2 para listar arquivos
    fun listfiles(){
                val path = this.getExternalFilesDir(null)
        val folder = File(path,"Android_Writer")
        for (file in folder.list()){
            Log.d("Files","$file")
            stringFiles.add(file)

        }
        Log.d("Files", " ${stringFiles}")
        findViewById<TextView>(R.id.txt_result).setText(stringFiles.toString())

    }

    // METODO #1 para abrir arquivos
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