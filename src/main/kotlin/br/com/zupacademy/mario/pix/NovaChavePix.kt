package br.com.zupacademy.mario.pix

import br.com.zupacademy.mario.integration.*
import br.com.zupacademy.mario.pix.validations.ValidPixKey
import br.com.zupacademy.mario.pix.validations.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NovaChavePix(
    @field:ValidUUID
    @field:NotNull
    val clienteId: String?,

    @field:NotNull
    val tipo: TipoDeChave?,

    @field:NotNull
    @field:Size(max=77)
    val chave:String?,

    @field:NotNull
    val tipoDeConta: TipoDeConta?

){
    fun paraCadastroBCB(contaAssociada: ContaAssociada):CadastraChaveRequest {
        return CadastraChaveRequest(
            keyType = tipo!!.paraEnumBcb().name,
            key = if(this.tipo == TipoDeChave.ALEATORIA) "" else this.chave!!,
            bankAccount = ContaRequest(
                participant = ContaAssociada.ispb,
                branch = contaAssociada.agencia,
                accountNumber = contaAssociada.numero,
                accountType = tipoDeConta!!.paraEnumBcb().name
            ), owner = TitularRequest(
                type = "NATURAL_PERSON",
                name = contaAssociada.titular,
                taxIdNumber = contaAssociada.cpfDoTitular
            )
        )
    }
}



