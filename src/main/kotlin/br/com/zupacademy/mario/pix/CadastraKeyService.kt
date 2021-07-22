package br.com.zupacademy.mario.pix

import br.com.zupacademy.mario.integration.ContaDoClienteNoItauClient
import br.com.zupacademy.mario.pix.exceptions.ChaveJaExistenteException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CadastraKeyService(
    val repository: ChavePixRepository,
    val itauClient: ContaDoClienteNoItauClient
) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)
    /* Key entra ja validada (@Valid)
    * 1-> lógica para ver se a chave é unica
    * 2 -> Consulta ERP Itaú
    * 3 -> salva
    * 4 -> retorna
    * */
    @Transactional
    fun cadastra(@Valid novaChavePix: NovaChavePix):ChavePix{
        if(repository.existsByChave(novaChavePix.chave!!)){
            throw ChaveJaExistenteException("chave ${novaChavePix.chave} ja existente no sistema")
        }
        val contaClienteResponse = itauClient.buscaContaPorTipo(novaChavePix.clienteId!!,novaChavePix.tipoDeConta!!.name)
        val conta = contaClienteResponse.body()?.paraConta() ?: throw IllegalStateException("Cliente não encontrado no Itaú")

        val chaveSalva = novaChavePix.paraChave(conta)
        repository.save(chaveSalva)

        return chaveSalva
    }
}