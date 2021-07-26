package br.com.zupacademy.mario.pix


import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoDeChave {
    CPF{
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()) return false

            val regex = """^[0-9]{11}$""".toRegex()

            if(!chave.matches(regex)) return false

            return CPFValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }

    },
    CELULAR{
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()) return false
            val regex = """^\+[1-9][0-9]\d{1,14}$""".toRegex()
            return chave.matches(regex)
        }

    },
    EMAIL{
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()) return false
            return EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }

    },
    ALEATORIA{
        override fun valida(chave: String?): Boolean {
            return chave.isNullOrBlank()  //não deve estar preenchida
        }

    };
    abstract fun valida(chave:String?):Boolean
}
