package dss.projeto.Model.Pedidos.Pedido;

import java.io.Serializable;

/// Este record descreve um passo de um plano de trabalho.
public class Passo implements Serializable {

    /// Descricao do passo.
    private final String descricao;

    /// Identificador do tecnico que gerou o passo.
    private final int idTecnicoPlaneamento;
    /// Preço previsto das peças para a execução do passo.
    private final double precoPrevisto;
    /// Tempo previsto para a execução do passo.
    private final double tempoPrevisto;

    /// Identificador do tecnico que executou o passo.
    private int idTecnicoExecucao;
    /// Preço real das peças para a execução do passo.
    private double precoReal;
    /// Tempo real da execução do passo.
    private double tempoReal;

    public Passo(String descricao, int idTecnicoPlaneamento, double precoPrevisto, double tempoPrevisto) {
        this.descricao = descricao;
        this.idTecnicoPlaneamento = idTecnicoPlaneamento;
        this.precoPrevisto = precoPrevisto;
        this.tempoPrevisto = tempoPrevisto;

        this.idTecnicoExecucao = -1;
        this.precoReal = this.tempoReal = -1;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPrecoPrevisto() {
        return precoPrevisto;
    }

    public double getTempoPrevisto() {
        return tempoPrevisto;
    }

    public int getIdTecnicoExecucao() {
        return idTecnicoExecucao;
    }

    public double getPrecoReal() {
        return precoReal;
    }

    public double getTempoReal() {
        return tempoReal;
    }

    public void setIdTecnicoExecucao(int idTecnicoExecucao) {
        this.idTecnicoExecucao = idTecnicoExecucao;
    }

    public void setPrecoReal(double precoReal) {
        this.precoReal = precoReal;
    }

    public void setTempoReal(double tempoReal) {
        this.tempoReal = tempoReal;
    }

    /// O que está planeado a fazer neste passo.
    public String toString(int i) {
        return "Passo " + i + " :\n " + descricao
                +"\n Preço previsto: " + precoPrevisto + "€."
                +"\n Tempo previsto:" + tempoPrevisto + " horas.\n";
    }

    /// O que está planeado a fazer neste passo (para ser usado em loop).
    public String toStringPassoPlano() {
        return "O próximo passo é :\n " + descricao
                +"\n Preço previsto: " + precoPrevisto + "€."
                +"\n Tempo previsto:" + tempoPrevisto + " horas.\n";
    }

    /// Passar o passo concluido para texto.
    public String toStringPassoConcluido() {
        return descricao
                +" Preço previsto/real: " + precoPrevisto + "€/" + precoReal + "€."
                +" Tempo previsto/real:" + tempoPrevisto + " horas/" + tempoReal + "horas.\n";
    }


}
