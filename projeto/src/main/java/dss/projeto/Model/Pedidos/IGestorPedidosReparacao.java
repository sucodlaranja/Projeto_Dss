package dss.projeto.Model.Pedidos;

import dss.projeto.Model.Pedidos.Exceptions.*;
import dss.projeto.Model.Pedidos.Pedido.PedidoReparacao;

import java.util.*;

/// ‘Interface’ que define aquilo que a classe \ref GestorPedidosReparacao permite fazer.
public interface IGestorPedidosReparacao {

    double getPrecoPorHora();

    void setPrecoPorHora(double precoPorHora);

    /**
     * Adiciona um pedido de reparação normal ao conjunto.
     *
     * @param idCliente         Identificador do Cliente que fez o pedido.
     * @param idEquipamento     Identificador do Equipamento a que este pedido se
     *                          refere.
     * @param idEmpregadoBalcao Identificador do Empregado de Balcao que gerou o
     *                          pedido.
     * @param descricao         Descricao do pedido.
     */
    void addPedidoNormal(int idCliente, int idEquipamento, int idEmpregadoBalcao, String descricao);

    int getIdCliente(int idPedido) throws PedidoNaoExiste;

    String getDescricao(int idPedido) throws PedidoNaoExiste;

    double getPrecoOrcamento(int idPedido) throws PedidoNaoExiste;

    double getTempoOrcamento(int idPedido) throws PedidoNaoExiste;

    double getPrecoReparacao(int idPedido) throws PedidoNaoExiste;

    double getTempoReparacao(int idPedido) throws PedidoNaoExiste;

    /**
     * Adiciona um pedido de reparação expresso ao conjunto.
     *
     * @param idCliente         Identificador do Cliente que fez o pedido.
     * @param idEquipamento     Identificador do Equipamento a que este pedido se
     *                          refere.
     * @param idEmpregadoBalcao Identificador do Empregado de Balcao que gerou o
     *                          pedido.
     * @param precoFixo         Preço que está definido na loja.
     * @param descricao         Descricao do pedido.
     */
    void addPedidoExpresso(int idCliente, int idEquipamento, int idEmpregadoBalcao, double precoFixo, String descricao);

    /**
     * Adiciona o preço de uma reparação expresso.
     *
     * @param reparacao Em que consiste a reparação.
     * @param precoFixo O preço da reparação.
     */
    void addTabelaPrecoExpresso(String reparacao, double precoFixo);

    /**
     * Encontra o preço de uma reparação expresso.
     *
     * @param reparacao Em que consiste a reparação.
     * @return O preço da reparação.
     */
    double getPrecoExpresso(String reparacao) throws ReparacaoExpressoNaoExiste;

    /**
     * Encontrar os pedidos de um determinado cliente.
     *
     * @param idCliente Id do cliente.
     * @return Lista com todos os seus pedidos.
     */
    List<String> encontraPedidosCliente(int idCliente);

    /**
     * Encontrar todos os pedidos expresso.
     *
     * @return Lista com todos os pedidos.
     */
    List<String> encontraPedidosExpresso();

    /**
     * Encontrar todos os pedidos normal.
     *
     * @return Lista com todos os pedidos.
     */
    List<String> encontraPedidosNormal();

    /**
     * Encontrar todos os pedidos cujos prazos expiraram.
     *
     * @return Lista com todos os pedidos.
     */
    List<String> encontraPedidosForaDeData();

    /**
     * Encontrar todos os pedidos cujos prazos expiraram.
     *
     * @return Lista com todos os pedidos.
     */
    List<String> encontraTodosPedidos();

    /**
     * Obtém a informação atual do orcamento e muda o seu estado.
     *
     * @param idPedido Id do pedido a ser observado
     */
    void comecarOrcamento(int idPedido) throws PedidoNaoExiste, EstadoIncorreto;

    /**
     * Obtém a informação atual do orcamento e muda o seu estado.
     * 
     * @param idPedido Id do pedido a ser observado
     * @return A informação atual do pedido.
     */
    String verOrcamento(int idPedido) throws PedidoNaoExiste, EstadoIncorreto;

    /**
     * Adiciona um passo ao orcamento.
     *
     * @param idPedido Id do pedido a ser observado.
     */
    void addPassoOrcamento(int idPedido, String descricao, int idTecnicoPlaneamento, double precoPrevisto,
            double tempoPrevisto) throws PedidoNaoExiste, EstadoIncorreto;

