package com.example.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private val  REQUEST_CODE_ENABLE_BT:Int =1
    private val  REQUEST_CODE_DISCOVERABLE_BT:Int =1

    lateinit var bAdapter:BluetoothAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bAdapter= BluetoothAdapter.getDefaultAdapter();

        val bluetoothStatusTv = findViewById<TextView>(R.id.bluetoothStatusTv)
        if(bAdapter == null){
         bluetoothStatusTv.text="Blutooth is not available"
        } else {
            bluetoothStatusTv.text="Blutooth is  available"
        }

        val bluetoothIv= findViewById<ImageView>(R.id.bluetoothIv)
        if(bAdapter.isEnabled){
            bluetoothIv.setImageResource(R.drawable.k_bluetooth_on)
        }else {
            bluetoothIv.setImageResource(R.drawable.k_bluetooth_off)
        }
        val turnOnBtn = findViewById<Button>(R.id.turnOnBtn)
        val turnOffBtn = findViewById<Button>(R.id.turnOffBtn)
        val discoverableOnBtn = findViewById<Button>(R.id.discoverableOnBtn)

        turnOnBtn.setOnClickListener {
            if(bAdapter.isEnabled){
                    Toast.makeText(this,"Already On", Toast.LENGTH_LONG).show()
            }else {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

                startActivityForResult(intent,REQUEST_CODE_ENABLE_BT)
            }
        }

        turnOffBtn.setOnClickListener {
            if(!bAdapter.isEnabled){
                Toast.makeText(this,"Already Off", Toast.LENGTH_LONG).show()
            }else {
              bAdapter.disable()
                val bluetoothIv= findViewById<ImageView>(R.id.bluetoothIv)
                bluetoothIv.setImageResource(R.drawable.k_bluetooth_off)
                Toast.makeText(this,"Bluetooth turned off", Toast.LENGTH_LONG).show()
            }
        }

        discoverableOnBtn.setOnClickListener {
            if(!bAdapter.isDiscovering){
                Toast.makeText(this,"Making your device discoverable", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
            }
        }

        val pairedBtn = findViewById<Button>(R.id.pairedBtn)
        pairedBtn.setOnClickListener {
            if(bAdapter.isEnabled){
                val pairedTv = findViewById<TextView>(R.id.pairedTv)
                pairedTv.text="Paired devices:"
                val devices = bAdapter.bondedDevices
                for (device in devices){
                    val deviceName = device.name
                    pairedTv.append("\nDevice: $deviceName, $device")

                }

            }else {
                Toast.makeText(this,"Turn on bluetooth first", Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if(resultCode == Activity.RESULT_OK){
                    val bluetoothIv= findViewById<ImageView>(R.id.bluetoothIv)
                    bluetoothIv.setImageResource(R.drawable.k_bluetooth_on)
                    Toast.makeText(this,"Blutooth is on", Toast.LENGTH_LONG).show()
                }else {
                    Toast.makeText(this,"Cant on bluetooth", Toast.LENGTH_LONG).show()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}