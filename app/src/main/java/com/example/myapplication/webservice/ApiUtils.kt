package com.example.myapplication.webservice

/**
 * Objeto utilitario que expone una instancia única de [ApiService].
 *
 * Actúa como punto de acceso centralizado para realizar llamadas
 * a la API del Pokédex, delegando la creación del servicio a
 * [RetrofitClient]. La instancia se crea de forma diferida la
 * primera vez que se accede a [apiService].
 */
object ApiUtils {

    /**
     * Instancia única de [ApiService] creada a partir del cliente
     * Retrofit configurado en [RetrofitClient].
     *
     * Se inicializa de forma diferida (lazy) para evitar crear
     * la conexión hasta que sea necesaria por primera vez.
     */
    val apiService: ApiService by lazy {
        RetrofitClient.instance.create(ApiService::class.java)
    }
}