package dss.projeto.Model.Sistema;

import dss.projeto.EmailManager.SendEmails;
import dss.projeto.Model.Entidades.EntidadeNaoExiste;
import dss.projeto.Model.Entidades.GestorEntidades;
import dss.projeto.Model.Entidades.IGestorEntidades;
import dss.projeto.Model.Pedidos.*;
import dss.projeto.Model.Pedidos.Exceptions.*;


import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// Classe principal do sistema.
public class SistemaFacade implements ISistemaFacade {

    /// Todas as entidades que fazem parte do sistema.
    private final IGestorEntidades entidades;
    /// Todos os pedidos que fazem parte do sistema.
    private final IGestorPedidosReparacao pedidos;
    /// Funcionario atual do sistema.
    private int funcionarioAtualId;

    public SistemaFacade(){
        entidades = new GestorEntidades();
        pedidos = new GestorPedidosReparacao();
        funcionarioAtualId = -1;
    }

    public double getPrecoPorHora(){
        return pedidos.getPrecoPorHora();
    }

    public void setPrecoPorHora(double precoPorHora){
        pedidos.setPrecoPorHora(precoPorHora);
    }

    /**
     * Autenticar um funcionario no sistema.
     * @param nome Nome do funcionario.
     * @param password Password do funcionario.
     */
    public int autenticarFuncionario(String nome,String password){
        return (funcionarioAtualId = entidades.isFuncionario(nome,password));
    }

    /**
     * Adiciona o equipamento no sistema. \n
     * Prepara o id para o próximo equipamento.
     * @param nome Nome do equipamento.
     */
    public int addEquipamento(String nome){
        return entidades.addEquipamento(nome);
    }

    // Empregado Balcao

    /**
     * Encontra um cliente se estiver no sistema
     * @param nif NIF do cliente.
     * @return ID do cliente no sistema.
     */
    public int getIdCliente(String nif){
        return entidades.getIdCliente(nif);
    }

    /**
     * Adiciona o cliente ao conjunto. \n
     * Prepara o id para o próximo cliente.
     * @param nome Nome do cliente.
     * @param email Endereço eletrónico do cliente.
     * @param nif NIF do cliente.
     * @return Id do cliente
     */
    public int addCliente(String nome, String email, String nif){
        return entidades.addCliente(nome,email,nif);
    }

    /**
     * Adiciona um pedido de reparação normal ao sistema.
     * @param idCliente Identificador do Cliente que fez o pedido.
     * @param idEquipamento Identificador do Equipamento a que este pedido se refere.
     * @param descricao Descricao do pedido.
     */
    public void addPedidoNormal( int idCliente, int idEquipamento,String descricao){
        pedidos.addPedidoNormal(idCliente, idEquipamento, funcionarioAtualId, descricao);
    }

    /**
     * Adiciona um pedido de reparação expresso ao sistema.
     * @param idCliente Identificador do Cliente que fez o pedido.
     * @param idEquipamento Identificador do Equipamento a que este pedido se refere.
     * @param descricao Descricao do pedido.
     */
    public void addPedidoExpresso(int idCliente, int idEquipamento, String descricao) throws ReparacaoExpressoNaoExiste,TecnicosIndisponiveis {
        if (tecnicosDisponiveis()) {
            double preco = pedidos.getPrecoExpresso(descricao);
            pedidos.addPedidoExpresso(idCliente, idEquipamento, funcionarioAtualId, preco, descricao);
        }
        else throw new TecnicosIndisponiveis();

    }

    /**
     * Verifica se um funcionario é gestor.
     * @return Se o funcionario é ou não gestor
     */
    public boolean isGestor() throws EntidadeNaoExiste{
        return entidades.isGestor(funcionarioAtualId);
    }

    /**
     * Verifica se um funcionario é tecnico.
     * @return Se o funcionario é ou não gestor.
     */
     public boolean isTecnico() throws EntidadeNaoExiste{
         return entidades.isTecnico(funcionarioAtualId);
     }

    /**
     * Verifica se um funcionario é empregado de balcao.
     * @return Se o funcionario é ou não empregado de balcao.
     */
     public boolean isEmpregadoBalcao() throws EntidadeNaoExiste{
         return entidades.isEmpregadoBalcao(funcionarioAtualId);
     }

    /**
     * Adiciona o preço de uma reparação expresso.
     * @param reparacao Em que consiste a reparação.
     * @param precoFixo O preço da reparação.
     */
    public void addTabelaPrecoExpresso(String reparacao,double precoFixo){
        pedidos.addTabelaPrecoExpresso(reparacao,precoFixo);
    }

