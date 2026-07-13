package com.example.myapplication.webservice

import retrofit2.http.GET

/**
 * Interfaz que define los endpoints disponibles de la API del Pokédex.
 *
 * Retrofit utiliza esta interfaz para generar automáticamente la
 * implementación de las llamadas HTTP. Cada función anotada representa
 * un endpoint distinto y es suspendible para ser usada con corrutinas.
 */
interface ApiService {

    /**
     * Obtiene la lista completa de Pokémon desde el archivo JSON del Pokédex.
     *
     * La ruta del endpoint se resuelve contra la [BASE_URL] definida
     * en [RetrofitClient], resultando en la URL completa:
     * `https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json`
     *
     * @return [PokemonResponse] con la lista completa de Pokémon.
     */
    @GET("pokedex.json")
    suspend fun getPokemon(): PokemonResponse
}