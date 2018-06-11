package com.example.usrdel.servidores

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.kittinunf.fuel.httpGet
import kotlinx.android.synthetic.main.activity_main.*
import javax.xml.transform.Result

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boton_http.setOnClickListener{view: View -> irAActividadHHTP()}
        boton_camera.setOnClickListener{view: View -> irAActividadCamara()}


    }

    fun irAActividadHHTP(){
        val intent = Intent(this,HttpFuel::class.java)
        startActivity(intent)
    }

    fun irAActividadCamara(){
        val intent = Intent(this,CamaraActivity::class.java)
        startActivity(intent)
    }

}
