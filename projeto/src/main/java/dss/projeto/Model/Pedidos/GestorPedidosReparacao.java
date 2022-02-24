package dss.projeto.Model.Pedidos;

import dss.projeto.Data.dataDAO;
import dss.projeto.Model.Pedidos.Exceptions.*;
import dss.projeto.Model.Pedidos.Pedido.*;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/// Classe que vai gerir todos os pedidos.
public class GestorPedidosReparacao implements IGestorPedidosReparacao {

    /// Conjunto de pedidos que estão em progresso.
    private final Map<Integer, PedidoReparacao> pedidosPorProcessar;
    /// O identificador do próximo pedido adicionado ao conjunto.
    private int nextIdPedido;
    /// A tabela de precos dos pedidos expresso.
    private final Map<String, Double> tabelaPrecoExpresso;
    /// Conjunto de pedidos já terminaram.
    private final Set<InfoPedidoTerminado> pedidosArquivados;
    /// Preço por hora
    private double precoPorHora;

    public GestorPedidosReparacao(){
        pedidosPorProcessar = dataDAO.getInstanceHashMap("pedidosPorProcessar");
        pedidosArquivados = dataDAO.getInstanceHashSet("pedidosArquivados");
        tabelaPrecoExpresso = dataDAO.getInstanceHashMap("tabelaPrecoExpresso");
        if (tabelaPrecoExpresso.containsKey("PPH")) precoPorHora = tabelaPrecoExpresso.remove("PPH");
        else precoPorHora = 10;
        nextIdPedido = dataDAO.maxId(pedidosPorProcessar.keySet());
    }




    public double getPrecoPorHora() {
        return precoPorHora;
    }

    public void setPrecoPorHora(double precoPorHora) {
        this.precoPorHora = precoPorHora;
    }

    /**
     * Adiciona um pedido de reparação normal ao conjunto.
     * @param idCliente Identificador do Cliente que fez o pedido.
     * @param idEquipamento Identificador do Equipamento a que este pedido se refere.
     * @param idEmpregadoBalcao Identificador do Empregado de Balcao que gerou o pedido.
     * @param descricao Descricao do pedido.
     */
    public void addPedidoNormal( int idCliente, int idEquipamento, int idEmpregadoBalcao,String descricao){
        pedidosPorProcessar.put(nextIdPedido,
                new PedidoNormal(nextIdPedido,idCliente,idEquipamento,idEmpregadoBalcao,descricao));
        nextIdPedido ++;
    }

    /**
     * Adiciona um pedido de reparação expresso ao conjunto.
     * @param idCliente Identificador do Cliente que fez o pedido.
     * @param idEquipamento Identificador do Equipamento a que este pedido se refere.
     * @param idEmpregadoBalcao Identificador do Empregado de Balcao que gerou o pedido.
     * @param precoFixo Preço que está definido na loja.
     * @param descricao Descricao do pedido.
     */
    public void addPedidoExpresso(int idCliente, int idEquipamento, int idEmpregadoBalcao, double precoFixo,String descricao){
        pedidosPorProcessar.put(nextIdPedido,
                new PedidoExpresso(nextIdPedido,idCliente,idEquipamento,idEmpregadoBalcao,precoFixo,descricao));
        nextIdPedido ++;
    }

    /**
     * Adiciona o preço de uma reparação expresso.
     * @param reparacao Em que consiste a reparação.
     * @param precoFixo O preço da reparação.
     */
    public void addTabelaPrecoExpresso(String reparacao,double precoFixo){
        tabelaPrecoExpresso.put(reparacao,precoFixo);
    }

    /**
     * Encontrar os pedidos de um determinado cliente.
     * @param idCliente Id do cliente.
     * @return Lista com todos os pedidos
     */
    public List<String> encontraPedidosCliente(int idCliente){
        verificaBaixas();
        return pedidosPorProcessar.values().stream().filter(pedido -> pedido.getIdCliente() == idCliente)
                .map(PedidoReparacao :: toStringPedido).collect(Collectors.toList());
    }

    /**
     * Encontrar todos os pedidos expresso.
     * @return Lista com todos os pedidos.
     */
    public List<String> encontraPedidosExpresso(){
        verificaBaixas();
        return pedidosPorProcessar.values().stream().filter(pedido -> pedido instanceof PedidoExpresso)
                .map(PedidoReparacao :: toStringPedido).collect(Collectors.toList());
    }

