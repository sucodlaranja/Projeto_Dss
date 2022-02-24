package dss.projeto.Model.Pedidos.Pedido;


import dss.projeto.Model.Pedidos.Exceptions.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/// Esta classe representa um orçamento de um pedido.
public class Orcamento implements Serializable {

    /// Plano de trabalho para a reparação do equipamento.
    Queue<Passo> planoTrabalho;
    /// Passos que já foram executados.
    List<Passo> passosExecutados;

    /// Construtor para iniciar um orçamento.
    public Orcamento(){
        planoTrabalho = new LinkedList<>();
        passosExecutados = new ArrayList<>();
    }

    /**
     * Obtém a informação atual do orcamento e muda o seu estado.
     * @return A informação atual do pedido.
     */
    public String toString()  {
        AtomicInteger i = new AtomicInteger(1);
        return planoTrabalho.stream().map(passo -> passo.toString(i.getAndIncrement())).collect(Collectors.joining("-----------\n"));
    }

    /**
     * Encontra o proximo passo de um orcamento.
     * @return os passos.
     */
    public List<String> getInfoPassos() throws PlanoDeTrabalhoVazio {
        if (planoTrabalho.isEmpty()) throw new PlanoDeTrabalhoVazio();
        return planoTrabalho.stream().map(Passo::toStringPassoPlano).collect(Collectors.toList());
    }

    /**
     * Adiciona o passo ao plano de trabalho;
     * @param descricao Descricao do passo.
     * @param idTecnicoPlaneamento Identificador do tecnico que gerou o passo.
     * @param precoPrevisto Preço previsto das peças para a execução do passo.
     * @param tempoPrevisto Tempo previsto para a execução do passo.
     */
    public void addPassoPlanoTrabalho(String descricao, int idTecnicoPlaneamento,
                                      double precoPrevisto, double tempoPrevisto){
        planoTrabalho.add(new Passo(descricao, idTecnicoPlaneamento, precoPrevisto, tempoPrevisto));
    }

    public double getPrecoOrcamento() {
        return planoTrabalho.stream().mapToDouble(Passo::getPrecoPrevisto).sum();
    }

    public double getTempoOrcamento() {
        return planoTrabalho.stream().mapToDouble(Passo::getTempoPrevisto).sum();
    }

    public double getPrecoReparacao() {
        return passosExecutados.stream().mapToDouble(Passo::getPrecoReal).sum();
    }

    public double getTempoReparacao() {
        return passosExecutados.stream().mapToDouble(Passo::getTempoReal).sum();
    }

    /**
     * Encontra o próximo passo do plano de trabalho
     * @return A descrição do próximo passo.
     */
    public String getNextPasso() throws PlanoDeTrabalhoVazio {
        if (planoTrabalho.isEmpty()) throw new PlanoDeTrabalhoVazio();
        return planoTrabalho.peek().toStringPassoPlano();
    }

    /**
     * Confirma a execução do próximo passo do plano de trabalho.
     * @param idTecnicoExecucao Identificador do tecnico que executou o passo.
     * @param precoReal Preço real das peças para a execução do passo.
     * @param tempoReal Tempo real da execução do passo.
     * @return Se o plano de trabalho já foi concluido
     */
    public boolean nextPassoExecutados(int idTecnicoExecucao, double precoReal, double tempoReal) throws PlanoDeTrabalhoVazio {
        Passo p = planoTrabalho.poll();
        if (p == null) throw new PlanoDeTrabalhoVazio();
        p.setIdTecnicoExecucao(idTecnicoExecucao);
        p.setPrecoReal(precoReal);
        p.setTempoReal(tempoReal);
        passosExecutados.add(p);
        return planoTrabalho.isEmpty();
    }

    /**
     * Se o plano de trabalho foi concluido vamos devolver os passos que foram concluídos.
     * @return A lista de passos executados.
     */
    public List<Passo> getPassosExecutados() {
        if(!planoTrabalho.isEmpty()) return null;
        return passosExecutados;
    }


}
