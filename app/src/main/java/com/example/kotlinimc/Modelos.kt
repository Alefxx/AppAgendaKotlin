package com.example.kotlinimc

import java.io.Serializable

// 1. Modelo de Usuário (Login/Cadastro)
data class Usuario(
    val id: Int = 0,
    var nome: String,
    var email: String,
    var senha: String
) : Serializable

// 2. Modelo de Tarefa
data class Tarefa(
    val id: Int = 0,
    var titulo: String,
    var prazo: String,      // "Hoje", "Amanhã" ou "Depois"
    var concluida: Boolean  // true = marcada, false = pendente
) : Serializable

// 3. Modelo da Agenda Telefônica
data class Contato(
    val id: Int = 0,
    var nome: String,
    var numero: String
) : Serializable

// 4. Modelo da Lista de Desejos
data class Desejo(
    val id: Int = 0,
    var nome: String,
    var preco: Double?,     // Opcional (pode ser null se não preencher)
    var prioridade: String  // "Alta", "Média" ou "Baixa"
) : Serializable