    /**
     * Encontrar os pedidos de um determinado cliente.
     * @param idCliente Id do cliente.
     * @return Lista com todos os seus pedidos.
     */
    public List<String> encontraPedidosCliente(int idCliente){
        return pedidos.encontraPedidosCliente( idCliente);
    }

    /**
     * Encontrar todos os pedidos expresso.
     * @return Lista com todos os pedidos.
     */
    public List<String> encontraPedidosExpresso(){
        return pedidos.encontraPedidosExpresso();
    }

    /**
     * Encontrar todos os pedidos normal.
     * @return Lista com todos os pedidos.
     */
    public List<String> encontraPedidosNormal(){
        return pedidos.encontraPedidosNormal();
    }

    /**
     * Encontrar todos os pedidos cujos prazos expiraram.
     * @return Lista com todos os pedidos.
     */
    public List<String> encontraPedidosForaDeData(){
        return pedidos.encontraPedidosForaDeData();
    }

    /**
     * Encontrar todos os pedidos cujos prazos expiraram.
     * @return Lista com todos os pedidos.
     */
    public List<String> encontraTodosPedidos(){
        return pedidos.encontraTodosPedidos();
    }

    /**
     * Confirma se um orçamento foi aceite ou não
     * @param idPedido Id do pedido a ser observado.
     * @param aceite Se o orçamento foi ou não aceite.
     */
    public void confirmarOrcamento(int idPedido,boolean aceite) throws PedidoNaoExiste, EstadoIncorreto {
        pedidos.confirmarOrcamento(idPedido,aceite);
    }

    /**
     * Levantar um equipamento da loja.
     * @param idPedido Id do pedido a ser observado.
     */
    public void acabaPedido(int idPedido, boolean baixa) throws PedidoNaoExiste, EstadoIncorreto, PedidoNaoTerminado, EntidadeNaoExiste {
        int id = pedidos.getIdEquipamento(idPedido);
        String equipamento = entidades.getEquipamento(id);
        pedidos.acabaPedido(idPedido,equipamento,funcionarioAtualId,baixa);
    }

    // Tecnico

    /**
     * Obtém a informação atual do orcamento e muda o seu estado.
     * @param idPedido Id do pedido a ser observado
     */
    public void comecarOrcamento(int idPedido) throws PedidoNaoExiste, EstadoIncorreto {
        pedidos.comecarOrcamento(idPedido);
    }

    /**
     * Obtém a informação atual do orcamento e muda o seu estado.
     * @param idPedido Id do pedido a ser observado
     * @return A informação atual do pedido.
     */
    public String verOrcamento(int idPedido) throws PedidoNaoExiste, EstadoIncorreto {
        return pedidos.verOrcamento(idPedido);
    }

    /**
     * Adiciona um passo ao orcamento.
     * @param idPedido Id do pedido a ser observado.
     */
    public void addPassoOrcamento(int idPedido,String descricao, double precoPrevisto, double tempoPrevisto) throws PedidoNaoExiste, EstadoIncorreto {
        pedidos.addPassoOrcamento(idPedido,descricao,funcionarioAtualId,precoPrevisto,tempoPrevisto);
    }

    /**
     * Dá como terminado as alterações atuais ao orçamento.
     * @param idPedido Id do pedido a ser tratado.
     * @param estaAcabado Se o orçamento deve passar a seguinte fase ou ainda não foi terminado.
     */
    public void acabaOrcamento(int idPedido,boolean estaAcabado) throws PedidoNaoExiste, EstadoIncorreto, EntidadeNaoExiste, MessagingException {
        pedidos.acabaOrcamento(idPedido,estaAcabado);
        if (estaAcabado) {
            int idCliente = pedidos.getIdCliente(idPedido);
            double preco = pedidos.getPrecoOrcamento(idPedido), tempo = pedidos.getTempoOrcamento(idPedido);
            SendEmails.sendEmailCode(entidades.getEmailCliente(idCliente),
                    true,
                    entidades.getNomeCliente(idCliente),
                    pedidos.getDescricao(idPedido),
                    preco,
                    tempo,
                    calculaPrecoFinal(preco, tempo));
        }
    }

    /**
     * Se o pedido existe muda o estado do pedido.
     * @param idPedido Id do pedido a ser observado
     */
    public void comecarReparacao(int idPedido) throws PedidoNaoExiste, EstadoIncorreto {
        pedidos.comecarReparacao(idPedido);
    }

    /**
     * Encontra o proximo passo de um orcamento.
     * @param idPedido Id do pedido a ser observado.
     * @return O proximo passo.
     */
    public String getProximoPasso(int idPedido) throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio {
        return pedidos.getProximoPasso(idPedido);
    }

