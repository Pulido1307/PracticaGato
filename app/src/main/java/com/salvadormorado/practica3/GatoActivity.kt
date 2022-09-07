package com.salvadormorado.practica3

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class GatoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var button_00: Button
    private lateinit var button_01: Button
    private lateinit var button_02: Button
    private lateinit var button_10: Button
    private lateinit var button_11: Button
    private lateinit var button_12: Button
    private lateinit var button_20: Button
    private lateinit var button_21: Button
    private lateinit var button_22: Button
    private lateinit var button_Reiniciar: Button
    private lateinit var textView_PuntuacionTotal: TextView
    private lateinit var textView_PuntuacionX: TextView
    private lateinit var textView4_PuntuacionO: TextView
    private lateinit var textView_Empates: TextView
    private lateinit var textView_Tiempo: TextView

    private lateinit var progressBar: ProgressBar
    private var gato = Array(3) { Array<String?>(3) { null } }
    private var contadorMovimientos: Int = 0
    private var puntuacionX: Int = 0
    private var puntuacionO: Int = 0
    private var empate: Int = 0
    private var myCountDownTimer: MyCountDownTimer? = null

    private var flagAux: Boolean? = null
    private var turno: Boolean? = null //False = Turno de jugador -  True = Turno de máquina
    private var seleccionar: Boolean? = null
    private var contador = 0
    private var juegoTerminado: Boolean? = null
    private var progressAsyncTask: ProgressAsyncTask? = null
    private val host: String = "https://pruebapulido.000webhostapp.com/Servicios/"
    private var posicionesEnviar: String = "0,1,2,3,4,5,6,7,8"
    private var progressDialog:ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gato)
        title = "¡Juego del gato! \uD83D\uDC08"

        setListenersButtons()

        textView_Tiempo = findViewById(R.id.textView_Tiempo)
        textView_PuntuacionTotal = findViewById(R.id.textView_PuntuacionTotal)
        textView_PuntuacionX = findViewById(R.id.textView_PuntuacionX)
        textView4_PuntuacionO = findViewById(R.id.textView4_PuntuacionO)
        textView_Empates = findViewById(R.id.textView_Empates)
        progressBar = findViewById(R.id.progressBar)
        progressBar.progress = contador

        flagAux = true //Se inicia con O
        seleccionar = false
        turno = false
        juegoTerminado = false

        progressDialog = ProgressDialog(this@GatoActivity)
        progressDialog!!.setTitle("Espera por favor.")
        progressDialog!!.setMessage("Obteniendo posición...")

        myCountDownTimer = MyCountDownTimer(5000, 1000)
        myCountDownTimer!!.start()
    }

    fun setListenersButtons() {
        button_00 = findViewById(R.id.button_00)
        button_01 = findViewById(R.id.button_01)
        button_02 = findViewById(R.id.button_02)
        button_10 = findViewById(R.id.button_10)
        button_11 = findViewById(R.id.button_11)
        button_12 = findViewById(R.id.button_12)
        button_20 = findViewById(R.id.button_20)
        button_21 = findViewById(R.id.button_21)
        button_22 = findViewById(R.id.button_22)
        button_Reiniciar = findViewById(R.id.button_Reiniciar)

        button_00.setOnClickListener(this)
        button_01.setOnClickListener(this)
        button_02.setOnClickListener(this)
        button_10.setOnClickListener(this)
        button_11.setOnClickListener(this)
        button_12.setOnClickListener(this)
        button_20.setOnClickListener(this)
        button_21.setOnClickListener(this)
        button_22.setOnClickListener(this)
        button_Reiniciar.setOnClickListener(this)
    }

    fun validateTurn(i: Int, j: Int, button: Button) {
        if (!turno!!) { //turno de jugador
            gato[i][j] = setLetterButton(button)
            validateWinner()
            turno = true
            seleccionar = true
            myCountDownTimer!!.onFinish()
        }
    }

    override fun onClick(v: View?) {
        println(v!!.id)
        when (v!!.id) {
            R.id.button_00 -> {
                validateTurn(0, 0, button_00)
            }
            R.id.button_01 -> {
                validateTurn(0, 1, button_01)
            }
            R.id.button_02 -> {
                validateTurn(0, 2, button_02)
            }
            R.id.button_10 -> {
                validateTurn(1, 0, button_10)
            }
            R.id.button_11 -> {
                validateTurn(1, 1, button_11)
            }
            R.id.button_12 -> {
                validateTurn(1, 2, button_12)
            }
            R.id.button_20 -> {
                validateTurn(2, 0, button_20)
            }
            R.id.button_21 -> {
                validateTurn(2, 1, button_21)
            }
            R.id.button_22 -> {
                validateTurn(2, 2, button_22)
            }
            R.id.button_Reiniciar -> {
                restart()
            }
        }
    }

    fun setLetterButton(button: Button): String {
        var opc = ""
        if (!flagAux!!) {
            button.text = "X"
            flagAux = true
            opc = "X"
        } else {
            button.text = "O"
            flagAux = false
            opc = "O"
        }
        button.isEnabled = false
        contadorMovimientos++
        return opc
    }

    fun fillPosition() {
        posicionesEnviar = ""

        if (gato[0][0] == null)
            posicionesEnviar += "0,"
        if (gato[0][1] == null)
            posicionesEnviar += "1,"
        if (gato[0][2] == null)
            posicionesEnviar += "2,"
        if (gato[1][0] == null)
            posicionesEnviar += "3,"
        if (gato[1][1] == null)
            posicionesEnviar += "4,"
        if (gato[1][2] == null)
            posicionesEnviar += "5,"
        if (gato[2][0] == null)
            posicionesEnviar += "6,"
        if (gato[2][1] == null)
            posicionesEnviar += "7,"
        if (gato[2][2] == null)
            posicionesEnviar += "8,"

        if(posicionesEnviar!=""){
            posicionesEnviar = posicionesEnviar.substring(0, posicionesEnviar.length-1)
            Log.e("Posiciones a enviar: ", posicionesEnviar)
        }
    }

    fun setLetterButtonAuto(pos: Int) {

        if (contadorMovimientos != 9) {
            when (pos) {
                0 -> {
                    if (gato[0][0] == null) {
                        gato[0][0] = setLetterButton(button_00)
                        validateWinner()
                    }
                }
                1 -> {
                    if (gato[0][1] == null) {
                        gato[0][1] = setLetterButton(button_01)
                        validateWinner()
                    }
                }
                2 -> {
                    if (gato[0][2] == null) {
                        gato[0][2] = setLetterButton(button_02)
                        validateWinner()
                    }
                }
                3 -> {
                    if (gato[1][0] == null) {
                        gato[1][0] = setLetterButton(button_10)
                        validateWinner()
                    }
                }
                4 -> {
                    if (gato[1][1] == null) {
                        gato[1][1] = setLetterButton(button_11)
                        validateWinner()
                    }
                }
                5 -> {
                    if (gato[1][2] == null) {
                        gato[1][2] = setLetterButton(button_12)
                        validateWinner()
                    }
                }
                6 -> {
                    if (gato[2][0] == null) {
                        gato[2][0] = setLetterButton(button_20)
                        validateWinner()
                    }
                }
                7 -> {
                    if (gato[2][1] == null) {
                        gato[2][1] = setLetterButton(button_21)
                        validateWinner()
                    }
                }
                8 -> {
                    if (gato[2][2] == null) {
                        gato[2][2] = setLetterButton(button_22)
                        validateWinner()
                    }
                }
            }
        }
    }

    fun restart() {
        button_00.isEnabled = true
        button_01.isEnabled = true
        button_02.isEnabled = true
        button_10.isEnabled = true
        button_11.isEnabled = true
        button_12.isEnabled = true
        button_20.isEnabled = true
        button_21.isEnabled = true
        button_22.isEnabled = true

        button_00.text = ""
        button_01.text = ""
        button_02.text = ""
        button_10.text = ""
        button_11.text = ""
        button_12.text = ""
        button_20.text = ""
        button_21.text = ""
        button_22.text = ""

        for (i in (0 until 3)) {
            for (j in (0 until 3)) {
                gato[i][j] = null
            }
        }

        contadorMovimientos = 0
        flagAux = false
        juegoTerminado = false
        turno = false
        seleccionar = false
        flagAux = true

        if (myCountDownTimer != null) {
            myCountDownTimer!!.cancel()
            progressBar.progress = 100
            contador = 0
            myCountDownTimer!!.start()
        }
    }

    fun validateWinner() {
        fillPosition()

        var aux = ""
        if (contadorMovimientos >= 5) {
            //Validaciones horizontales
            if ((gato[0][0] == gato[0][1]) && (gato[0][1] == gato[0][2])) {
                aux = gato[0][0].toString()
            } else if ((gato[1][0] == gato[1][1]) && (gato[1][1] == gato[1][2])) {
                aux = gato[1][0].toString()
            } else if ((gato[2][0] == gato[2][1]) && (gato[2][1] == gato[2][2])) {
                aux = gato[2][0].toString()
            }
            //Validaciones verticales
            else if ((gato[0][0] == gato[1][0]) && (gato[1][0] == gato[2][0])) {
                aux = gato[0][0].toString()
            } else if ((gato[0][1] == gato[1][1]) && (gato[1][1] == gato[2][1])) {
                aux = gato[0][1].toString()
            } else if ((gato[0][2] == gato[1][2]) && (gato[1][2] == gato[2][2])) {
                aux = gato[0][2].toString()
            }
            //Validaciones en diagonal
            else if ((gato[0][0] == gato[1][1]) && (gato[1][1] == gato[2][2])) {
                aux = gato[0][0].toString()
            } else if ((gato[2][0] == gato[1][1]) && (gato[1][1] == gato[0][2])) {
                aux = gato[2][0].toString()
            }

            if (aux == "X" || aux == "O") {
                myCountDownTimer!!.cancel()
                juegoTerminado = true
                progressBar.progress = 100
                textView_Tiempo.text = "Tiempo: 0 segundos"
                showAlertDialogWinner(aux)
            } else if (contadorMovimientos == 9) {
                empate++
                myCountDownTimer!!.cancel()
                juegoTerminado = true
                progressBar.progress = 100
                textView_Tiempo.text = "Tiempo: 0 segundos"
                showAlertDialogWinner("Empate")
            }
        }
    }

    fun showAlertDialogWinner(winner: String) {
        if (winner != "Empate") {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Ganador ${winner}")
                .setMessage("El ganador es el jugador ${winner}")
                .setPositiveButton("Aceptar") { view, _ ->
                    updateScore(winner)
                    //restart()
                    view.dismiss()
                }
                .setCancelable(false)
                .create()
            dialog.show()
        } else {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Empate")
                .setMessage("Ningún jugador gano, es un empate.")
                .setPositiveButton("Aceptar") { view, _ ->
                    updateScore(winner)
                    //restart()
                    view.dismiss()
                }
                .setCancelable(false)
                .create()
            dialog.show()
        }
    }

    fun updateScore(winner: String) {
        if (winner == "X") {
            puntuacionX++
        } else {
            puntuacionO++
        }
        textView_PuntuacionTotal.text = "Puntuación total: ${puntuacionX + puntuacionO + empate}"
        textView_PuntuacionX.text = "Puntuación X: ${puntuacionX}"
        textView4_PuntuacionO.text = "Puntuación O: ${puntuacionO}"
        textView_Empates.text = "Empate: ${empate}"
    }

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            if (!juegoTerminado!!) {
                contador++
                println("contador = $contador")
                progressBar.progress = (contador * 100 / (5000 / 1000))
                textView_Tiempo.text = "Tiempo: ${5 - contador} segundos"
            }
        }

        override fun onFinish() {
            if (!juegoTerminado!! && posicionesEnviar!="") {
                myCountDownTimer!!.cancel()
                progressBar.progress = 100
                contador = 0

                if (turno == true) {//Turno de la máquina
                    callAsyncTask("turno jugador")
                } else {//Turno del jugador
                    if (!seleccionar!!) {//El jugador no selecciono
                        callAsyncTask("turno maquina")
                    }
                }
            }
        }

        fun callAsyncTask(turn:String) {
            val json = JSONObject()
            json.put("turno", turn)
            json.put("posiciones", posicionesEnviar)
            progressAsyncTask = ProgressAsyncTask()
            progressAsyncTask!!.execute("POST", host + "randomGato.php", json.toString())
        }
    }

    inner class ProgressAsyncTask : AsyncTask<String, Unit, String>() {
        val TIME_OUT = 50000

        //Antes de ejecutar
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog!!.show()
        }

        //En ejecución en segundo plano
        override fun doInBackground(vararg params: String?): String {
            val url = URL(params[1])
            val httpClient = url.openConnection() as HttpURLConnection
            httpClient.readTimeout = TIME_OUT
            httpClient.connectTimeout = TIME_OUT
            httpClient.requestMethod = params[0]

            if (params[0] == "POST") {
                httpClient.instanceFollowRedirects = false
                httpClient.doOutput = true
                httpClient.doInput = true
                httpClient.useCaches = false
                httpClient.setRequestProperty("Content-Type", "application/json; charset-utf-8")
            }

            try {
                if (params[0] == "POST") {
                    httpClient.connect()
                    val os = httpClient.outputStream
                    val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                    writer.write(params[2])
                    writer.flush()
                    writer.close()
                    os.close()
                }
                if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
                    val stream = BufferedInputStream(httpClient.inputStream)
                    val data: String = readStream(inputStream = stream)
                    Log.e("Data:", data)
                    return data
                } else if (httpClient.responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                    Log.e("ERROR:", httpClient.responseCode.toString())
                    /*//El tiempo de espera se agoto, se hace de nuevo la petición
                    progressDialog!!.dismiss()
                    myCountDownTimer!!.start()
                    myCountDownTimer!!.onFinish()*/
                } else {
                    Log.e("ERROR:", httpClient.responseCode.toString())
                    /*//Ocurrio un error se, hace de nuevo la petición
                    progressDialog!!.dismiss()
                    myCountDownTimer!!.start()
                    myCountDownTimer!!.onFinish()*/
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                httpClient.disconnect()
            }
            return null.toString()
        }

        fun readStream(inputStream: BufferedInputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()

            bufferedReader.forEachLine { stringBuilder.append(it) }
            Log.e("StringBuider", "${stringBuilder.toString()}")

            return stringBuilder.toString()
        }

        //Cuando llegan los datos del servidor
        override fun onProgressUpdate(vararg values: Unit?) {
            super.onProgressUpdate(*values)
        }

        //Despues de la ejecuión
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            Log.e("Resultado:", "$result")

            if (!result.isNullOrBlank() && !result.isNullOrEmpty()) {
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject

                if (json.int("succes") == 1) {
                    val jsonFinal = JSONObject(result)
                    val respuestaServer = jsonFinal.getJSONArray("respuestaServer")
                    val turnoEnviar = respuestaServer.getJSONObject(0).getBoolean("turnoEnviar")
                    val pos = respuestaServer.getJSONObject(0).getInt("pos")

                    //var pos:Int = aux.get(1).toString().toInt()
                    Log.e("Posición", pos.toString())

                    if(turnoEnviar){
                        turno = true
                        seleccionar = true
                        setLetterButtonAuto(pos)

                        progressDialog!!.dismiss()

                        myCountDownTimer!!.start()
                        myCountDownTimer!!.onFinish()
                    }else{
                        turno = false
                        seleccionar = false
                        setLetterButtonAuto(pos)

                        progressDialog!!.dismiss()

                        myCountDownTimer!!.start()
                    }
                }
            }
        }

        override fun onCancelled() {
            super.onCancelled()
            progressDialog!!.dismiss()
            Toast.makeText(applicationContext, "Se ha cancelado la petición, reintentando de nuevo...", Toast.LENGTH_SHORT).show()
            myCountDownTimer!!.start()
            myCountDownTimer!!.onFinish()
        }
    }
}