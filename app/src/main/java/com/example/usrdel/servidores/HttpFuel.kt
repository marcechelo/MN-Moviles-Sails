package com.example.usrdel.servidores

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import java.util.*
import kotlin.collections.ArrayList

class HttpFuel : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_fuel)

        "http://172.29.64.82:1337/Entrenador/3".httpGet().responseString{request, response, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                }
                is Result.Success -> {
                    val jsonStringEntrenador = result.get()
                    Log.i("mensaje","${jsonStringEntrenador}")

                    val entrenador:Entredador? = Klaxon().parse<Entredador>(jsonStringEntrenador)

                    if (entrenador!=null){
                        Log.i("Http-ejemplo","Nombre ${entrenador.nombre}")
                        Log.i("Http-ejemplo","Apellido ${entrenador.apellido}")
                        Log.i("Http-ejemplo","id ${entrenador.id}")
                        Log.i("Http-ejemplo","Medallas ${entrenador.medallas}")
                        Log.i("Http-ejemplo","Edad ${entrenador.edad}")
                        Log.i("Http-ejemplo","created ${entrenador.createdAtDate}")
                        Log.i("Http-ejemplo","updated ${entrenador.updatedAtDate}")

                        entrenador.pokemons.forEach{pokemon: Pokemon ->
                            Log.i("Http-ejemplo","Nombre ${pokemon.nombre}")
                            Log.i("Http-ejemplo","Tipo ${pokemon.tipo}")
                            Log.i("Http-ejemplo","Numero ${pokemon.numero}")
                        }

                    }

                }
            }
        }
    }
}

class Entredador(var nombre:String,
                 var apellido:String,
                 var edad:String,
                 var medallas: String,
                 var createdAt: Long,
                 var updatedAt: Long,
                 var id:Int,
                 var pokemons: ArrayList<Pokemon> = ArrayList()){

    var createdAtDate = Date(createdAt)
    var updatedAtDate = Date(updatedAt)


}

class Pokemon(var nombre:String,
              var numero: Int,
              var tipo: String,
              var entrenadorId: Int,
              var createdAt: Long,
              var updatedAt: Long,
              var id:Int){
    var createdAtDate = Date(createdAt)
    var updatedAtDate = Date(updatedAt)

}