    /**
     * Encontra o proximo passo de um orcamento.
     * @param idPedido Id do pedido a ser observado.
     * @return os passos.
     */
    public List<String> getInfoPassos(int idPedido) throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio{
        return pedidos.getInfoPassos(idPedido);
    }

    /**
     * Pedido esta a espera de orcamento.
     * @param idPedido Id do pedido a ser tratado.
     */
    public boolean esperaOrcamento(int idPedido) throws PedidoNaoExiste{
        return pedidos.esperaOrcamento( idPedido);
    }

    /**
     * Pedido esta a espera de reparacao.
     * @param idPedido Id do pedido a ser tratado.
     */
    public boolean esperaReparacao(int idPedido) throws PedidoNaoExiste{
        return pedidos.esperaReparacao(idPedido);
    }

    /**
     * Dá como terminado as alterações atuais ao orçamento.
     * @param idPedido Id do pedido a ser tratado.
     * @return Se o plano de trabalho já foi completo.
     */
    public boolean terminadoProximoPasso(int idPedido,double precoReal,double tempoPrevisto) throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio, EntidadeNaoExiste, MessagingException {
        boolean res = pedidos.terminadoProximoPasso(funcionarioAtualId,idPedido,precoReal,tempoPrevisto);
        if (res) {
            int idCliente = pedidos.getIdCliente(idPedido);
            double preco = pedidos.getPrecoReparacao(idPedido), tempo = pedidos.getTempoReparacao(idPedido);
            SendEmails.sendEmailCode(entidades.getEmailCliente(idCliente),
                    false,
                    entidades.getNomeCliente(idCliente),
                    pedidos.getDescricao(idPedido),
                    preco,
                    tempo,
                    calculaPrecoFinal(preco, tempo));
        }
        return res;
    }

    /**
     * Paragem na execucao de tarefas.
     * @param idPedido Id do pedido a ser tratado.
     */
    public void paragemExecucao(int idPedido) throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio{
        pedidos.paragemExecucao(idPedido);
    }

    /**
     * Encontra o id do proximo orçamento a ser calculado.
     * @return O id do proximo orçamento.
     */
    public int proximoOrcamento(){
        return pedidos.proximoOrcamento();
    }

    /**
     * Encontra o id do proximo orçamento a ser calculado.
     * @return O id do proximo orçamento.
     */
    public int proximaReparacao(){
        return pedidos.proximaReparacao();
    }

    // Gestor

    /**
     * Adiciona o funcionário ao conjunto. \n
     * Prepara o id para o próximo funcionario.
     * @param nome Nome de um funcionario.
     * @param password Senha de um funcionario.
     * @param gestor Se este funcionario desempenha a funcao de gestor.
     * @param tecnico Se este funcionario desempenha a funcao de tecnico.
     * @param empregadoBalcao Se este funcionario desempenha a funcao de empregadoBalcao.
     */
    public void addFuncionario(String nome, String password, boolean gestor, boolean tecnico, boolean empregadoBalcao){
        entidades.addFuncionario(nome, password, gestor, tecnico, empregadoBalcao);
    }

    public void saveInfo(){
        entidades.saveInfo();
        pedidos.saveInfo();
    }

    /**
     * A lista de precos expresso
     * @return A lista de precos.
     */
    public List<String> verTabelaPrecoExpresso(){
        return pedidos.verTabelaPrecoExpresso();
    }

    private double calculaPrecoFinal(double preco,double tempo){
        return preco + tempo * getPrecoPorHora();
    }

    ///Encontra as listagens corretas e faz o seu calculo.
    public List<String> listagemPorFuncionario(int numListagem,boolean nesteMes){
        Map<Integer,String> map;
        if (numListagem == 1) map = pedidos.listagemMedias(nesteMes);
        else if (numListagem == 2) map = pedidos.numRececoesEntregas(nesteMes);
        else if (numListagem == 3) map = pedidos.passoPasso(nesteMes);
        else return null;

        List<String> r = new ArrayList<>();

        for(Map.Entry<Integer,String> e : map.entrySet()){
            String s = "";
            try {
                s += entidades.getNomeFuncionario(e.getKey()) + " \n";
            } catch (EntidadeNaoExiste ignored) { }
            s += e.getValue();
            r.add(s);
        }

        return r;
    }

    private boolean tecnicosDisponiveis(){
        return entidades.getNumberOfTecnicos() - pedidos.pedidosNecessarioAcao() > 0;
    }

    public boolean isExpresso(int id) throws PedidoNaoExiste{
        return pedidos.isExpresso(id);
    }
}
