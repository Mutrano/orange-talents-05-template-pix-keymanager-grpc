package br.com.zupacademy.mario.pix

import br.com.zupacademy.mario.integration.InstituicaoResponse
import br.com.zupacademy.mario.integration.TitularResponse
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class ContaAssociada(
    @Column(name = "conta_titular_cpf", nullable = false)
    val cpfDoTitular: String,

    @Column(name = "conta_instituicao", nullable = false)
    val instituicao: String,

    @Column(name = "conta_agencia", nullable = false)
    val agencia: String,

    @Column(name = "conta_numero", nullable = false)
    val numero: String,

    @Column(name = "conta_titular_nome", nullable = false)
    val titular: String,
) {

}