package br.com.zupacademy.mario.pix.deleta

import br.com.zupacademy.mario.DeletaKeyRequest
import br.com.zupacademy.mario.DeletaKeyResponse
import br.com.zupacademy.mario.PixKeyManagerDeletaGrpcServiceGrpc
import br.com.zupacademy.mario.shared.handlers.ErrorAroundHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@ErrorAroundHandler
@Singleton
class DeletaKeyServerGrpc(val service: DeletaKeyService) : PixKeyManagerDeletaGrpcServiceGrpc.PixKeyManagerDeletaGrpcServiceImplBase() {


    override fun deleta(request: DeletaKeyRequest, responseObserver: StreamObserver<DeletaKeyResponse>) {
        service.deleta(clienteId = request.clienteId, pixId = request.pixId)

        responseObserver.onNext(DeletaKeyResponse.newBuilder()
                .setClienteId(request.clienteId)
                .setPixId(request.pixId)
                .build())

        responseObserver.onCompleted()
    }
}