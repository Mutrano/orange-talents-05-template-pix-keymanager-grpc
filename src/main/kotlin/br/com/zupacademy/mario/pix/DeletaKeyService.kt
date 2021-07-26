package br.com.zupacademy.mario.pix

import br.com.zupacademy.mario.integration.ContaDoClienteNoItauClient
import br.com.zupacademy.mario.pix.exceptions.ChaveNaoEhDoClienteException
import br.com.zupacademy.mario.pix.exceptions.ChaveNaoEncontradaException
import br.com.zupacademy.mario.pix.exceptions.ClienteNaoEncontradoException
import br.com.zupacademy.mario.pix.validations.ValidUUID
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class DeletaKeyService(
    val repository: ChavePixRepository
    )
{
    @Transactional
    fun deleta(
        @NotBlank @ValidUUID clienteId:String?,
        @NotBlank @ValidUUID pixId:String?
    ){
        val uuidPixId = UUID.fromString(pixId)
        val uuidClienteId = UUID.fromString(clienteId)

        val chaveExiste = repository.existsByPixId(uuidPixId)
        if(!chaveExiste){
            throw ChaveNaoEncontradaException("A chave ${pixId} não foi encontrada no sistema")
        }
        val chaveEncontrada = repository
            .findByPixIdAndClienteId(pixId=uuidPixId!!,clienteId=uuidClienteId!!)
            ?: throw ChaveNaoEhDoClienteException("A chave ${pixId} não pertence ao cliente ${clienteId}")

        repository.delete(chaveEncontrada)
    }
}
