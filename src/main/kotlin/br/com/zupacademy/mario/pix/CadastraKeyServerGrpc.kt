package br.com.zupacademy.mario

import br.com.zupacademy.mario.integration.ContaDoClienteNoItauClient
import br.com.zupacademy.mario.pix.CadastraKeyService
import br.com.zupacademy.mario.pix.ChavePixRepository
import br.com.zupacademy.mario.pix.paraNovaChave
import br.com.zupacademy.mario.shared.handlers.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@ErrorAroundHandler
@Singleton
class CadastraKeyEndpoint(
    val cadastraKeyService: CadastraKeyService,
) : PixKeyManagerCadastraGrpcServiceGrpc.PixKeyManagerCadastraGrpcServiceImplBase() {

    override fun cadastra(request: CadastraKeyRequest, responseObserver: StreamObserver<CadastraKeyResponse>) {
        val novaChave = request.paraNovaChave()
        val chavePixSalva = cadastraKeyService.cadastra(novaChave)


        val response = CadastraKeyResponse.newBuilder()
            .setPixId(chavePixSalva.pixId.toString())
            .setClienteId(chavePixSalva.clienteId.toString())
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()


    }
}