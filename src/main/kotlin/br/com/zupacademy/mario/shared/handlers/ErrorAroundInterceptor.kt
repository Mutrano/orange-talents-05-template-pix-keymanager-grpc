package br.com.zupacademy.mario.shared.handlers

import br.com.zupacademy.mario.pix.exceptions.ChaveJaExistenteException
import br.com.zupacademy.mario.pix.exceptions.ChaveNaoEhDoClienteException
import br.com.zupacademy.mario.pix.exceptions.ChaveNaoEncontradaException
import br.com.zupacademy.mario.pix.exceptions.ClienteNaoEncontradoException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorAroundHandler::class)
class ErrorAroundHandlerInterceptor : MethodInterceptor<Any, Any> {

    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {

        try {
            return context.proceed()
        } catch (ex: Exception) {

            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status = when(ex) {
                is ConstraintViolationException -> Status.INVALID_ARGUMENT
                    .withCause(ex)
                    .withDescription(ex.message)
                is ChaveJaExistenteException -> Status.ALREADY_EXISTS
                    .withCause(ex)
                    .withDescription(ex.message)
                is ClienteNaoEncontradoException -> Status.NOT_FOUND
                    .withCause(ex)
                    .withDescription(ex.message)
                is ChaveNaoEncontradaException -> Status.NOT_FOUND
                    .withCause(ex)
                    .withDescription(ex.message)
                is ChaveNaoEhDoClienteException -> Status.FAILED_PRECONDITION
                    .withCause(ex)
                    .withDescription(ex.message)
                else -> Status.UNKNOWN
                    .withCause(ex)
                    .withDescription("Ops, um erro inesperado ocorreu")
            }

            responseObserver.onError(status.asRuntimeException())
        }

        return null
    }

}