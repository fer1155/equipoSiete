package com.example.myapplication.webservice

/**
 * Representa la respuesta completa de la API del Pokédex.
 *
 * Gson deserializa automáticamente el JSON recibido desde la API
 * en esta clase, mapeando el campo `pokemon` del JSON a la lista
 * de objetos [Pokemon].
 *
 * @property pokemon Lista completa de Pokémon disponibles en el Pokédex.
 */
data class PokemonResponse(
    val pokemon: List<Pokemon>
)

/**
 * Representa la información básica de un Pokémon individual.
 *
 * Solo se mapean los campos necesarios para el funcionamiento
 * del diálogo de reto aleatorio. Los campos adicionales presentes
 * en el JSON son ignorados por Gson.
 *
 * @property id Identificador numérico único del Pokémon.
 * @property num Número del Pokémon en formato de texto (ej. "001").
 * @property name Nombre del Pokémon.
 * @property img URL de la imagen del Pokémon. Puede venir con `http`
 * y debe reemplazarse por `https` antes de usarse.
 */
data class Pokemon(
    val id: Int,
    val num: String,
    val name: String,
    val img: String
)