    /**
     * Dá como terminado as alterações atuais ao orçamento.
     *
     * @param idPedido    Id do pedido a ser tratado.
     * @param estaAcabado Se o orçamento deve passar a seguinte fase ou ainda não
     *                    foi terminado.
     */
    void acabaOrcamento(int idPedido, boolean estaAcabado) throws PedidoNaoExiste, EstadoIncorreto;

    /**
     * Confirma se um orçamento foi aceite ou não
     *
     * @param idPedido Id do pedido a ser observado.
     * @param aceite   Se o orçamento foi ou não aceite.
     */
    void confirmarOrcamento(int idPedido, boolean aceite) throws PedidoNaoExiste, EstadoIncorreto;

    /**
     * Se o pedido existe muda o estado do pedido.
     *
     * @param idPedido Id do pedido a ser observado
     */
    void comecarReparacao(int idPedido) throws PedidoNaoExiste, EstadoIncorreto;

    /**
     * Encontra o proximo passo de um orcamento.
     *
     * @param idPedido Id do pedido a ser observado.
     * @return O proximo passo.
     */
    String getProximoPasso(int idPedido) throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio;

    /**
     * Encontra o proximo passo de um orcamento.
     * 
     * @param idPedido Id do pedido a ser observado.
     * @return os passos.
     */
    List<String> getInfoPassos(int idPedido) throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio;

    /**
     * Dá como terminado as alterações atuais ao orçamento.
     *
     * @param idPedido Id do pedido a ser tratado.
     * @return Se o plano de trabalho já foi completo.
     */
    boolean terminadoProximoPasso(int idTecnicoExecucao, int idPedido, double precoReal, double tempoPrevisto)
            throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio;

    /**
     * Paragem na execucao de tarefas.
     * 
     * @param idPedido Id do pedido a ser tratado.
     */
    void paragemExecucao(int idPedido) throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio;

    /**
     * Encontra o id do proximo orçamento a ser calculado.
     * 
     * @return O id do proximo orçamento.
     */
    int proximoOrcamento();

    /**
     * Encontra o id do proximo orçamento a ser calculado.
     * 
     * @return O id do proximo orçamento.
     */
    int proximaReparacao();

    /**
     * Pedido esta a espera de orcamento.
     * 
     * @param idPedido Id do pedido a ser tratado.
     */
    boolean esperaOrcamento(int idPedido) throws PedidoNaoExiste;

    /**
     * Pedido esta a espera de reparacao.
     * 
     * @param idPedido Id do pedido a ser tratado.
     */
    boolean esperaReparacao(int idPedido) throws PedidoNaoExiste;

    /**
     * Encontra o identificar do equipamento associado a este pedido.
     *
     * @param idPedido Id do pedido a ser tratado.
     * @return O id do equipamento
     */
    int getIdEquipamento(int idPedido) throws PedidoNaoExiste;

    /**
     * Levantar um equipamento da loja.
     *
     * @param idPedido Id do pedido a ser observado.
     */
    void acabaPedido(int idPedido, String equipamento, int idEmpregadoBalcaoEntrega, boolean baixa)
            throws PedidoNaoExiste, EstadoIncorreto, PedidoNaoTerminado;

    /**
     * A lista de precos expresso
     *
     * @return A lista de precos.
     */
    List<String> verTabelaPrecoExpresso();

    /*
     * uma listagem em que para cada técnico de reparações é indicado o número de
     * reparações programadas/expresso realizadas, a duração média
     * das reparações programadas realizadas e a média dos desvio em relação
     * às durações previstas;
     */
    Map<Integer, String> listagemMedias(boolean nesteMes);

    // uma listagem que indica, para cada funcionário de balcão, quantas recepções e
    // entregas de equipamentos realizou;
    Map<Integer, String> numRececoesEntregas(boolean nesteMes);

    // uma listagem exaustiva, para cada técnico, de todas as intervenções (passos
    // de reparação e reparações expresso) realizas.
    Map<Integer, String> passoPasso(boolean nesteMes);

    void saveInfo();

    int pedidosNecessarioAcao();

    boolean isExpresso(int id) throws PedidoNaoExiste;
}