    /**
     * Encontrar todos os pedidos normal.
     * @return Lista com todos os pedidos.
     */
    public List<String> encontraPedidosNormal(){
        verificaBaixas();
        return pedidosPorProcessar.values().stream().filter(pedido -> pedido instanceof PedidoNormal)
                .map(PedidoReparacao :: toStringPedido).collect(Collectors.toList());
    }

    /**
     * Pedido esta a espera de orcamento.
     * @param idPedido Id do pedido a ser tratado.
     */
    public boolean esperaOrcamento(int idPedido) throws PedidoNaoExiste{
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (!(p instanceof PedidoNormal pedido)) throw new PedidoNaoExiste();
        return pedido.esperaOrcamento();
    }

    /**
     * Pedido esta a espera de reparacao.
     * @param idPedido Id do pedido a ser tratado.
     */
    public boolean esperaReparacao(int idPedido) throws PedidoNaoExiste{
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (p == null) throw new PedidoNaoExiste();
        return p.esperaReparacao();
    }

    /**
     * Encontrar todos os pedidos cujos prazos expiraram.
     * @return Lista com todos os pedidos.
     */
    public List<String> encontraPedidosForaDeData(){
        verificaBaixas();
        return pedidosPorProcessar.values().stream().filter(pedido -> pedido.getEstado() == Estados.NecessitaBaixa)
                .map(PedidoReparacao :: toStringPedido).collect(Collectors.toList());
    }

    /**
     * Encontrar todos os pedidos cujos prazos expiraram.
     * @return Lista com todos os pedidos.
     */
    public List<String> encontraTodosPedidos(){
        verificaBaixas();
        return pedidosPorProcessar.values().stream()
                .map(PedidoReparacao :: toStringPedido).collect(Collectors.toList());
    }

    /**
     * Encontra o preço de uma reparação expresso.
     * @param reparacao Em que consiste a reparação.
     * @return O preço da reparação.
     */
    public double getPrecoExpresso(String reparacao) throws ReparacaoExpressoNaoExiste {
        if (!tabelaPrecoExpresso.containsKey(reparacao)) throw new ReparacaoExpressoNaoExiste();
        return tabelaPrecoExpresso.get(reparacao);
    }

    /**
     * Obtém a informação atual do orcamento e muda o seu estado.
     * @param idPedido Id do pedido a ser observado
     */
    public void comecarOrcamento(int idPedido) throws PedidoNaoExiste, EstadoIncorreto {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (!(p instanceof PedidoNormal pedido)) throw new PedidoNaoExiste();
        pedido.comecarOrcamento();
    }

    /**
     * Obtém a informação atual do orcamento e muda o seu estado.
     * @param idPedido Id do pedido a ser observado
     * @return A informação atual do pedido.
     */
    public String verOrcamento(int idPedido) throws PedidoNaoExiste, EstadoIncorreto {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (!(p instanceof PedidoNormal pedido)) throw new PedidoNaoExiste();
        return pedido.verOrcamento();
    }

    /**
     * Adiciona um passo ao orcamento.
     * @param idPedido Id do pedido a ser observado.
     */
    public void addPassoOrcamento(int idPedido,String descricao, int idTecnicoPlaneamento, double precoPrevisto, double tempoPrevisto)throws PedidoNaoExiste, EstadoIncorreto{
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (!(p instanceof PedidoNormal pedido)) throw new PedidoNaoExiste();
        pedido.addPassoOrcamento(descricao,idTecnicoPlaneamento,precoPrevisto,tempoPrevisto);
    }

    public int getIdCliente(int idPedido) throws PedidoNaoExiste {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (p == null) throw new PedidoNaoExiste();
        return p.getIdCliente();
    }


    public String getDescricao(int idPedido) throws PedidoNaoExiste {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (p == null) throw new PedidoNaoExiste();
        return p.getDescricao();
    }

    public double getPrecoOrcamento(int idPedido) throws PedidoNaoExiste {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (!(p instanceof PedidoNormal pedido)) throw new PedidoNaoExiste();
        return pedido.getPrecoOrcamento();
    }

    public double getTempoOrcamento(int idPedido) throws PedidoNaoExiste {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (!(p instanceof PedidoNormal pedido)) throw new PedidoNaoExiste();
        return pedido.getTempoOrcamento();
    }

    public double getPrecoReparacao(int idPedido) throws PedidoNaoExiste {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (p == null) throw new PedidoNaoExiste();
        return p.getPrecoReparacao();
    }

    public double getTempoReparacao(int idPedido) throws PedidoNaoExiste {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (p == null) throw new PedidoNaoExiste();
        return p.getTempoReparacao();
    }



