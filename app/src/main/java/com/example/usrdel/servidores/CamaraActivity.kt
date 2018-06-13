package com.example.usrdel.servidores

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_camara.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CamaraActivity : AppCompatActivity() {

    var directorioActualImagen = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camara)


        val fotoActual = File(
                Environment.getExternalStorageDirectory().path + // ->
                        "/Android/data/com.example.usrdel.servidores/files/Pictures/JPEG_20180613_153033_1261933880561795823.jpg")

        val fotoActualBitmap = BitmapFactory
                .decodeFile(fotoActual.getAbsolutePath())

        image_view_camara.setImageBitmap(fotoActualBitmap)




        boton_tomar_foto.setOnClickListener { view ->
            tomarFoto()
        }
    }

    private fun tomarFoto() {
        val archivoImagen = crearArchivo(
                "JPEG_",
                Environment.DIRECTORY_PICTURES,
                ".jpg")

        enviarIntentFoto(archivoImagen)
    }

    private fun crearArchivo(prefijo: String,
                             directorio: String,
                             extension: String): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(Date())

        val imageFileName = prefijo + timeStamp + "_"
        val storageDir = getExternalFilesDir(directorio)

        return File.createTempFile(
                imageFileName, /* prefix */
                extension, /* suffix */
                storageDir      /* directory */
        )
    }

    private fun enviarIntentFoto(archivo: File) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val photoURI: Uri = FileProvider
                .getUriForFile(
                        this,
                        "com.example.usrdel.servidores.fileprovider",
                        archivo)

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, TOMAR_FOTO_REQUEST);
        }

    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent) {
        when (requestCode) {
            TOMAR_FOTO_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    val fotoActualBitmap = BitmapFactory
                            .decodeFile(directorioActualImagen)
                    image_view_foto.setImageBitmap(fotoActualBitmap)
                    //obtenerInfoCodigDeBarras(fotoActualBitmap)
                }
            }
        }
    }

    fun obtenerInfoCodigDeBarras(bitmap:Bitmap){

        val image = FirebaseVisionImage.fromBitmap(bitmap)

        val detector = FirebaseVision.getInstance().visionBarcodeDetector

        val result = detector.detectInImage(image).result

    }

    companion object {
        val TOMAR_FOTO_REQUEST = 1
    }


}


class GenericFileProvider : FileProvider() {

}
