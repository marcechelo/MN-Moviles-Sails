package com.example.usrdel.servidores

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet
import com.onesignal.OneSignal
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import javax.xml.transform.Result

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        Alerter.create(this@MainActivity)
                .setTitle("Alert Title")
                .setText("Alerta")
                .show()

        boton_http.setOnClickListener{view: View -> irAActividadHHTP()}
        boton_camera.setOnClickListener{view: View -> irAActividadCamara()}
        boton_maps.setOnClickListener{view: View -> irAActividadMaps()}
        boton_ankor.setOnClickListener{view: View -> irAActividadAnkor()}
        boton_speech.setOnClickListener { vie: View -> irAActividadSTT() }
        boton_camaraDos.setOnClickListener { view: View ->irAActividadCamara2() }

        boton_random.setOnClickListener { view: View ->
            val numero = (-0.189193..-0.188193).random()
            val toast = Toast.makeText(this, numero.toString(), Toast.LENGTH_LONG)
            toast.show()

        }


    }

    fun ClosedRange<Double>.random() = (Math.random() * (endInclusive - start) + start).toDouble()

    fun irAActividadHHTP(){
        val intent = Intent(this,HttpFuel::class.java)
        startActivity(intent)
    }

    fun irAActividadCamara(){
        val intent = Intent(this,CamaraActivity::class.java)
        startActivity(intent)
    }

    fun irAActividadCamara2(){
        val intent = Intent(this,Camara2::class.java)
        startActivity(intent)
    }

    fun irAActividadMaps(){
        val intent = Intent(this,MapsActivity::class.java)
        startActivity(intent)
    }

    fun irAActividadAnkor(){
        startActivity(intentFor<AnkorActivity>())
    }

    fun irAActividadSTT(){
        val intent = Intent(this,STT::class.java)
        startActivity(intent)
    }

}
