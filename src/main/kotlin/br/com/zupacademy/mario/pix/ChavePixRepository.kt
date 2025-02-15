package br.com.zupacademy.mario.pix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository:JpaRepository<ChavePix,Long> {

    fun existsByChave(chave:String):Boolean

    fun existsByPixId(pixId:UUID):Boolean

    fun findByPixIdAndClienteId(pixId:UUID,clienteId:UUID): ChavePix?
}