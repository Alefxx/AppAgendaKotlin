package com.example.kotlinimc


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.exemplo.myapp.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recebe o usuário que veio do Login e mostra o nome salvo no banco
        @Suppress("DEPRECATION")
        val usuario = intent.getSerializableExtra("USUARIO_LOGADO") as? Usuario
        binding.txtBoasVindas.text = "Olá, ${usuario?.nome ?: "Usuário"}!"

        // Navegação para os 3 módulos (sem finish(), para o botão Voltar retornar ao Menu)
        binding.btnTarefas.setOnClickListener {
            startActivity(Intent(this, TarefasActivity::class.java))
        }

        binding.btnAgenda.setOnClickListener {
            startActivity(Intent(this, AgendaTelActivity::class.java))
        }

        binding.btnDesejos.setOnClickListener {
            startActivity(Intent(this, DesejosActivity::class.java))
        }

        // Sair -> Volta para o Login e fecha o Menu
        binding.btnSair.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
