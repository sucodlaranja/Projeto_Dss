package dss.projeto.Model.Pedidos.Pedido;


import dss.projeto.Model.Pedidos.Exceptions.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/// Representa um pedido de reparação.
public abstract class PedidoReparacao implements Serializable {

    /// Identificador do pedido.
    private final int id;
    /// Identificador do Cliente que fez o pedido.
    private final int idCliente;
    /// Identificador do Equipamento a que este pedido se refere.
    private final int idEquipamento;
    /// Identificador do Empregado de Balcao que gerou o pedido.
    private final int idEmpregadoBalcao;
    /// Descricao do pedido
    private final String descricao;
    /// A data de criação de um pedido
    private final LocalDateTime dataCriacao;

    public PedidoReparacao(int id, int idCliente, int idEquipamento, int idEmpregadoBalcao,String descricao) {
        this.id = id;
        this.idCliente = idCliente;
        this.idEquipamento = idEquipamento;
        this.idEmpregadoBalcao = idEmpregadoBalcao;
        this.descricao = descricao;
        this.dataCriacao = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdEquipamento() {
        return idEquipamento;
    }

    public int getIdEmpregadoBalcao() {
        return idEmpregadoBalcao;
    }

    public String getDescricao() {
        return descricao;
    }

    public abstract Estados getEstado();

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    /**
     * Verifica se o pedido está no prazo ou não
     * @param agora a data atual.
     */
    public void verificaPrazo(LocalDateTime agora){
        if ((getEstado() == Estados.EsperaConfirmacaoOrcamento || getEstado() == Estados.ProntoParaLevantar)
                && agora.isAfter(getPrazo())) setEstado(Estados.NecessitaBaixa);
    }

    /**
     * Verifica se o Pedido esta a espera de reparacao.
     */
    public boolean esperaReparacao() {
        return getEstado() == Estados.EmReparacao;
    }

    public abstract void setEstado(Estados estado);

    public abstract String toStringPedido();

    public abstract LocalDateTime getPrazo();

    public abstract double getPrecoReparacao();

    public abstract double getTempoReparacao();

    /**
     * Se o pedido existe muda o estado do pedido.
     */
    public abstract void comecarReparacao() throws EstadoIncorreto;

    /**
     * Encontra o proximo passo de um orcamento.
     * @return O proximo passo.
     */
    public abstract String getProximoPasso() throws EstadoIncorreto, PlanoDeTrabalhoVazio;

    /**
     * Encontra o proximo passo de um orcamento.
     * @return os passos.
     */
    public abstract List<String> getInfoPassos() throws EstadoIncorreto, PlanoDeTrabalhoVazio;

    /**
     * Dá como terminado as alterações atuais ao orçamento.
     * @return Se o plano de trabalho já foi completo ou não.
     */
    public abstract boolean terminadoProximoPasso(int idTecnicoExecucao,double precoReal,double tempoPrevisto) throws EstadoIncorreto, PlanoDeTrabalhoVazio;


    /**
     * O pedido foi terminado e vamos criar o record com a informação que desejamos guardar. \n
     * Não podemos guardar o \ref idEquipamento, pois este foi levantado da loja.
     * @param equipamento O nome do equipamento.
     * @param idEmpregadoBalcaoEntrega O empregado de balcão que fez a entrega do equipamento.
     * @return A informação que queremos guardar deste pedido.
     * @throws PedidoNaoTerminado Se o pedido não foi concluido.
     */
    abstract public InfoPedidoTerminado terminarPedido(boolean baixa,String equipamento, int idEmpregadoBalcaoEntrega)
            throws PedidoNaoTerminado;
}
