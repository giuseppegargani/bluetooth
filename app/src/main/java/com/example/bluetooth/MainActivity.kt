package com.example.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*


class MainActivity : Activity() {
    var myLabel: TextView? = null
    var myTextbox: EditText? = null
    var mBluetoothAdapter: BluetoothAdapter? = null
    var mmSocket: BluetoothSocket? = null
     var mmDevice: BluetoothDevice? = null
    var mmOutputStream: OutputStream? = null
    var mmInputStream: InputStream? = null
    var workerThread: Thread? = null
    lateinit var readBuffer: ByteArray
    var readBufferPosition = 0
    var counter = 0

    @Volatile
    var stopWorker = false
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val openButton = findViewById<View>(R.id.open) as Button


        myLabel = findViewById<View>(R.id.label) as TextView
        myTextbox = findViewById<View>(R.id.entry) as EditText

        //Open Button
        openButton.setOnClickListener {
            try {
                findBT()
                openBT()
            } catch (ex: IOException) {
            }
        }




    }

    fun findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            myLabel!!.text = "No bluetooth adapter available"
        }
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
            val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetooth, 0)
        }
        val pairedDevices = BluetoothAdapter.getDefaultAdapter().bondedDevices

        if (pairedDevices.size > 0) {
            myLabel!!.text="Paired devices:"

            for (device in pairedDevices){
                val deviceName = device.name
                myLabel!!.append("\nDevice: $deviceName, ${device.uuids}")

            }
            }
        mmDevice = pairedDevices.first()
    }




    @Throws(IOException::class)
    fun openBT() {
        val uuid =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") //Standard SerialPortService ID
        var mmSocket =  mmDevice!!.createRfcommSocketToServiceRecord(uuid)
        mmSocket.connect()
        mmOutputStream = mmSocket.getOutputStream()
        mmInputStream = mmSocket.getInputStream()
        beginListenForData()
        myLabel!!.text = "Bluetooth Opened"
    }

    fun beginListenForData() {
        val handler = Handler()
        val delimiter: Byte = 10 //This is the ASCII code for a newline character
        stopWorker = false
        readBufferPosition = 0
        readBuffer = ByteArray(1024)
        workerThread = Thread {
            while (!Thread.currentThread().isInterrupted && !stopWorker) {
                try {
                    val bytesAvailable = mmInputStream!!.available()
                    if (bytesAvailable > 0) {
                        val packetBytes = ByteArray(bytesAvailable)
                        mmInputStream!!.read(packetBytes)
                        for (i in 0 until bytesAvailable) {
                            val b = packetBytes[i]
                            if (b == delimiter) {
                                val encodedBytes = ByteArray(readBufferPosition)
                                System.arraycopy(
                                    readBuffer,
                                    0,
                                    encodedBytes,
                                    0,
                                    encodedBytes.size
                                )
                                val US_ASCII: Charset=Charset.forName("us-ascii")
                                val data = String(encodedBytes, US_ASCII)
                                readBufferPosition = 0
                                handler.post { myLabel!!.text = data }
                            } else {
                                readBuffer[readBufferPosition++] = b
                            }
                        }
                    }
                } catch (ex: IOException) {
                    stopWorker = true
                }
            }
        }
        workerThread!!.start()
    }



    @Throws(IOException::class)
    fun closeBT() {
        stopWorker = true
        mmOutputStream!!.close()
        mmInputStream!!.close()
        mmSocket!!.close()
        myLabel!!.text = "Bluetooth Closed"
    }
}