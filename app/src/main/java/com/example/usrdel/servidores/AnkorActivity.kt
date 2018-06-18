package com.example.usrdel.servidores

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class AnkorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ankor)

        async(UI){
            llamarPosts("i")
            Log.i("async", "Luego de llamar post")
        }
        Log.i("async", "Termino")
    }

    fun llamarPosts(idPost: String){
        val url = "https://jsonplaceholder.typicode.com/posts/1"
        url.httpGet().responseString{reques, response, result ->
            Log.i("async", " request: $reques")
            Log.i("async", " response: $response")
            Log.i("async", " result: $result")
        }
    }
}
