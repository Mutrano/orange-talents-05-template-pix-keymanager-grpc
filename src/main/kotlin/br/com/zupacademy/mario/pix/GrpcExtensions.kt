package br.com.zupacademy.mario.pix

import br.com.zupacademy.mario.CadastraKeyRequest
import br.com.zupacademy.mario.TipoDeChave.UNKNOWN_TIPO_CHAVE
import br.com.zupacademy.mario.TipoDeConta.UNKNOWN_TIPO_CONTA

fun CadastraKeyRequest.paraNovaChave(): NovaChavePix {
        return NovaChavePix(
            clienteId = clienteId,
            tipo = when(tipoDeChave) {
                UNKNOWN_TIPO_CHAVE -> null
                else -> TipoDeChave.valueOf(tipoDeChave.name)
            },

            chave = chave,
            tipoDeConta = when(tipoDeConta){
                UNKNOWN_TIPO_CONTA -> null
                else -> TipoDeConta.valueOf(tipoDeConta.name)
            }
        )
    }
