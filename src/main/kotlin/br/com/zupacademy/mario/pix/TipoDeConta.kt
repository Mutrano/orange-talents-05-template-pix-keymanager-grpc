package br.com.zupacademy.mario.pix

import br.com.zupacademy.mario.integration.AccountType

enum class TipoDeConta {
    CONTA_CORRENTE{
        override fun paraEnumBcb():AccountType {
            return AccountType.CACC
        }

    },
    CONTA_POUPANCA{
        override fun paraEnumBcb():AccountType {
            return AccountType.SGVS
        }
    };
    abstract fun paraEnumBcb():AccountType
}
