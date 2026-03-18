package com.example.myamover.route.utils

import android.content.Context

/**
 * AssetsUtils
 *
 * Funções utilitárias relacionadas com ficheiros
 * guardados na pasta /assets da aplicação Android.
 *
 * Este ficheiro é normalmente usado para:
 * - carregar rotas em JSON
 * - carregar dados de teste
 * - simular respostas do backend em modo offline
 */

/**
 * Lê um ficheiro JSON localizado na pasta assets
 * e devolve o seu conteúdo como String.
 *
 * @param context  Contexto da aplicação (necessário para aceder aos assets)
 * @param fileName Nome do ficheiro dentro da pasta assets
 *                 Exemplo: "route_v1.json"
 *
 * @return Conteúdo completo do ficheiro em formato String
 *
 * Exemplo de uso:
 * val json = loadJsonFromAssets(context, "route.json")
 */

//Ler o json
fun loadJsonFromAssets(context: Context, fileName: String): String {

    // Abre o ficheiro nos assets,
    // cria um BufferedReader
    // e lê todo o conteúdo para String
    return context.assets
        .open(fileName)
        .bufferedReader()
        .use { it.readText() }
}

/*
Testar rotas sem backend

✔ Desenvolver o MapScreen offline
✔ Carregar dados mockados
✔ Comparar versões de rotas (A/B)

 Nota importante, esta função:

não deve ser usada para ficheiros grandes
não deve ser chamada repetidamente em UI thread

Se for preciso performance:

usar cache em memória ou carrega apenas uma vez no ViewModel
 */