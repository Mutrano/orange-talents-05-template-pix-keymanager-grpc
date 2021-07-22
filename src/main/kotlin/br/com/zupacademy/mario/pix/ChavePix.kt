package br.com.zupacademy.mario.pix

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ChavePix(
    @field:NotNull
    @Column(nullable = false)
    val clienteId: UUID,

    @field:NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipo: TipoDeChave,

    @field:NotBlank
    @Column(unique = true,nullable = false,length = 77)
    val chave: String,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoDeConta: TipoDeConta,

    @Embedded
    val contaAssociada:ContaAssociada
) {
    @Id
    @GeneratedValue
    var id :Long? = null

    val pixId :UUID = UUID.randomUUID()

}

