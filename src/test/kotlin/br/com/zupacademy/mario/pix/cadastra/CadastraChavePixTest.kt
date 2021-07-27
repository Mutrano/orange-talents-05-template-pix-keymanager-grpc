package br.com.zupacademy.mario.pix.cadastra

import br.com.zupacademy.mario.CadastraKeyRequest
import br.com.zupacademy.mario.PixKeyManagerCadastraGrpcServiceGrpc
import br.com.zupacademy.mario.TipoDeChave
import br.com.zupacademy.mario.TipoDeConta
import br.com.zupacademy.mario.integration.ContaDoClienteNoItauClient
import br.com.zupacademy.mario.integration.DadosContaClienteResponse
import br.com.zupacademy.mario.integration.InstituicaoResponse
import br.com.zupacademy.mario.integration.TitularResponse
import br.com.zupacademy.mario.pix.ChavePixRepository
import br.com.zupacademy.mario.pix.paraNovaChave
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import javax.inject.Singleton


@MicronautTest(transactional = false)
class CadastraChavePixTest(
    val repository: ChavePixRepository,
    val grpcClient: PixKeyManagerCadastraGrpcServiceGrpc.PixKeyManagerCadastraGrpcServiceBlockingStub,
    val itauClient: ContaDoClienteNoItauClient
) {
    /**
     * 1 - happy path do cadastro de chave
     * 2 - erro de validação do tipo de chave
     * 3 - erros de validação em geral
     * 4 - usuário não encontrado no ERP Itaú
     * **/
    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `Deve criar uma chave pix de uma chave valido`() {
        //ação
        val request = CadastraKeyRequest.newBuilder()
            .setChave("26481207002")
            .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.CPF)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()
        //mock erp itau
        Mockito.`when`(itauClient.buscaContaPorTipo(request.clienteId, request.tipoDeConta.name)).thenReturn(
            HttpResponse.ok(dadosDoCliente())
        )

        val response = grpcClient.cadastra(request)
        //verificação

        assertEquals(request.clienteId, response.clienteId)
        assertNotNull(response.pixId)
        assertTrue(response.pixId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\$".toRegex()))
    }

    @Test
    fun `Não deve cadastrar chave inválida`() {
        //ação
        val request = CadastraKeyRequest.newBuilder()
            .setChave("26481207005") // cpf inválido
            .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.CPF)
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(request)
        }
        //verificação
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `Não deve cadastrar com campos em branco`() {
        val request = CadastraKeyRequest.newBuilder().build()

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(request)
        }
        //verificação
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `Não deve cadastrar chave ja existente`() {
        // cenário
        val request = requestValida()
        val novaChave = request.paraNovaChave()
        val contaAssociada = dadosDoCliente().paraConta()
        val chaveSalva = novaChave.paraChave(contaAssociada)

        repository.save(chaveSalva)
        // ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(request)
        }
        //verificação
        with(thrown) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
        }
    }

    @Test
    fun `Não deve cadastrar se não encontrar o cliente no ERP do Itaú`() {
        //cenário
        val request = requestValida()
        Mockito.`when`(
            itauClient.buscaContaPorTipo(
                clienteId = request.clienteId,
                tipo = request.tipoDeConta.name
            )
        ).thenReturn(HttpResponse.notFound())

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastra(request)
        }
        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel("grpc-server") channel: ManagedChannel): PixKeyManagerCadastraGrpcServiceGrpc.PixKeyManagerCadastraGrpcServiceBlockingStub? {
            return PixKeyManagerCadastraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ContaDoClienteNoItauClient::class)
    fun ContaDoClienteNoItauClient(): ContaDoClienteNoItauClient {
        return Mockito.mock(ContaDoClienteNoItauClient::class.java)
    }
}


fun dadosDoCliente(): DadosContaClienteResponse {
    return DadosContaClienteResponse(
        tipo = "CONTA_CORRENTE",
        instituicao = InstituicaoResponse(nome = "ITAÚ UNIBANCO S.A.", ispb = "60701190"),
        agencia = "0001",
        numero = "291900",
        titular = TitularResponse(
            id = "c56dfef4-7901-44fb-84e2-a2cefb157890",
            nome = "Rafael M C Ponte", cpf = "02467781054"
        )
    )
}

fun requestValida(): CadastraKeyRequest {
    return CadastraKeyRequest.newBuilder()
        .setChave("26481207002")
        .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
        .setTipoDeChave(TipoDeChave.CPF)
        .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
        .build()
}