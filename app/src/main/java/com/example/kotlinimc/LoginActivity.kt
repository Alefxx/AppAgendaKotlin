package com.example.kotlinimc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exemplo.myapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var banco: BancoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        banco = BancoHelper(this)

        // Botão Entrar -> Consulta no SQLite
        binding.btnEntrar.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val senha = binding.edtSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Busca o usuário no banco
            val usuarioEncontrado = banco.validarLogin(email, senha)

            if (usuarioEncontrado != null) {
                // Envia o objeto Usuario completo para o Menu (para exibir o nome lá)
                val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("USUARIO_LOGADO", usuarioEncontrado)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão Criar Conta -> Vai para CadastroActivity
        binding.btnIrCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }
}
