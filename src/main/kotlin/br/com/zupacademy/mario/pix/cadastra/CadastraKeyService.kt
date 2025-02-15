package br.com.zupacademy.mario.pix.cadastra

import br.com.zupacademy.mario.integration.ContaDoClienteNoItauClient
import br.com.zupacademy.mario.integration.IntegracaoBCB
import br.com.zupacademy.mario.pix.ChavePix
import br.com.zupacademy.mario.pix.ChavePixRepository
import br.com.zupacademy.mario.pix.NovaChavePix
import br.com.zupacademy.mario.pix.exceptions.ChaveJaExistenteException
import br.com.zupacademy.mario.pix.exceptions.ClienteNaoEncontradoException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.RuntimeException
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CadastraKeyService(
    val repository: ChavePixRepository,
    val itauClient: ContaDoClienteNoItauClient,
    val bcbClient: IntegracaoBCB
) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)
    /* Key entra ja validada (@Valid)
    * 1-> lógica para ver se a chave é unica
    * 2 -> Consulta ERP Itaú
    * 3 -> Cadastra no BCB
    * 3 -> salva
    * 4 -> retorna
    * */
    @Transactional
    fun cadastra(@Valid novaChavePix: NovaChavePix): ChavePix {
        if(repository.existsByChave(novaChavePix.chave!!)){
            throw ChaveJaExistenteException("chave ${novaChavePix.chave} ja existente no sistema")
        }

        val contaClienteResponse = itauClient.buscaContaPorTipo(novaChavePix.clienteId!!,novaChavePix.tipoDeConta!!.name)

        val conta = contaClienteResponse.body()?.paraConta() ?:
            throw ClienteNaoEncontradoException("Cliente ${novaChavePix.clienteId} não encontrado no Itaú")

        LOGGER.info("Oficialmente perdido")
        val requestBcb  = novaChavePix.paraCadastroBCB(conta)

        val requestBCBResponse = bcbClient.cadastra(requestBcb)

        val chaveBCB = requestBCBResponse.body()?: throw RuntimeException()


        val chaveSalva = chaveBCB.paraChave(clienteId = novaChavePix.clienteId, instituicao = conta.instituicao)
        repository.save(chaveSalva)

        return chaveSalva
    }
}