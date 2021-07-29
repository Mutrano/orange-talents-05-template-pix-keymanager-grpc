package br.com.zupacademy.mario.pix.deleta

import br.com.zupacademy.mario.integration.DeletaChaveRequest
import br.com.zupacademy.mario.integration.IntegracaoBCB
import br.com.zupacademy.mario.pix.ChavePixRepository
import br.com.zupacademy.mario.pix.exceptions.ChaveNaoEhDoClienteException
import br.com.zupacademy.mario.pix.exceptions.ChaveNaoEncontradaException
import br.com.zupacademy.mario.pix.validations.ValidUUID
import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpStatus.FORBIDDEN
import io.micronaut.http.HttpStatus.NOT_FOUND
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import java.lang.RuntimeException
import java.util.*
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class DeletaKeyService(
    val repository: ChavePixRepository,
    val bcbClient: IntegracaoBCB
) {
    @Transactional
    fun deleta(
        @NotBlank @ValidUUID clienteId: String?,
        @NotBlank @ValidUUID pixId: String?
    ) {
        val uuidPixId = UUID.fromString(pixId)
        val uuidClienteId = UUID.fromString(clienteId)

        val chaveExiste = repository.existsByPixId(uuidPixId)
        if (!chaveExiste) {
            throw ChaveNaoEncontradaException("A chave ${pixId} não foi encontrada no sistema")
        }
        val chaveEncontrada = repository
            .findByPixIdAndClienteId(pixId = uuidPixId!!, clienteId = uuidClienteId!!)
            ?: throw ChaveNaoEhDoClienteException("A chave ${pixId} não pertence ao cliente ${clienteId}")

        try {
            val bcbResponse = bcbClient.deleta(
                chaveEncontrada.chave,
                DeletaChaveRequest.of(chaveEncontrada)
            )
        } catch (expn: HttpClientResponseException) {
            when (expn.status) {
                NOT_FOUND -> throw ChaveNaoEncontradaException("A chave ${pixId} não foi encontrada no sistema")
                FORBIDDEN -> throw RuntimeException("Ocorreu um erro no sistema")
                else -> throw RuntimeException("Ocorreu um erro no sistema")
            }
        }
        repository.delete(chaveEncontrada)
    }
}
