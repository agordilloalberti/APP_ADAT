package com.es.aplicacion.service

import com.es.aplicacion.dto.UsuarioDTO
import com.es.aplicacion.dto.UsuarioRegisterDTO
import com.es.aplicacion.error.exception.BadRequestException
import com.es.aplicacion.error.exception.NotFoundException
import com.es.aplicacion.error.exception.UnauthorizedException
import com.es.aplicacion.model.Usuario
import com.es.aplicacion.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.stereotype.Service
import com.es.aplicacion.externalApi.ExternalApiService
import com.es.aplicacion.util.DTOMapper

@Service
class UsuarioService : UserDetailsService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    private lateinit var apiService: ExternalApiService


    override fun loadUserByUsername(username: String?): UserDetails {
        var usuario: Usuario = usuarioRepository
            .findByUsername(username!!)
            .orElseThrow {
                UnauthorizedException("$username no existente")
            }

        return User.builder()
            .username(usuario.username)
            .password(usuario.password)
            .roles(usuario.roles)
            .build()
    }

    fun insertUser(usuarioInsertadoDTO: UsuarioRegisterDTO) : UsuarioDTO? {

        var user: UsuarioDTO? = null

        val datosProvincias = apiService.obtenerDatosDesdeApi()

        var cpro = ""

        if (usuarioInsertadoDTO.username.isBlank()||
            usuarioInsertadoDTO.email.isBlank()||
            usuarioInsertadoDTO.password.isBlank()||
            usuarioInsertadoDTO.passwordRepeat.isBlank()||
            usuarioInsertadoDTO.direccion.isEmpty()){
                throw BadRequestException("")
            }

        user = UsuarioDTO(
            usuarioInsertadoDTO.username,
            usuarioInsertadoDTO.email,
            passwordEncoder.encode(usuarioInsertadoDTO.password),
            usuarioInsertadoDTO.direccion
        )

        if(datosProvincias?.data != null) {
            val dato = datosProvincias.data.stream().filter {
                it.PRO == user.direccion.provincia.uppercase()
            }.findFirst().orElseThrow {
                NotFoundException("Provincia ${user.direccion.provincia.uppercase()} no válida")
            }
            cpro = dato.CPRO
        }

        val datosMunicipios = apiService.obtenerMunicipios(cpro)

        if (datosMunicipios?.data !=null){
            datosMunicipios.data.stream().filter {
                it.PRO == user.direccion.municipio
            }.findFirst().orElseThrow{
                NotFoundException("Municipio ${user.direccion.municipio.uppercase()} no valido")
            }
        }

        usuarioRepository.insert(DTOMapper.userDTOToEntity(usuarioInsertadoDTO))

        return user

    }
}