package dss.projeto.Model.Sistema;

import dss.projeto.Model.Entidades.EntidadeNaoExiste;
import dss.projeto.Model.Pedidos.Exceptions.*;

import javax.mail.MessagingException;
import java.util.List;

public interface ISistemaFacade {

        double getPrecoPorHora();

        void setPrecoPorHora(double precoPorHora);

        /**
         * Autenticar um funcionario no sistema.
         * 
         * @param nome     Nome do funcionario.
         * @param password Password do funcionario.
         */
        int autenticarFuncionario(String nome, String password);

        // Empregado Balcao

        /**
         * Encontra um cliente se estiver no sistema
         * 
         * @param nif NIF do cliente.
         * @return ID do cliente no sistema.
         */
        int getIdCliente(String nif);

        /**
         * Adiciona o cliente ao conjunto. \n
         * Prepara o id para o próximo cliente.
         * 
         * @param nome  Nome do cliente.
         * @param email Endereço eletrónico do cliente.
         * @param nif   NIF do cliente.
         * @return Id do cliente
         */
        int addCliente(String nome, String email, String nif);

        /**
         * Adiciona o equipamento no sistema. \n
         * Prepara o id para o próximo equipamento.
         * 
         * @param nome Nome do equipamento.
         */
        int addEquipamento(String nome);

        /**
         * Adiciona um pedido de reparação normal ao sistema.
         * 
         * @param idCliente     Identificador do Cliente que fez o pedido.
         * @param idEquipamento Identificador do Equipamento a que este pedido se
         *                      refere.
         * @param descricao     Descricao do pedido.
         */
        void addPedidoNormal(int idCliente, int idEquipamento, String descricao);

        /**
         * Adiciona um pedido de reparação expresso ao sistema.
         * 
         * @param idCliente     Identificador do Cliente que fez o pedido.
         * @param idEquipamento Identificador do Equipamento a que este pedido se
         *                      refere.
         * @param descricao     Descricao do pedido.
         */
        void addPedidoExpresso(int idCliente, int idEquipamento, String descricao)
                        throws ReparacaoExpressoNaoExiste, TecnicosIndisponiveis;

        /**
         * Verifica se um funcionario é gestor.
         * 
         * @return Se o funcionario é ou não gestor
         */
        boolean isGestor() throws EntidadeNaoExiste;

        /**
         * Verifica se um funcionario é tecnico.
         * 
         * @return Se o funcionario é ou não gestor.
         */
        boolean isTecnico() throws EntidadeNaoExiste;

        /**
         * Verifica se um funcionario é empregado de balcao.
         * 
         * @return Se o funcionario é ou não empregado de balcao.
         */
        boolean isEmpregadoBalcao() throws EntidadeNaoExiste;

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
         * Confirma se um orçamento foi aceite ou não
         * 
         * @param idPedido Id do pedido a ser observado.
         * @param aceite   Se o orçamento foi ou não aceite.
         */
        void confirmarOrcamento(int idPedido, boolean aceite) throws PedidoNaoExiste, EstadoIncorreto;

        /**
         * Levantar um equipamento da loja.
         * 
         * @param idPedido Id do pedido a ser observado.
         */
        void acabaPedido(int idPedido, boolean baixa)
                        throws PedidoNaoExiste, EstadoIncorreto, PedidoNaoTerminado, EntidadeNaoExiste;

        // Tecnico

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
        void addPassoOrcamento(int idPedido, String descricao, double precoPrevisto, double tempoPrevisto)
                        throws PedidoNaoExiste, EstadoIncorreto;

        /**
         * Dá como terminado as alterações atuais ao orçamento.
         * 
         * @param idPedido    Id do pedido a ser tratado.
         * @param estaAcabado Se o orçamento deve passar a seguinte fase ou ainda não
         *                    foi terminado.
         */
        void acabaOrcamento(int idPedido, boolean estaAcabado)
                        throws PedidoNaoExiste, EstadoIncorreto, EntidadeNaoExiste, MessagingException;

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
        boolean terminadoProximoPasso(int idPedido, double precoReal, double tempoPrevisto) throws PedidoNaoExiste,
                        EstadoIncorreto, PlanoDeTrabalhoVazio, EntidadeNaoExiste, MessagingException;

        /**
         * Paragem na execucao de tarefas.
         * 
         * @param idPedido Id do pedido a ser tratado.
         */
        void paragemExecucao(int idPedido) throws PedidoNaoExiste, EstadoIncorreto, PlanoDeTrabalhoVazio;

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

        // Gestor

        /**
         * Adiciona o funcionário ao conjunto. \n
         * Prepara o id para o próximo funcionario.
         * 
         * @param nome            Nome de um funcionario.
         * @param password        Senha de um funcionario.
         * @param gestor          Se este funcionario desempenha a funcao de gestor.
         * @param tecnico         Se este funcionario desempenha a funcao de tecnico.
         * @param empregadoBalcao Se este funcionario desempenha a funcao de
         *                        empregadoBalcao.
         */
        void addFuncionario(String nome, String password, boolean gestor, boolean tecnico, boolean empregadoBalcao);

        /**
         * Adiciona o preço de uma reparação expresso.
         * 
         * @param reparacao Em que consiste a reparação.
         * @param precoFixo O preço da reparação.
         */
        void addTabelaPrecoExpresso(String reparacao, double precoFixo);

        /**
         * A lista de precos expresso
         * 
         * @return A lista de precos.
         */
        List<String> verTabelaPrecoExpresso();

        List<String> listagemPorFuncionario(int numListagem, boolean nesteMes);

        void saveInfo();

        boolean isExpresso(int id) throws PedidoNaoExiste;
}
