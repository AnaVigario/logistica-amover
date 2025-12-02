package com.example.myamover.di

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Objeto utilitário responsável por gerar salts e calcular hashes seguros de palavras-passe.
// Usa o algoritmo PBKDF2 (Password-Based Key Derivation Function 2) com HMAC-SHA1.
object PasswordHasher {

    // Número de iterações (quanto maior, mais seguro, mas também mais lento).
    private const val INTERATIONS = 120_000
    // Tamanho da chave gerada, em bits.
    private const val KEY_LENGTH = 256
    // Algoritmo usado para derivar a chave.
    private const val ALGORITHM = "PBKDF2WithHmacSHA1"
    // Gerador de números aleatórios seguro, para criar salts.
    private val rng = SecureRandom()

    // Gera um salt aleatório com o número de bytes especificado (por defeito 16 bytes).
    fun generateSalt(bytes: Int = 16): ByteArray {
        val salt = ByteArray(bytes)
        rng.nextBytes(salt)
        return salt
    }

    // Calcula o hash de uma palavra-passe, dado um salt.
    // Recebe a palavra-passe como CharArray (mais seguro do que String, pois pode ser limpo da memória).
    fun hash(password: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password, salt, INTERATIONS, KEY_LENGTH)
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
    }

    // Calcula o hash de uma palavra-passe, recebendo a password em String e o salt em Base64.
    // Retorna o hash também codificado em Base64 (mais prático para guardar em base de dados).
    fun hashBase64(password: String, saltB64: String): String {
        val salt = Base64.decode(saltB64, Base64.NO_WRAP)
        val hash = hash(password.toCharArray(), salt)
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    // Gera um novo salt aleatório e devolve-o já codificado em Base64.
    fun newSaltBase64(): String =
        Base64.encodeToString(generateSalt(), Base64.NO_WRAP)

}