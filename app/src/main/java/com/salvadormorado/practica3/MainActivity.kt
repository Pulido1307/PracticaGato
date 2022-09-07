package com.salvadormorado.practica3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        val editText_Mensaje: EditText = findViewById(R.id.editText_Mensaje)
        val button_Log: Button = findViewById(R.id.button_Log)
        val button_SnackBar: Button = findViewById(R.id.button_SnackBar)
        val button_Toast: Button = findViewById(R.id.button_Toast)
        val button_PasarGato: Button = findViewById(R.id.button_PasarGato)

        button_Log.setOnClickListener({
            //Log.d("Mensaje", editText_Mensaje.text.toString())
            Log.d("Mensaje", "Soy un mensaje por consola")
        })
        button_SnackBar.setOnClickListener({
            //val snack = Snackbar.make(it, editText_Mensaje.text, Snackbar.LENGTH_SHORT)
            val snack = Snackbar.make(it, "Hola soy un snackbar", Snackbar.LENGTH_SHORT)
            snack.show()
        })
        button_Toast.setOnClickListener({
            val mensaje = "Hola soy un toast"
            val duracion = Toast.LENGTH_SHORT
            val toast = Toast.makeText(this, mensaje, duracion)
            toast.show()
            //Toast.makeText(applicationContext, "Hola soy un toast", Toast.LENGTH_SHORT).show()
        })
        button_PasarGato.setOnClickListener({
            var mensaje = editText_Mensaje.text
            var intent:Intent = Intent(applicationContext, GatoActivity::class.java).apply {
                putExtra("mensaje", mensaje)
            }
            startActivity(intent)
        })
    }

    fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            this.requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
            return false
        }
    }

}