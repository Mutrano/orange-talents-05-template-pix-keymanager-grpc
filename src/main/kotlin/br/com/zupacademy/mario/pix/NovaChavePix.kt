package br.com.zupacademy.mario.pix

import br.com.zupacademy.mario.pix.validations.ValidPixKey
import br.com.zupacademy.mario.pix.validations.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class NovaChavePix(
    @field:ValidUUID
    @field:NotNull
    val clienteId: String?,

    @field:NotNull
    val tipo:TipoDeChave?,

    @field:NotNull
    @field:Size(max=77)
    val chave:String?,

    @field:NotNull
    val tipoDeConta: TipoDeConta?

){
    fun paraChave(contaAssociada: ContaAssociada):ChavePix{
        return ChavePix(
            clienteId = UUID.fromString(this.clienteId),
            tipo = TipoDeChave.valueOf(this.tipo!!.name),
            chave = if(this.tipo == TipoDeChave.ALEATORIA) UUID.randomUUID().toString() else this.chave!!,
            tipoDeConta = TipoDeConta.valueOf(this.tipoDeConta!!.name),
            contaAssociada = contaAssociada
        )
    }
}



