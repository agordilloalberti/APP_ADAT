package com.es.aplicacion.util

import com.es.aplicacion.dto.UsuarioRegisterDTO
import com.es.aplicacion.model.Usuario

object DTOMapper {

    /*
    val username: String,
    val password: String,
    val email: String,
    val roles: String = "USER",
    val direccion: Direccion
     */
    fun userDTOToEntity(usuarioDTO: UsuarioRegisterDTO): Usuario{
        return Usuario(
            null,
            usuarioDTO.username,
            usuarioDTO.password,
            usuarioDTO.email,
            usuarioDTO.rol!!,
            usuarioDTO.direccion
        )
    }
}