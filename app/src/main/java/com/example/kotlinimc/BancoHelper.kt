package com.example.kotlinimc

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BancoHelper(context: Context) : SQLiteOpenHelper(context, "myapp.db", null, 1) {

    // Cria as 4 tabelas na primeira vez que o app abre
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                email TEXT UNIQUE,
                senha TEXT
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE tarefas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT,
                prazo TEXT,
                concluida INTEGER
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE contatos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                numero TEXT
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE desejos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT,
                preco REAL,
                prioridade TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS tarefas")
        db.execSQL("DROP TABLE IF EXISTS contatos")
        db.execSQL("DROP TABLE IF EXISTS desejos")
        onCreate(db)
    }

    // ==================== 1. USUÁRIOS (Cadastro e Login) ====================
    fun cadastrarUsuario(u: Usuario): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("nome", u.nome)
            put("email", u.email)
            put("senha", u.senha)
        }
        val res = db.insert("usuarios", null, cv)
        db.close()
        return res != -1L
    }

    fun validarLogin(email: String, senha: String): Usuario? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE email = ? AND senha = ?", arrayOf(email, senha))
        var user: Usuario? = null
        if (cursor.moveToFirst()) {
            user = Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nome = cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                senha = cursor.getString(cursor.getColumnIndexOrThrow("senha"))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    // ==================== 2. TAREFAS (Criar, Listar, Check, Excluir) ====================
    fun inserirTarefa(t: Tarefa) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("titulo", t.titulo)
            put("prazo", t.prazo)
            put("concluida", if (t.concluida) 1 else 0) // SQLite guarda Boolean como 1 ou 0
        }
        db.insert("tarefas", null, cv)
        db.close()
    }

    fun listarTarefas(): List<Tarefa> {
        val lista = mutableListOf<Tarefa>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Tarefa(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                        prazo = cursor.getString(cursor.getColumnIndexOrThrow("prazo")),
                        concluida = cursor.getInt(cursor.getColumnIndexOrThrow("concluida")) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun atualizarCheckTarefa(id: Int, concluida: Boolean) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("concluida", if (concluida) 1 else 0)
        }
        db.update("tarefas", cv, "id = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deletarTarefa(id: Int) {
        val db = writableDatabase
        db.delete("tarefas", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    // ==================== 3. AGENDATEL (CRUD + Ordem Alfabética + Pesquisa) ====================
    fun inserirContato(c: Contato) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("nome", c.nome)
            put("numero", c.numero)
        }
        db.insert("contatos", null, cv)
        db.close()
    }

    fun listarContatos(filtro: String = ""): List<Contato> {
        val lista = mutableListOf<Contato>()
        val db = readableDatabase
        // ORDER BY nome COLLATE NOCASE ASC garante ordem alfabética ignorando maiúscula/minúscula
        val cursor = if (filtro.isBlank()) {
            db.rawQuery("SELECT * FROM contatos ORDER BY nome COLLATE NOCASE ASC", null)
        } else {
            db.rawQuery(
                "SELECT * FROM contatos WHERE nome LIKE ? OR numero LIKE ? ORDER BY nome COLLATE NOCASE ASC",
                arrayOf("%$filtro%", "%$filtro%")
            )
        }
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Contato(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        nome = cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                        numero = cursor.getString(cursor.getColumnIndexOrThrow("numero"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun atualizarContato(c: Contato) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("nome", c.nome)
            put("numero", c.numero)
        }
        db.update("contatos", cv, "id = ?", arrayOf(c.id.toString()))
        db.close()
    }

    fun deletarContato(id: Int) {
        val db = writableDatabase
        db.delete("contatos", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    // ==================== 4. LISTA DE DESEJOS (Criar, Listar, Excluir) ====================
    fun inserirDesejo(d: Desejo) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("nome", d.nome)
            if (d.preco != null) put("preco", d.preco) else putNull("preco")
            put("prioridade", d.prioridade)
        }
        db.insert("desejos", null, cv)
        db.close()
    }

    fun listarDesejos(): List<Desejo> {
        val lista = mutableListOf<Desejo>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM desejos ORDER BY id DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val idxPreco = cursor.getColumnIndexOrThrow("preco")
                val precoSalvo = if (cursor.isNull(idxPreco)) null else cursor.getDouble(idxPreco)

                lista.add(
                    Desejo(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        nome = cursor.getString(cursor.getColumnIndexOrThrow("nome")),
                        preco = precoSalvo,
                        prioridade = cursor.getString(cursor.getColumnIndexOrThrow("prioridade"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun deletarDesejo(id: Int) {
        val db = writableDatabase
        db.delete("desejos", "id = ?", arrayOf(id.toString()))
        db.close()
    }
}
