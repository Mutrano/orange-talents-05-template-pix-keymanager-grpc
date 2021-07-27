package br.com.zupacademy.mario.pix.deleta

import br.com.zupacademy.mario.DeletaKeyRequest
import br.com.zupacademy.mario.PixKeyManagerCadastraGrpcServiceGrpc
import br.com.zupacademy.mario.PixKeyManagerDeletaGrpcServiceGrpc
import br.com.zupacademy.mario.pix.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Singleton

/*
* chave existe e eh do cliente
* chave existe e nao e do cliente
* chave nao existe
* */

@MicronautTest(transactional = false)
class DeletaChavePixTest(
    val grpcClient: PixKeyManagerDeletaGrpcServiceGrpc.PixKeyManagerDeletaGrpcServiceBlockingStub,
    val repository: ChavePixRepository
) {
    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve deletar chave existente e do cliente`() {
        //cenario
        val chaveSalva = repository.save(chaveValida())
        //acao
        grpcClient.deleta(
            DeletaKeyRequest.newBuilder()
                .setClienteId(chaveSalva.clienteId.toString())
                .setPixId(chaveSalva.pixId.toString())
                .build()
        )

        //validacoes
        assertFalse(repository.existsByPixId(chaveSalva.pixId))
    }

    @Test
    fun `nao deve deletar se a chave nao for do cliente informado`() {
        //cenario
        val chaveSalva = repository.save(chaveValida())
        //acao

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(
                DeletaKeyRequest.newBuilder()
                    .setClienteId("5260263c-a3c1-4727-ae32-3bdb2538841b") // cliente diferente
                    .setPixId(chaveSalva.pixId.toString())
                    .build()
            )
        }
        // validacao

        with(thrown){
            assertEquals(status.code, Status.FAILED_PRECONDITION.code)
        }
        assertTrue(repository.existsByPixId(chaveSalva.pixId))
    }

    @Test
    fun `nao deve deletar se a chave nao for encontrada`(){

        // acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(
                DeletaKeyRequest.newBuilder()
                    .setClienteId("5260263c-a3c1-4727-ae32-3bdb2538841b") // cliente diferente
                    .setPixId("5260263c-a3c1-4727-ae32-3bdb2538841b") //UUID aleatório
                    .build()
            )
        }

        // validacoes

        with(thrown){
            assertEquals(status.code,Status.NOT_FOUND.code)
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun DeletaChaveClient(@GrpcChannel("grpc-server") channel: ManagedChannel): PixKeyManagerDeletaGrpcServiceGrpc.PixKeyManagerDeletaGrpcServiceBlockingStub {
            return PixKeyManagerDeletaGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}


fun chaveValida(): ChavePix {
    return ChavePix(
        clienteId = UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890"),
        tipo = TipoDeChave.CPF,
        chave = "90022141030",
        tipoDeConta = TipoDeConta.CONTA_CORRENTE,

        contaAssociada = ContaAssociada(
            cpfDoTitular = "90022141030",
            instituicao = "Itaú Unibanco",
            agencia = "2315649",
            numero = "151456489",
            titular = "Rafael Ponte"
        )

    )
}