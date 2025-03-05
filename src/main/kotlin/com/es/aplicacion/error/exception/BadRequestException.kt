package com.es.aplicacion.error.exception

class BadRequestException(message: String)
    : Exception("Not authorized exception (401). $message") {
}