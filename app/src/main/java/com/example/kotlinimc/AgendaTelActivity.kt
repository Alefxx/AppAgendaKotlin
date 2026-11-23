package com.example.kotlinimc


import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.exemplo.myapp.databinding.ActivityAgendaTelBinding

class AgendaTelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgendaTelBinding
    private lateinit var banco: BancoHelper
    private var listaContatos: List<Contato> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendaTelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        banco = BancoHelper(this)
        atualizarLista()

        // 1. SALVAR CONTATO
        binding.btnSalvarContato.setOnClickListener {
            val nome = binding.edtNomeContato.text.toString().trim()
            val numero = binding.edtNumeroContato.text.toString().trim()

            if (nome.isEmpty() || numero.isEmpty()) {
                Toast.makeText(this, "Preencha nome e número", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            banco.inserirContato(Contato(nome = nome, numero = numero))
            binding.edtNomeContato.text.clear()
            binding.edtNumeroContato.text.clear()
            atualizarLista(binding.edtPesquisar.text.toString())
        }

        // 2. PESQUISA EM TEMPO REAL (já volta ordenado de A-Z pelo BancoHelper)
        binding.edtPesquisar.doAfterTextChanged { texto ->
            atualizarLista(texto.toString())
        }

        // 3. CLIQUE NO CONTATO -> MENU EDITAR OU EXCLUIR
        binding.listViewContatos.setOnItemClickListener { _, _, position, _ ->
            val contato = listaContatos[position]

            AlertDialog.Builder(this)
                .setTitle(contato.nome)
                .setItems(arrayOf("Editar", "Excluir")) { _, opcao ->
                    if (opcao == 0) abrirDialogEditar(contato) else excluirContato(contato)
                }
                .show()
        }
    }

    private fun atualizarLista(filtro: String = "") {
        listaContatos = banco.listarContatos(filtro) // Já traz em ordem alfabética
        val textos = listaContatos.map { "${it.nome}\nTel: ${it.numero}" }
        binding.listViewContatos.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, textos)
    }

    private fun abrirDialogEditar(contato: Contato) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }
        val edtNome = EditText(this).apply { setText(contato.nome) }
        val edtNumero = EditText(this).apply { setText(contato.numero) }
        layout.addView(edtNome)
        layout.addView(edtNumero)

        AlertDialog.Builder(this)
            .setTitle("Editar Contato")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                contato.nome = edtNome.text.toString().trim()
                contato.numero = edtNumero.text.toString().trim()
                banco.atualizarContato(contato)
                atualizarLista(binding.edtPesquisar.text.toString())
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun excluirContato(contato: Contato) {
        banco.deletarContato(contato.id)
        atualizarLista(binding.edtPesquisar.text.toString())
        Toast.makeText(this, "Contato excluído", Toast.LENGTH_SHORT).show()
    }
}
