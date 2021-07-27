package br.com.zupacademy.mario.pix.cadastra

import br.com.zupacademy.mario.pix.TipoDeChave
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoDeChaveValidationTest {

    /*
    * cpf em branco
    * cpf nao da match na regex
    *  cpf valido com match na regex ( happy path)
    * cpf invalido com match na regex
    * cpf com letras não deve dar match na regex
    * */
    @Nested
    inner class CPF {

        @Test
        fun `Nao deve aceitar cpf em branco ou nulo`() {
            with(TipoDeChave.CPF) {
                assertFalse(valida(""))
                assertFalse(valida(null))
            }
        }

        @Test
        fun `Nao deve aceitar cpf que nao da match na regex`() {
            val chave = "012345678910"
            with(TipoDeChave.CPF) {
                assertFalse(valida(chave))
            }
        }

        @Test
        fun `Deve aceitar cpf valido`() {
            val chave = "26481207002"
            with(TipoDeChave.CPF) {
                assertTrue(valida(chave))
            }
        }

        @Test
        fun `Nao deve aceitar cpf inválido com match na regex`() {
            val chave = "26481207005"
            with(TipoDeChave.CPF) {
                assertFalse(valida(chave))
            }
        }

        @Test
        fun `Nao deve aceitar cpf com letras`() {
            val chave = "2648120700A"
            with(TipoDeChave.CPF) {
                assertFalse(valida(chave))
            }
        }
    }

    /*
    * Não deve aceitar email em branco ou nulo
    * Não deve aceitar email inválido
    * Deve aceitar email válido
    * */
    @Nested
    inner class EMAIL {

        @Test
        fun `Nao deve aceitar email em branco ou nulo`() {
            with(TipoDeChave.EMAIL) {
                assertFalse(valida(""))
                assertFalse(valida(null))
            }
        }

        @Test
        fun `deve ser valido quando email for endereco valido`() {
            with(TipoDeChave.EMAIL) {
                assertTrue(valida("mario.rabelo@zup.com.br"))
            }
        }

        @Test
        fun `Nao deve aceitar email invalido`() {
            with(TipoDeChave.EMAIL) {
                assertFalse(valida("mario.zup.com.br"))
                assertFalse(valida("mario.zup@"))
                assertFalse(valida("@zup"))
                assertFalse(valida("@zup.com.br"))
            }
        }

    }

    /*
    * não deve aceitar celular nulo ou em branco
    *  não deve aceitar celular com mais nuemros do que o padrão
    * não deve aceitar celular com letras
    * */
    @Nested
    inner class CELULAR {

        @Test
        fun `nao deve aceitar celular nulo ou em branco`() {
            with(TipoDeChave.CELULAR) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve aceitar celular com mais numeros do que o padrao`() {
            with(TipoDeChave.CELULAR) {
                assertFalse(valida("123456789123456"))
            }
        }

        @Test
        fun `numeros dentro do padrao devem ser validos`() {
            with(TipoDeChave.CELULAR) {
                assertTrue(valida("+61994047094")) // numero com DDD e sem DDI
                assertTrue(valida("+55061994047094"))// numero com 0 no DDD e com DDI
                assertTrue(valida("+5561994047094"))// numero sem 0 no DDD e com DDI
                assertTrue(valida("+994047094")) // numero sem DDD e sem  DDI
            }
        }

        @Test
        fun `nao deve aceitar numero sem o +`() {
            with(TipoDeChave.CELULAR) {
                assertFalse(valida("5561993260446"))
                assertFalse(valida("61993260446"))
            }
        }

        @Test
        fun `numero nao pode comecar com 0`() {
            with(TipoDeChave.CELULAR) {
                assertFalse(valida("+061994047094"))
            }
        }
    }
    /*
    * deve aceitar chave nula ou vazia
    * Não deve aceitar chave ja preenchida
    * */

    @Nested
    inner class ALEATORIA {

        @Test
        fun `deve aceitar chave nula ou vazia`() {
            with(TipoDeChave.ALEATORIA) {
                assertTrue(valida(""))
                assertTrue(valida(null))
            }
        }

        @Test
        fun `nao deve aceitar chave ja preenchida`(){
            with(TipoDeChave.ALEATORIA){
                assertFalse(valida("chave preenchida"))
            }
        }

    }

}