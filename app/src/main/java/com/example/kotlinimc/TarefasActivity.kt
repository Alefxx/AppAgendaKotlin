package com.example.kotlinimc

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinimc.databinding.ActivityTarefasBinding

class TarefasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTarefasBinding
    private lateinit var banco: BancoHelper
    private var listaTarefas: List<Tarefa> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTarefasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        banco = BancoHelper(this)
        atualizarTela()

        // 1. CRIAR TAREFA
        binding.btnAddTarefa.setOnClickListener {
            val titulo = binding.edtTarefa.text.toString().trim()
            val prazo = binding.spinnerPrazo.selectedItem.toString()

            if (titulo.isEmpty()) {
                Toast.makeText(this, "Digite a tarefa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            banco.inserirTarefa(Tarefa(titulo = titulo, prazo = prazo, concluida = false))
            binding.edtTarefa.text.clear()
            atualizarTela() // Atualiza lista e recalcula a % na hora
        }

        // 2. TOQUE SIMPLES -> MARCA OU DESMARCA CHECK E RECALCULA %
        binding.listViewTarefas.setOnItemClickListener { _, _, position, _ ->
            val tarefa = listaTarefas[position]
            val novoStatus = !tarefa.concluida // Inverte (se estava false vira true, e vice-versa)

            banco.atualizarCheckTarefa(tarefa.id, novoStatus)
            atualizarTela()
        }

        // 3. SEGURAR (LONG CLICK) -> EXCLUIR TAREFA
        binding.listViewTarefas.setOnItemLongClickListener { _, _, position, _ ->
            val tarefa = listaTarefas[position]

            AlertDialog.Builder(this)
                .setTitle("Excluir Tarefa")
                .setMessage("Deseja excluir '${tarefa.titulo}'?")
                .setPositiveButton("Sim") { _, _ ->
                    banco.deletarTarefa(tarefa.id)
                    atualizarTela()
                }
                .setNegativeButton("Não", null)
                .show()

            true // Obrigatório retornar true no LongClick
        }
    }

    // Recarrega a lista do banco e calcula a % em tempo real
    private fun atualizarTela() {
        listaTarefas = banco.listarTarefas()

        // Cálculo da porcentagem (evita divisão por zero quando a lista está vazia)
        val total = listaTarefas.size
        val concluidas = listaTarefas.count { it.concluida }
        val porcentagem = if (total > 0) (concluidas * 100) / total else 0

        binding.txtProgresso.text = "Concluído: $porcentagem% ($concluidas de $total)"
        binding.progressBar.progress = porcentagem

        // Formata cada linha com ícone de check visual
        val itensTexto = listaTarefas.map { t ->
            val check = if (t.concluida) "[✔]" else "[   ]"
            "$check ${t.titulo}  (${t.prazo})"
        }

        binding.listViewTarefas.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itensTexto)
    }
}
