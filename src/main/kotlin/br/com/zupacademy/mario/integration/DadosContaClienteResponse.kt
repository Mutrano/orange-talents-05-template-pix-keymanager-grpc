package br.com.zupacademy.mario.integration

import br.com.zupacademy.mario.pix.ContaAssociada

data class DadosContaClienteResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse,
){
    fun paraConta(): ContaAssociada {
        return ContaAssociada(
            cpfDoTitular = titular.cpf,
            instituicao = instituicao.nome,
            agencia = agencia,
            numero = numero,
            titular = titular.nome
        )
    }
}

data class InstituicaoResponse(
    val nome: String,
    val ispb: String
)

data class TitularResponse(
    val id: String,
    val nome: String,
    val cpf: String
)
