package com.es.aplicacion.externalApi

data class DatosMunicipios(
    val update_date: String,
    val size: Int,
    val data: List<Provincia>?,
)