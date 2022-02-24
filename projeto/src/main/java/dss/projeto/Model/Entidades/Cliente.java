package dss.projeto.Model.Entidades;

import java.io.Serializable;

///Esta classe representa um cliente e contém as suas informações.
class Cliente implements Serializable {

    /// Identificador de um cliente.
    private final int id;
    /// Nome do cliente.
    private final String nome;
    /// Endereço eletrónico do cliente.
    private final String email;
    /// NIF do cliente.
    private final String nif;

    public Cliente(int id, String nome, String email, String nif) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.nif = nif;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getNif() {
        return nif;
    }
}
