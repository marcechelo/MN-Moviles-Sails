package com.example.usrdel.servidores

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*
import kotlin.collections.ArrayList


class MapsActivity :
        AppCompatActivity(),
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener {

    private lateinit var mMap: GoogleMap

    var arregloMarcadores = ArrayList<Marker>()

    val epnLatLang = LatLng(-0.2103764, -78.4891095)
    val zoom = 17f

    val casaCulturaLatLang = LatLng(-0.209990, -78.495118)
    val casaCultura: CameraPosition = CameraPosition
            .Builder()
            .target(casaCulturaLatLang)
            .zoom(zoom)
            .build()

    val supermaxiLatLang = LatLng(-0.206450, -78.487270)
    val supermaxi: CameraPosition = CameraPosition
            .Builder()
            .target(supermaxiLatLang)
            .zoom(zoom)
            .build()


    var usuarioTienePermisosLocalizacion = false;
    private val COLOR_BLACK_ARGB = -0x1000000
    private val COLOR_WHITE_ARGB = -0x1
    private val COLOR_GREEN_ARGB = -0xc771c4
    private val COLOR_PURPLE_ARGB = -0x7e387c
    private val COLOR_ORANGE_ARGB = -0xa80e9
    private val COLOR_BLUE_ARGB = -0x657db

    private val POLYGON_STROKE_WIDTH_PX: Float = 8.toFloat()
    private val PATTERN_DASH_LENGTH_PX = 20
    private val PATTERN_GAP_LENGTH_PX = 20
    private val DOT = Dot()
    private val DASH = Dash(PATTERN_DASH_LENGTH_PX.toFloat())
    private val GAP = Gap(PATTERN_GAP_LENGTH_PX.toFloat())

    private val PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH)

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private val PATTERN_POLYGON_BETA = Arrays.asList(DOT, GAP, DASH, GAP)


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    val PERMISO_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        solicitarPermisosLocalizacion()

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISO_REQUEST)
        } else {
            iniciar()
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }


    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                val location = p0!!.locations.get(p0!!.locations.size - 1) // obtener la localizacion
                Log.i("location", "${location.latitude} / ${location.longitude}")
            }
        }
    }

    private fun parar() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun iniciar(){
        buildLocationRequest()
        buildLocationCallback()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val tienePermisosFineLocation = ActivityCompat.checkSelfPermission(this@MapsActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        val tienePermisosCoarseLocation = ActivityCompat.checkSelfPermission(this@MapsActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (tienePermisosFineLocation && tienePermisosCoarseLocation) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISO_REQUEST)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        with(googleMap) {
            var polyline1 = googleMap.addPolyline(PolylineOptions()
                    .clickable(true)
                    .add(

                            LatLng(-0.210462, -78.493948),
                            LatLng(-0.208218, -78.490163),
                            LatLng(-0.208583, -78.488940),
                            LatLng(-0.209377, -78.490303)
                    )
            )

            var polygon1 = googleMap.addPolygon(PolygonOptions()
                    .clickable(true)
                    .add(
                            LatLng(-0.209431, -78.490078),
                            LatLng(-0.208734, -78.488951),
                            LatLng(-0.209431, -78.488286),
                            LatLng(-0.210085, -78.489745)


                    )

            )

            polygon1.tag = "alpha"

            formatearEstiloPoligono(polygon1)


            establecerListeners(googleMap)
            establecerSettings(googleMap)


            anadirMarcador(epnLatLang, "Ciudad de quito")




            moverCamaraPorLatLongZoom(epnLatLang, zoom)


            button_quito_julio_andrade.setOnClickListener { v ->
                anadirMarcador(casaCulturaLatLang, "Marcador en Quito Julio Andrade")


                moverCamaraPorPosicion(casaCultura)
            }

            button_quito.setOnClickListener { v ->
                anadirMarcador(supermaxiLatLang, "Marcador en Quito Julio Andrade")


                moverCamaraPorPosicion(supermaxi)
            }
        }
    }

    fun solicitarPermisosLocalizacion() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            usuarioTienePermisosLocalizacion = true
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun establecerSettings(googleMap: GoogleMap) {
        with(googleMap) {
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isMyLocationButtonEnabled = true
        }
    }

    private fun anadirMarcador(latitudLongitud: LatLng, titulo: String) {

        arregloMarcadores.forEach { marker: Marker ->
            marker.remove()
        }

        arregloMarcadores = ArrayList<Marker>()

        val marker = mMap.addMarker(
                MarkerOptions()
                        .position(latitudLongitud)
                        .title(titulo)
        )

        arregloMarcadores.add(marker)

        Log.i("map-adrian", "$arregloMarcadores")
    }


    private fun moverCamaraPorLatLongZoom(latitudLongitud: LatLng, zoom: Float) {


        mMap.moveCamera(
                CameraUpdateFactory
                        .newLatLngZoom(latitudLongitud, zoom)
        )


    }

    private fun moverCamaraPorPosicion(posicionCamara: CameraPosition) {
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(posicionCamara)
        )
    }


    private fun establecerListeners(googleMap: GoogleMap) {
        with(googleMap) {

            setOnCameraIdleListener(this@MapsActivity)
            setOnCameraMoveStartedListener(this@MapsActivity)
            setOnCameraMoveListener(this@MapsActivity)
            setOnCameraMoveCanceledListener(this@MapsActivity)


            setOnPolylineClickListener(this@MapsActivity)
            setOnPolygonClickListener(this@MapsActivity)
        }
    }


    private fun formatearEstiloPoligono(polygon: Polygon) {
        var type = ""
        // Get the data object stored with the polygon.
        if (polygon.tag != null) {
            type = polygon.tag.toString()
        }

        var pattern: List<PatternItem>? = null
        var strokeColor = COLOR_BLACK_ARGB
        var fillColor = COLOR_WHITE_ARGB

        when (type) {
        // If no type is given, allow the API to use the default.
            "alpha" -> {
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA
                strokeColor = COLOR_GREEN_ARGB
                fillColor = COLOR_PURPLE_ARGB
            }
            "beta" -> {
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA
                strokeColor = COLOR_ORANGE_ARGB
                fillColor = COLOR_BLUE_ARGB
            }
        }

        polygon.strokePattern = pattern
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX)
        polygon.strokeColor = strokeColor
        polygon.fillColor = fillColor
    }


    override fun onCameraMoveStarted(p0: Int) {
    }

    override fun onCameraMove() {
    }

    override fun onCameraMoveCanceled() {
    }

    override fun onCameraIdle() {
    }

    override fun onPolylineClick(p0: Polyline?) {
        Log.i("google-maps","dio clic en la ruta")
    }

    override fun onPolygonClick(p0: Polygon?) {
        Log.i("google-maps","dio clic en el poligono")
    }


}
