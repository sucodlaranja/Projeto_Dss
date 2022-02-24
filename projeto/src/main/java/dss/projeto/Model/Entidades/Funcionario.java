package dss.projeto.Model.Entidades;

import java.io.Serializable;

class Funcionario implements Serializable {

    /// Identificador de um funcionario.
    private final int id;
    /// Nome de um funcionario.
    private final String nome;
    /// Senha de um funcionario.
    private final String password;

    /// Se este funcionario desempenha a funcao de gestor.
    private final boolean gestor;
    /// Se este funcionario desempenha a funcao de tecnico.
    private final boolean tecnico;
    /// Se este funcionario desempenha a funcao de empregadoBalcao.
    private final boolean empregadoBalcao;

    public Funcionario(int id, String nome, String password, boolean gestor, boolean tecnico, boolean empregadoBalcao) {
        this.id = id;
        this.nome = nome;
        this.password = password;
        this.gestor = gestor;
        this.tecnico = tecnico;
        this.empregadoBalcao = empregadoBalcao;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getPassword() {
        return password;
    }

    public boolean isGestor() {
        return gestor;
    }

    public boolean isTecnico() {
        return tecnico;
    }

    public boolean isEmpregadoBalcao() {
        return empregadoBalcao;
    }
}
