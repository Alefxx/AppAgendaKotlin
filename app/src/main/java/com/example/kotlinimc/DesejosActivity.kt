package com.example.kotlinimc

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.exemplo.myapp.databinding.ActivityDesejosBinding

class DesejosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDesejosBinding
    private lateinit var banco: BancoHelper
    private var listaDesejos: List<Desejo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDesejosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        banco = BancoHelper(this)
        atualizarLista()

        // 1. SALVAR DESEJO
        binding.btnSalvarDesejo.setOnClickListener {
            val nome = binding.edtNomeDesejo.text.toString().trim()
            val prioridade = binding.spinnerPrioridade.selectedItem.toString()

            // Preço é opcional: se ficar vazio, toDoubleOrNull() retorna null sem dar crash
            val preco = binding.edtPrecoDesejo.text.toString()
                .replace(",", ".")
                .toDoubleOrNull()

            if (nome.isEmpty()) {
                Toast.makeText(this, "Informe o nome do desejo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            banco.inserirDesejo(Desejo(nome = nome, preco = preco, prioridade = prioridade))
            binding.edtNomeDesejo.text.clear()
            binding.edtPrecoDesejo.text.clear()
            atualizarLista()
        }

        // 2. CLIQUE NO ITEM -> EXCLUIR DESEJO
        binding.listViewDesejos.setOnItemClickListener { _, _, position, _ ->
            val desejo = listaDesejos[position]

            AlertDialog.Builder(this)
                .setTitle("Excluir Desejo")
                .setMessage("Remover '${desejo.nome}' da lista?")
                .setPositiveButton("Sim") { _, _ ->
                    banco.deletarDesejo(desejo.id)
                    atualizarLista()
                }
                .setNegativeButton("Não", null)
                .show()
        }
    }

    private fun atualizarLista() {
        listaDesejos = banco.listarDesejos()

        val textos = listaDesejos.map { d ->
            val precoTexto = if (d.preco != null) "R$ %.2f".format(d.preco) else "Preço não informado"
            "[Prioridade: ${d.prioridade}] ${d.nome}\n$precoTexto"
        }

        binding.listViewDesejos.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, textos)
    }
}
