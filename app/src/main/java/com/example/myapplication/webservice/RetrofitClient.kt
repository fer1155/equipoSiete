package com.example.myapplication.webservice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton que configura y provee la instancia de [Retrofit]
 * utilizada para realizar llamadas a la API del Pokédex.
 *
 * La instancia se crea de forma diferida la primera vez que
 * se accede a [instance], garantizando que solo exista una
 * única instancia durante el ciclo de vida de la aplicación.
 */
object RetrofitClient {

    /**
     * URL base del servidor desde donde se obtiene el archivo
     * JSON del Pokédex. Todos los endpoints definidos en
     * [ApiService] se resuelven relativos a esta URL.
     */
    private const val BASE_URL =
        "https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/"

    /**
     * Instancia única de [Retrofit] configurada con la [BASE_URL]
     * y el convertidor [GsonConverterFactory] para deserializar
     * automáticamente las respuestas JSON en objetos Kotlin.
     *
     * Se inicializa de forma diferida (lazy) para evitar crear
     * la instancia hasta que sea requerida por primera vez.
     */
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}