    /**
     * Dá como terminado as alterações atuais ao orçamento.
     * @param idPedido Id do pedido a ser tratado.
     * @param estaAcabado Se o orçamento deve passar a seguinte fase ou ainda não foi terminado.
     */
    public void acabaOrcamento(int idPedido,boolean estaAcabado) throws PedidoNaoExiste, EstadoIncorreto {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (!(p instanceof PedidoNormal pedido)) throw new PedidoNaoExiste();
        pedido.acabaOrcamento(estaAcabado);
    }

    /**
     * Confirma se um orçamento foi aceite ou não
     * @param idPedido Id do pedido a ser observado.
     * @param aceite Se o orçamento foi ou não aceite.
     */
    public void confirmarOrcamento(int idPedido,boolean aceite) throws PedidoNaoExiste, EstadoIncorreto {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (!(p instanceof PedidoNormal pedido)) throw new PedidoNaoExiste();
        pedido.orcamentoAceite(aceite);
    }

    /**
     * Se o pedido existe muda o estado do pedido.
     * @param idPedido Id do pedido a ser observado
     */
    public void comecarReparacao(int idPedido) throws PedidoNaoExiste, EstadoIncorreto {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (p == null) throw new PedidoNaoExiste();
        p.comecarReparacao();
    }

    /**
     * Encontra o proximo passo de um orcamento.
     * @param idPedido Id do pedido a ser observado.
     * @return O proximo passo.
     */
    public String getProximoPasso(int idPedido) throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (p == null) throw new PedidoNaoExiste();
        return p.getProximoPasso();
    }

    /**
     * Encontra o proximo passo de um orcamento.
     * @param idPedido Id do pedido a ser observado.
     * @return os passos.
     */
    public List<String> getInfoPassos(int idPedido) throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (p == null) throw new PedidoNaoExiste();
        return p.getInfoPassos();
    }

    /**
     * Dá como terminado as alterações atuais ao orçamento.
     * @param idPedido Id do pedido a ser tratado.
     * @return Se o plano de trabalho já foi completo.
     */
    public boolean terminadoProximoPasso(int idTecnicoExecucao,int idPedido,double precoReal,double tempoPrevisto) throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (p == null) throw new PedidoNaoExiste();
        return p.terminadoProximoPasso(idTecnicoExecucao,precoReal,tempoPrevisto);
    }



    /**
     * Paragem na execucao de tarefas.
     * @param idPedido Id do pedido a ser tratado.
     */
    public void paragemExecucao(int idPedido) throws PedidoNaoExiste, EstadoIncorreto{
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (!(p instanceof PedidoNormal pedido)) throw new PedidoNaoExiste();
        pedido.paragemExecucao();
    }

    /**
     * Encontra o id do proximo orçamento a ser calculado.
     * @return O id do proximo orçamento.
     */
    public int proximoOrcamento(){
        verificaBaixas();
        return pedidosPorProcessar.values().stream().filter(p -> p.getEstado() == Estados.OrcamentoPorCalcular)
                .min(Comparator.comparing(PedidoReparacao::getDataCriacao)).map(PedidoReparacao::getId).orElse(-1);
    }

    /**
     * Encontra o id do proximo orçamento a ser calculado.
     * @return O id do proximo orçamento.
     */
    public int proximaReparacao(){
        verificaBaixas();
        return pedidosPorProcessar.values().stream().filter(p -> p.getEstado() == Estados.NecessitaReparacao)
                .min(Comparator.comparing(PedidoReparacao::getDataCriacao)).map(PedidoReparacao::getId).orElse(-1);
    }

    /**
     * Encontra o identificar do equipamento associado a este pedido.
     * @param idPedido Id do pedido a ser tratado.
     * @return O id do equipamento.
     */
    public int getIdEquipamento(int idPedido) throws PedidoNaoExiste {
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (p == null) throw new PedidoNaoExiste();
        return p.getIdEquipamento();
    }

    /**
     * Levantar um equipamento da loja.
     * @param idPedido Id do pedido a ser observado.
     */
    public void acabaPedido(int idPedido, String equipamento, int idEmpregadoBalcaoEntrega, boolean baixa) throws PedidoNaoExiste, EstadoIncorreto, PedidoNaoTerminado {
        verificaBaixas();
        PedidoReparacao p = pedidosPorProcessar.get(idPedido);
        if (p == null) throw new PedidoNaoExiste();
        if(baixa){
            if (p.getEstado() != Estados.NecessitaBaixa)
                throw new EstadoIncorreto(p.getEstado(),Estados.NecessitaBaixa);
        } else if(p.getEstado() != Estados.ProntoParaLevantar) throw new EstadoIncorreto(p.getEstado(),Estados.ProntoParaLevantar);
        terminarPedido(idPedido, baixa,equipamento,idEmpregadoBalcaoEntrega);
    }

    /**
     *  Termina um pedido de reparação, removendo-o do conjunto de pedidos por processar
     *  e colocando a sua informação nos pedidosArquivados.
     * @param id O identificador do pedido.
     * @param equipamento O nome do equipamento.
     * @param idEmpregadoBalcaoEntrega O empregado de balcão que fez a entrega do equipamento.
     * @throws PedidoNaoTerminado Se o pedido não foi concluido.
     * @throws PedidoNaoExiste Se o pedido não existe nos nossos dados.
     */
    private void terminarPedido(int id,boolean baixa,String equipamento,int idEmpregadoBalcaoEntrega) throws PedidoNaoTerminado, PedidoNaoExiste {
        PedidoReparacao p = pedidosPorProcessar.remove(id);
        if (p == null) throw new PedidoNaoExiste();
        pedidosArquivados.add(p.terminarPedido(baixa,equipamento,idEmpregadoBalcaoEntrega));
    }

    public boolean isExpresso(int id) throws PedidoNaoExiste {
        PedidoReparacao p = pedidosPorProcessar.get(id);
        if (p == null) throw new PedidoNaoExiste();
        
        return p instanceof PedidoExpresso;
    }

    /**
     * A lista de precos expresso
     * @return A lista de precos.
     */
    public List<String> verTabelaPrecoExpresso(){
        return tabelaPrecoExpresso.entrySet().stream()
                .map(entry -> entry.getKey() + " -> " + entry.getValue() + "€").collect(Collectors.toList());
    }

    private void verificaBaixas(){
        LocalDateTime l = LocalDateTime.now();
        pedidosPorProcessar.values().forEach(p -> p.verificaPrazo(l));
    }

    public boolean isTrabalhoNecessario(Estados estados){
        return (estados == Estados.NecessitaReparacao
                || estados == Estados.EmReparacao
                || estados == Estados.OrcamentoPorCalcular
                || estados == Estados.ACalcularOrcamento);
    }

    public int pedidosNecessarioAcao(){
        return (int) pedidosPorProcessar.values().stream()
                .map(PedidoReparacao::getEstado).filter(this::isTrabalhoNecessario).count();
    }

    /*
    uma listagem em que para cada técnico de reparações é indicado o número de reparações programadas/expresso realizadas, a duração média
    das reparações programadas realizadas e a média dos desvio em relação
    às durações previstas;
     */
    public Map<Integer,String> listagemMedias(boolean nesteMes){
        Map<Integer,List<List<Double>>> map = new HashMap<>();
        LocalDateTime ultimoMes = LocalDateTime.now().minusDays(30);

        for(InfoPedidoTerminado info : pedidosArquivados){
            if (nesteMes) {if(ultimoMes.isBefore(info.time())) adSpecialMap(map,info);}
            else adSpecialMap(map,info);
        }

        Map<Integer,String> r = new HashMap<>();
        for(Map.Entry<Integer,List<List<Double>>> pair : map.entrySet()){
            r.put(pair.getKey(),toStringSpecialList(pair.getValue()));
        }

        return r;
    }

    private static void adSpecialMap(Map<Integer,List<List<Double>>> resultado,InfoPedidoTerminado info){

        Set<Integer> idsFuncionarios = info.getIdsFuncionarios();
        for (int id : idsFuncionarios){
            List<Double> c1,c2,c3;
            if (resultado.containsKey(id)) {
                c1 = resultado.get(id).get(0);
                c2 = resultado.get(id).get(1);
                c3 = resultado.get(id).get(2);
            }
            else{
                c1 = new ArrayList<>();c2 = new ArrayList<>();c3 = new ArrayList<>();
                List<List<Double>> temp = new ArrayList<>();
                temp.add(0,c1);temp.add(1,c2);temp.add(2,c3);
                resultado.put(id,temp);
            }

            c1.add(1.);
            if (info.passosExecutados() != null){
                for(Passo p : info.passosExecutados()){
                    if (id == p.getIdTecnicoExecucao()){
                        c2.add(p.getTempoReal());
                        c3.add(p.getTempoReal() - p.getTempoPrevisto());
                    }
                }
            }
        }
    }

    private static String toStringSpecialList(List<List<Double>> lista){
        StringBuilder sb = new StringBuilder();
        double nReparacoes = lista.get(0).stream().mapToDouble(id -> id).sum();
        double tempoMedio = lista.get(1).stream().mapToDouble(id -> id).average().orElse(0);
        double desvioMedio = lista.get(2).stream().mapToDouble(id -> id).average().orElse(0);

        sb.append("\n   Numero de reparações em que participou: ").append(nReparacoes).append(".\n");
        if (tempoMedio == 0) sb.append("   Técnico só realizou reparações do tipo expresso.\n");
        else {
            sb.append("   Duração média dos passos das reparações realizadas: ")
                    .append(tempoMedio).append(" horas.\n");
            sb.append("   Desvio médio dos passos em relação aos tempos previstos: ")
                    .append(desvioMedio).append(" horas.\n");
        }
        return sb.toString();
    }

    //uma listagem que indica, para cada funcionário de balcão, quantas recepções e entregas de equipamentos realizou;
    public Map<Integer,String> numRececoesEntregas(boolean nesteMes){
        Map<Integer,List<Integer>> map = new HashMap<>();
        LocalDateTime ultimoMes = LocalDateTime.now().minusDays(30);

        for(InfoPedidoTerminado info : pedidosArquivados){
            if (nesteMes) {if(ultimoMes.isBefore(info.time())) adSpecialMap2(map,info);}
            else adSpecialMap2(map,info);
        }

        Map<Integer,String> r = new HashMap<>();
        for(Map.Entry<Integer,List<Integer>> pair : map.entrySet()){
            r.put(pair.getKey(),toStringSpecialList2(pair.getValue()));
        }

        return r;
    }

    private static void adSpecialMap2(Map<Integer,List<Integer>> resultado,InfoPedidoTerminado info){
        if (resultado.containsKey(info.idEmpregadoBalcaoRegisto())){
            List<Integer> l = resultado.get(info.idEmpregadoBalcaoRegisto());
            l.add(0,l.remove(0) + 1);
        }
        else{
            List<Integer> l = new ArrayList<>();
            l.add(0,1);l.add(1,0);
            resultado.put(info.idEmpregadoBalcaoRegisto(),l);
        }

        if (resultado.containsKey(info.idEmpregadoBalcaoEntrega())){
            List<Integer> l = resultado.get(info.idEmpregadoBalcaoEntrega());
            l.add(1,l.remove(1) + 1);
        }
        else{
            List<Integer> l = new ArrayList<>();
            l.add(0,0);l.add(1,1);
            resultado.put(info.idEmpregadoBalcaoRegisto(),l);
        }
    }

    private static String toStringSpecialList2(List<Integer> lista){
        return " completou " + lista.get(0) +
                " registos e " + lista.get(1) + " entregas.";
    }

    //uma listagem exaustiva, para cada técnico, de todas as intervenções (passos de reparação e reparações expresso) realizas.
    public Map<Integer,String> passoPasso(boolean nesteMes){
        Map<Integer,List<String>> map = new HashMap<>();
        LocalDateTime ultimoMes = LocalDateTime.now().minusDays(30);

        for(InfoPedidoTerminado info : pedidosArquivados){
            if (nesteMes) {if(ultimoMes.isBefore(info.time())) adSpecialMap3(map,info);}
            else adSpecialMap3(map,info);
        }

        Map<Integer,String> r = new HashMap<>();
        for(Map.Entry<Integer,List<String>> pair : map.entrySet()){
            r.put(pair.getKey(),String.join("\n",pair.getValue()));
        }

        return r;
    }

    private static void adSpecialMap3(Map<Integer,List<String>> resultado,InfoPedidoTerminado info){
        Set<Integer> idsFuncionarios = info.getIdsFuncionarios();
        for (int id : idsFuncionarios){
            if(resultado.containsKey(id)){
                List<String> l = resultado.get(id);
                l.add(info.passosToString(id));
            }
            else{
                List<String> l = new ArrayList<>();
                l.add(info.passosToString(id));
                resultado.put(id,l);
            }
        }

    }

    public void saveInfo(){
        dataDAO.saveInstanceHashMap(pedidosPorProcessar,"pedidosPorProcessar");
        dataDAO.saveInstanceHashSet(pedidosArquivados,"pedidosArquivados");
        tabelaPrecoExpresso.put("PPH",precoPorHora);
        dataDAO.saveInstanceHashMap(tabelaPrecoExpresso,"tabelaPrecoExpresso");
    }
}
