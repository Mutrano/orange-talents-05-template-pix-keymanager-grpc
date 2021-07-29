package br.com.zupacademy.mario.integration

import br.com.zupacademy.mario.pix.ChavePix
import br.com.zupacademy.mario.pix.ContaAssociada
import br.com.zupacademy.mario.pix.TipoDeChave
import br.com.zupacademy.mario.pix.TipoDeConta
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import java.time.LocalDateTime
import java.util.*

@Client("\${bcb.url}")
interface IntegracaoBCB {

    @Post(consumes = [MediaType.APPLICATION_XML], produces = [MediaType.APPLICATION_XML])
    fun cadastra(@Body request: CadastraChaveRequest): HttpResponse<CadastraChaveResponse>

    @Delete(value = "/{key}", consumes = [MediaType.APPLICATION_XML], produces = [MediaType.APPLICATION_XML])
    fun deleta(@PathVariable key: String, @Body request: DeletaChaveRequest): HttpResponse<DeletaChaveResponse>

}

class DeletaChaveRequest(
    val key: String,
    val participant: String,
) {
    companion object {
        fun of(chave: ChavePix): DeletaChaveRequest {
            return DeletaChaveRequest(
                key = chave.chave,
                participant = ContaAssociada.ispb
            )
        }
    }
}

class DeletaChaveResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)

data class CadastraChaveResponse(
    val keyType: String,
    val key: String,
    val bankAccount: ContaRequest,
    val owner: TitularRequest,
    val createdAt: LocalDateTime
) {


    fun paraChave(clienteId: String, instituicao: String): ChavePix {
        return ChavePix(
            clienteId = UUID.fromString(clienteId),
            tipo = PixKeyType.valueOf(keyType).paraEnumLocal(),// mapeia enum do bcb para o local
            chave = key,
            tipoDeConta = AccountType.valueOf(bankAccount.accountType)
                .paraEnumLocal(),// mapeia enum do BCB para o local
            contaAssociada = ContaAssociada(
                cpfDoTitular = owner.taxIdNumber,
                instituicao = instituicao,
                agencia = bankAccount.branch,
                numero = bankAccount.accountNumber,
                titular = owner.name
            )

        )
    }
}

data class CadastraChaveRequest(
    val keyType: String,
    val key: String,
    val bankAccount: ContaRequest,
    val owner: TitularRequest
)

data class TitularRequest(
    val type: String,
    val name: String,
    val taxIdNumber: String,
) {

}

data class ContaRequest(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: String
) {

}

enum class AccountType {
    CACC {
        override fun paraEnumLocal(): TipoDeConta {
            return TipoDeConta.CONTA_CORRENTE
        }

    },
    SGVS {
        override fun paraEnumLocal(): TipoDeConta {
            return TipoDeConta.CONTA_POUPANCA
        }

    };

    abstract fun paraEnumLocal(): TipoDeConta
}

enum class PixKeyType {
    CPF {
        override fun paraEnumLocal():TipoDeChave{
            return TipoDeChave.CPF
        }
    },
    CNPJ {
        override fun paraEnumLocal():TipoDeChave{
            return TipoDeChave.CPF
        }
    },
    PHONE {
        override fun paraEnumLocal():TipoDeChave{
            return TipoDeChave.CELULAR
        }
    },
    EMAIL {
        override fun paraEnumLocal():TipoDeChave{
            return TipoDeChave.EMAIL
        }
    },
    RANDOM {
        override fun paraEnumLocal():TipoDeChave{
            return TipoDeChave.ALEATORIA
        }
    };
    abstract fun paraEnumLocal() : TipoDeChave
}