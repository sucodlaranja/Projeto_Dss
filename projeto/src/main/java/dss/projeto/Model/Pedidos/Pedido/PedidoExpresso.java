package dss.projeto.Model.Pedidos.Pedido;


import dss.projeto.Model.Pedidos.Exceptions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoExpresso extends PedidoReparacao {

    /// Preço que está definido na loja.
    private final double precoFixo;
    /// Tecnico que executou a reparação
    private int idTecnico;
    /// Estado atual do pedido.
    private Estados estado;
    /// Prazo para terminar a fase atual do pedido.
    private LocalDateTime prazo;

    public PedidoExpresso(int id, int idCliente, int idEquipamento, int idEmpregadoBalcao, double precoFixo,String descricao) {
        super(id, idCliente, idEquipamento, idEmpregadoBalcao,descricao);
        this.precoFixo = precoFixo;
        this.estado = Estados.NecessitaReparacao;
        idTecnico = -1;
    }

    public Estados getEstado() {
        return estado;
    }

    public LocalDateTime getPrazo() { return prazo; }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }

    /**
     * Se o pedido existe muda o estado do pedido.
     */
    public void comecarReparacao() throws EstadoIncorreto {
        if (estado != Estados.NecessitaReparacao) throw new EstadoIncorreto(estado,Estados.NecessitaReparacao);
        estado = Estados.EmReparacao;
    }

    /**
     * Encontra o proximo passo de um orcamento.
     * @return O proximo passo.
     */
    public String getProximoPasso() throws EstadoIncorreto {
        if (estado != Estados.EmReparacao) throw new EstadoIncorreto(estado,Estados.EmReparacao);
        return "O pedido expresso é :\n " + super.getDescricao()
                +"\n Preço fixo: " + precoFixo + "€.\n";
    }

    /**
     * Encontra o proximo passo de um orcamento.
     * @return os passos.
     */
    public List<String> getInfoPassos() throws EstadoIncorreto, PlanoDeTrabalhoVazio {
        List<String> res = new ArrayList<>();
        res.add(getProximoPasso());    
        return res;
    }

    /**
     * Dá como terminado as alterações atuais ao orçamento.
     * @return Se o plano de trabalho já foi completo.
     */
    public boolean terminadoProximoPasso(int idTecnicoExecucao,double precoReal,double tempoPrevisto) throws EstadoIncorreto {
        idTecnico = idTecnicoExecucao;
        if (estado != Estados.EmReparacao) throw new EstadoIncorreto(estado,Estados.EmReparacao);
        estado = Estados.ProntoParaLevantar;
        this.prazo = LocalDateTime.now().plusDays(30);
        return true;
    }

    public double getPrecoReparacao() {
        return precoFixo;
    }

    public double getTempoReparacao() {
        return -1;
    }

    /**
     * O pedido foi terminado e vamos criar o record com a informação que desejamos guardar. \n
     * Não podemos guardar o \ref idEquipamento, pois este foi levantado da loja. \n
     * Não vamos guardar passos executados, pois é um pedido expresso e não existem.
     * @param equipamento O nome do equipamento.
     * @param idEmpregadoBalcaoEntrega O empregado de balcão que fez a entrega do equipamento.
     * @return A informação que queremos guardar deste pedido.
     */
    public InfoPedidoTerminado terminarPedido(boolean baixa,String equipamento, int idEmpregadoBalcaoEntrega) {
        return new InfoPedidoTerminado(LocalDateTime.now(),super.getDescricao(),true,baixa,super.getIdCliente(),equipamento,super.getIdEmpregadoBalcao(),
                idEmpregadoBalcaoEntrega,idTecnico,null);
    }

    public String toStringPedido(){
        return "Pedido Expresso -> Id: " + super.getId() + "\nId do Cliente: " + super.getIdCliente() + "\nId do Equipamento: " + super.getIdEquipamento()
                + "\nEstado: " + estado.toString()
                + "\nDescricao: " + super.getDescricao() + "\n";

    }
}
