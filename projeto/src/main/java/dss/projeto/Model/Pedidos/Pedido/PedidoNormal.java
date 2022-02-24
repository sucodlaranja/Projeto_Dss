package dss.projeto.Model.Pedidos.Pedido;

import dss.projeto.Model.Pedidos.Exceptions.*;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoNormal extends PedidoReparacao {

    /// Orçamento deste pedido. Contém o Plano de trabalho.
    private final Orcamento orcamento;
    /// Estado atual do pedido.
    private Estados estado;
    /// Prazo para terminar a fase atual do pedido.
    private LocalDateTime prazo;

    public PedidoNormal(int id, int idCliente, int idEquipamento, int idEmpregadoBalcao,String descricao) {
        super(id, idCliente, idEquipamento, idEmpregadoBalcao,descricao);
        this.orcamento = new Orcamento();
        this.estado = Estados.OrcamentoPorCalcular;
        this.prazo = LocalDateTime.now().plusDays(30);
    }

    public Estados getEstado() {
        return estado;
    }

    public LocalDateTime getPrazo() { return prazo; }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }

    /**
     * Obtém a informação atual do orcamento e muda o seu estado.
     */
    public void comecarOrcamento() throws EstadoIncorreto {
        if (estado != Estados.OrcamentoPorCalcular) throw new EstadoIncorreto(estado,Estados.OrcamentoPorCalcular);
        estado = Estados.ACalcularOrcamento;
    }

    /**
     * Obtém a informação atual do orcamento e muda o seu estado.
     * @return A informação atual do pedido.
     */
    public String verOrcamento() throws EstadoIncorreto {
        if (estado != Estados.ACalcularOrcamento) throw new EstadoIncorreto(estado,Estados.ACalcularOrcamento);
        return orcamento.toString();
    }

    /**
     * Adiciona um passo ao orcamento.
     */
    public void addPassoOrcamento(String descricao, int idTecnicoPlaneamento, double precoPrevisto, double tempoPrevisto) throws EstadoIncorreto {
        if (estado != Estados.ACalcularOrcamento) throw new EstadoIncorreto(estado,Estados.ACalcularOrcamento);
        orcamento.addPassoPlanoTrabalho(descricao,idTecnicoPlaneamento,precoPrevisto,tempoPrevisto);
    }

    /**
     * Dá como terminado as alterações atuais ao orçamento.
     * @param estaAcabado Se o orçamento deve passar a seguinte fase ou ainda não foi terminado.
     */
    public void acabaOrcamento(boolean estaAcabado) throws EstadoIncorreto {
        if (estado != Estados.ACalcularOrcamento) throw new EstadoIncorreto(estado,Estados.ACalcularOrcamento);
        if (estaAcabado) {
            estado = Estados.EsperaConfirmacaoOrcamento;
            this.prazo = LocalDateTime.now().plusDays(30);
        }
        else estado = Estados.OrcamentoPorCalcular;
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
    public String getProximoPasso() throws EstadoIncorreto, PlanoDeTrabalhoVazio {
        if (estado != Estados.EmReparacao) throw new EstadoIncorreto(estado,Estados.EmReparacao);
        return orcamento.getNextPasso();
    }

    /**
     * Encontra o proximo passo de um orcamento.
     * @return os passos.
     */
    public List<String> getInfoPassos() throws EstadoIncorreto, PlanoDeTrabalhoVazio {
        return orcamento.getInfoPassos();
    }

    /**
     * Dá como terminado as alterações atuais ao orçamento.
     * @return Se o plano de trabalho já foi completo.
     */
    public boolean terminadoProximoPasso(int idTecnicoExecucao,double precoReal,double tempoPrevisto) throws PlanoDeTrabalhoVazio, EstadoIncorreto {
        // TODO :mandar email.
        if (estado != Estados.EmReparacao) throw new EstadoIncorreto(estado,Estados.EmReparacao);

        boolean acabou = orcamento.nextPassoExecutados(idTecnicoExecucao,precoReal,tempoPrevisto);
        if (acabou) {
            estado = Estados.ProntoParaLevantar;
            this.prazo = LocalDateTime.now().plusDays(30);
        }
        return acabou;
    }

    /**
     * Pedido esta a espera de orcamento.
     */
    public boolean esperaOrcamento(){
        return estado == Estados.ACalcularOrcamento;
    }

    /**
     * Paragem na execucao de tarefas.
     */
    public void paragemExecucao() throws EstadoIncorreto{
        if (estado != Estados.EmReparacao) throw new EstadoIncorreto(estado,Estados.EmReparacao);
        estado = Estados.NecessitaReparacao;
    }

    public double getPrecoOrcamento() {
        return orcamento.getPrecoOrcamento();
    }

    public double getTempoOrcamento() {
        return orcamento.getTempoOrcamento();
    }

    public double getPrecoReparacao() {
        return orcamento.getPrecoReparacao();
    }

    public double getTempoReparacao() {
        return orcamento.getTempoReparacao();
    }

    /**
     * Confirma se um orçamento foi aceite ou não
     */
    public void orcamentoAceite(boolean aceite) throws EstadoIncorreto {
        if (estado != Estados.EsperaConfirmacaoOrcamento) throw new EstadoIncorreto(estado,Estados.EsperaConfirmacaoOrcamento);
        if (aceite) estado = Estados.NecessitaReparacao;
        else {
            estado = Estados.ProntoParaLevantar;
            this.prazo = LocalDateTime.now().plusDays(30);
        }
    }

    /**
     * O pedido foi terminado e vamos criar o record com a informação que desejamos guardar. \n
     * Não podemos guardar o \ref idEquipamento, pois este foi levantado da loja.
     * @param equipamento O nome do equipamento.
     * @param idEmpregadoBalcaoEntrega O empregado de balcão que fez a entrega do equipamento.
     * @return A informação que queremos guardar deste pedido.
     * @throws PedidoNaoTerminado Se o pedido não foi concluido.
     */
    public InfoPedidoTerminado terminarPedido(boolean baixa, String equipamento, int idEmpregadoBalcaoEntrega){
        return new InfoPedidoTerminado(LocalDateTime.now(),super.getDescricao(),false,baixa,super.getIdCliente(),equipamento,
                super.getIdEmpregadoBalcao(), idEmpregadoBalcaoEntrega,
                -1,orcamento.getPassosExecutados());
    }

    public String toStringPedido(){
        return "Pedido Normal -> Id: " + super.getId() + "\nId do Cliente: " + super.getIdCliente() + "\nId do Equipamento: " + super.getIdEquipamento()
                + "\nEstado: " + estado.toString() + "\nA acabar estado em: " + prazo + "."
                + "\nDescricao: " + super.getDescricao() + "\n";

    }
}
