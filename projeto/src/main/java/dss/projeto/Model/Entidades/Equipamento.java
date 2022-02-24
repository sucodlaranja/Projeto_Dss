package dss.projeto.Model.Entidades;

import java.io.Serializable;

///Esta classe representa um equipamento e contém as suas informações.
class Equipamento implements Serializable {

    /// Identificador de um equipamento.
    private final int id;
    /// Nome de um cliente.
    private final String nome;

    public Equipamento(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
