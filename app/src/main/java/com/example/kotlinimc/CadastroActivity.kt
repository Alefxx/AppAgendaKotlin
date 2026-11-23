package com.example.kotlinimc

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinimc.databinding.ActivityCadastroBinding

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var banco: BancoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        banco = BancoHelper(this)

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.edtNome.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val senha = binding.edtSenha.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val novoUsuario = Usuario(nome = nome, email = email, senha = senha)

            if (banco.cadastrarUsuario(novoUsuario)) {
                Toast.makeText(this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                finish() // Fecha o Cadastro e volta direto para o Login
            } else {
                Toast.makeText(this, "Erro: E-mail já cadastrado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
