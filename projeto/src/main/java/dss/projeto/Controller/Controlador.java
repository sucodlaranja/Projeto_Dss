package dss.projeto.Controller;

import java.util.List;

import dss.projeto.Model.Entidades.EntidadeNaoExiste;
import dss.projeto.Model.Pedidos.Exceptions.EstadoIncorreto;
import dss.projeto.Model.Pedidos.Exceptions.PedidoNaoExiste;
import dss.projeto.Model.Pedidos.Exceptions.PedidoNaoTerminado;
import dss.projeto.Model.Pedidos.Exceptions.PlanoDeTrabalhoVazio;
import dss.projeto.Model.Pedidos.Exceptions.ReparacaoExpressoNaoExiste;
import dss.projeto.Model.Sistema.ISistemaFacade;
import dss.projeto.Model.Sistema.SistemaFacade;
import dss.projeto.Model.Sistema.TecnicosIndisponiveis;
import dss.projeto.View.Menu;
import dss.projeto.View.ReaderWriter;

import javax.mail.MessagingException;

public class Controlador {
    private final ISistemaFacade sisFac;

    public Controlador() {
        this.sisFac = new SistemaFacade();
    }

    /**
     * Faz com que a classe comece a correr.
     */
    public void start() {
        login();
    }

    /**
     * Faz a autenticação do utilizador, pedindo que o utilizador indique o seu nome
     * e password.
     */
    public void login() {
        ReaderWriter.clearScreen();
        ReaderWriter.printString("Bem vindo à app da loja de reparações!!");
        ReaderWriter.printString("(Admin geral -> nome:admin | palavra-passe:admin)\n");
        int i = -1;

        while (i == -1) {

            String nome = ReaderWriter.getString("Indique o seu username:");
            String password = ReaderWriter.getString("Indique a sua palavra-passe:");

            i = sisFac.autenticarFuncionario(nome, password);
            ReaderWriter.clearScreen();
            if (i == -1) {
                System.out.println("Nome e/ou palavra passe errados.");
            }
        }
        try {
            menuPrincipal();
        } catch (EntidadeNaoExiste ignored) {
        }
    }

    /**
     * Cria o menu principal da aplicação e corre-a em seguida.
     * 
     * @throws EntidadeNaoExiste No caso de não existir uma das entidades pedidas.
     */
    public void menuPrincipal() throws EntidadeNaoExiste {
        boolean isEmp = sisFac.isEmpregadoBalcao();
        boolean isTec = sisFac.isTecnico();
        boolean isAdmin = sisFac.isGestor();

        String titulo = "Menu Principal";
        String[] opcoesEmpB = { "Registar Pedido", "Confirmar orçamento", "Levantar equipamento",
                "Baixar equipamento", "Encontrar Pedido" };
        String[] opcoesTec = { "Calcular orçamento", "Reparar Equipamento" };
        String[] opcoesAdmin = { "Adicionar funcionário", "Adicionar preço de reparação expresso",
                "Alterar preço por hora", "Consultar listagens" };

        Menu m = new Menu(titulo, opcoesEmpB);
        m.addOpcoes(opcoesTec);
        m.addOpcoes(opcoesAdmin);

        m.setPreCondition(1, () -> isEmp);
        m.setPreCondition(2, () -> isEmp);
        m.setPreCondition(3, () -> isEmp);
        m.setPreCondition(4, () -> isEmp);
        m.setPreCondition(5, () -> isEmp);
        m.setPreCondition(6, () -> isTec);
        m.setPreCondition(7, () -> isTec);
        m.setPreCondition(8, () -> isAdmin);
        m.setPreCondition(9, () -> isAdmin);
        m.setPreCondition(10, () -> isAdmin);
        m.setPreCondition(11, () -> isAdmin);

        m.setHandler(1, this::menuRegistoPedido);
        m.setHandler(2, this::confirmaOrcamento);
        m.setHandler(3, () -> baixaLevantaEquipamento(false));
        m.setHandler(4, () -> baixaLevantaEquipamento(true));
        m.setHandler(5, this::menuProcuraPedidos);
        m.setHandler(6, this::menuCalcularOrcamento);
        m.setHandler(7, this::menuReparacao);
        m.setHandler(8, this::adicionaFuncionario);
        m.setHandler(9, this::adicionarPrecoRepExpresso);
        m.setHandler(10, this::alterarPrecoPorHora);
        m.setHandler(11, this::menuListagens);

        m.run();
        sisFac.saveInfo();
        ReaderWriter.clearScreen();
        ReaderWriter.printString("Informação guardada, adeus!");
    }

    /**
     * Cria o menu para o registo do pedido de reparação.
     */
    public void menuRegistoPedido() {
        String titulo = "Registo de pedido";
        String[] opcoes = { "Normal", "Expresso" };

        Menu m = new Menu(titulo, opcoes);

        m.setHandler(1, () -> registaPedido("normal"));
        m.setHandler(2, () -> registaPedido("expresso"));

        m.runOnce();
    }

    /**
     * Regista um pedido de reparação.
     * 
     * @param tipo Tipo de reparação.
     */
    public void registaPedido(String tipo) {
        ReaderWriter.clearScreen();
        ReaderWriter.printString("Registo de pedido " + tipo + "\n");
        String nif = ReaderWriter.getString("Indique o NIF do cliente:");

        int idCliente = sisFac.getIdCliente(nif);

        if (idCliente == -1) {
            ReaderWriter
                    .printString("\n\nNão existe o cliente indicado no sistema, terá de criar o registo do cliente.");
            ReaderWriter.pressEnterToContinue();

            ReaderWriter.clearScreen();
            ReaderWriter.printString("NIF do cliente : " + nif);

            String nome = ReaderWriter.getString("Indique o nome do cliente:");
            String email = ReaderWriter.getString("Indique o email do cliente:");

            ReaderWriter.printString("\n\nRegisto do cliente efetuado!");
            ReaderWriter.pressEnterToContinue();
            ReaderWriter.clearScreen();

            idCliente = sisFac.addCliente(nome, email, nif);
        }
        ReaderWriter.printString("NIF do cliente : " + nif);

        String nomeEq = ReaderWriter.getString("Indique o nome do equipamento:");
        int idEquipamento = sisFac.addEquipamento(nomeEq);

        if (tipo.equals("expresso")) {
            List<String> tiposExpresso = sisFac.verTabelaPrecoExpresso();

            ReaderWriter.printString("\nTipos de reparações expresso existentes:");

            for (String exp : tiposExpresso) {
                ReaderWriter.printString(exp);
            }
        }

        String descricao = ReaderWriter.getString("Descrição do estado do equipamento:");

        if (tipo.equals("normal")) {
            sisFac.addPedidoNormal(idCliente, idEquipamento, descricao);
            ReaderWriter.printString("\n\nPedido de reparação registado!!");
        } else
            try {
                sisFac.addPedidoExpresso(idCliente, idEquipamento, descricao);
                ReaderWriter.printString("\n\nPedido de reparação registado!!");
            } catch (ReparacaoExpressoNaoExiste e) {
                ReaderWriter.printString("Reparação expresso não existe");
            } catch (TecnicosIndisponiveis e) {
                ReaderWriter.printString("Não existem técnicos disponíveis");
            }

        ReaderWriter.pressEnterToContinue();
    }

    /**
     * Confirma o orçamento do pedido dado pelo utilizador na View.
     */
    public void confirmaOrcamento() {
        ReaderWriter.clearScreen();
        ReaderWriter.printString("Confirmação de orçamento\n");
        boolean hasID = false;
        int idPedido = -1;

        while (!hasID) {
            try {
                String pedido = ReaderWriter.getString("Indique o id do pedido:");
                idPedido = Integer.parseInt(pedido);
                hasID = true;
            } catch (NumberFormatException e) {
                ReaderWriter.printString("Formato do ID dado está errado!");
            }
        }

        String aceiteS = "";
        boolean aceite = false;
        while (!aceiteS.equals("sim") && !aceiteS.equals("nao")) {
            aceiteS = ReaderWriter.getString("O orçamento foi aceite? (Responder: sim/nao)");

            if (aceiteS.equals("nao")) {
                aceite = false;
            } else if (aceiteS.equals("sim")) {
                aceite = true;
            } else {
                ReaderWriter.printString("\nFormatação errada!");
            }
        }

        try {
            sisFac.confirmarOrcamento(idPedido, aceite);
        } catch (PedidoNaoExiste e) {
            ReaderWriter.printString("\nO pedido indicado não existe!");
        } catch (EstadoIncorreto e) {
            ReaderWriter.printString("\nO pedido indicado não está à espera de resposta sobre o seu orçamento!");
        }
        ReaderWriter.pressEnterToContinue();
    }

    /**
     * Dá a baixa ou levanta um equipamento no sistema.
     * 
     * @param baixa Indica se pretenda dar baixa ou levantar o equipamento.
     */
    public void baixaLevantaEquipamento(boolean baixa) {
        ReaderWriter.clearScreen();
        if (baixa) {
            ReaderWriter.printString("Baixa de equipamento\n");
        } else
            ReaderWriter.printString("Levantar equipamento\n");
        boolean hasID = false;
        int idPedido = -1;

        while (!hasID) {
            try {
                String pedido = ReaderWriter.getString("Indique o id do pedido:");
                idPedido = Integer.parseInt(pedido);
                hasID = true;
            } catch (NumberFormatException e) {
                ReaderWriter.printString("Formato do ID dado está errado!");
            }
        }

        try {
            sisFac.acabaPedido(idPedido, baixa);
        } catch (PedidoNaoExiste e) {
            ReaderWriter.printString("\nO pedido indicado não existe!");
        } catch (EstadoIncorreto e) {
            if (baixa) {
                ReaderWriter.printString("\nO pedido indicado não pode receber baixa!");  
            }
            else ReaderWriter.printString("\nO pedido indicado não está no estado de poder ser levantado!");
        } catch (PedidoNaoTerminado e) {
            ReaderWriter.printString("\nO pedido indicado não está terminado!");
        } catch (EntidadeNaoExiste e) {
            ReaderWriter.printString("\nO equipamento do pedido indicado não existe!");
        }

        ReaderWriter.pressEnterToContinue();
    }

    /**
     * Cria o menu de procura dos pedidos.
     */
    public void menuProcuraPedidos() {
        String titulo = "Procura de pedidos";
        String[] opcoes = { "Por cliente", "Expresso", "Normal", "Fora de Prazo", "Todos" };

        Menu m = new Menu(titulo, opcoes);

        m.setHandler(1, () -> procuraPorCliente());
        m.setHandler(2, () -> procuraPedidos("expresso"));
        m.setHandler(3, () -> procuraPedidos("normal"));
        m.setHandler(4, () -> procuraPedidos("fora"));
        m.setHandler(5, () -> procuraPedidos("todos"));

        m.run();
    }

    /**
     * Faz procura dos pedidos para um cliente dado pelo utilizador na View.
     */
    public void procuraPorCliente() {
        ReaderWriter.clearScreen();
        ReaderWriter.printString("Pedidos feitos por cliente\n");

        String nif = ReaderWriter.getString("Indique o NIF do cliente:");

        int idCliente = sisFac.getIdCliente(nif);
        List<String> pedidos = sisFac.encontraPedidosCliente(idCliente);

        ReaderWriter.clearScreen();
        ReaderWriter.printString("Pedidos de reparação atuais do cliente:\n");

        for (String pedido : pedidos) {
            ReaderWriter.printString(pedido);
        }

        ReaderWriter.pressEnterToContinue();
    }

    /**
     * Faz procura de todos os pedidos para um certo tipo.
     * 
     * @param tipo Tipo de pedidos que pretende-se procurar.
     */
    public void procuraPedidos(String tipo) {
        ReaderWriter.clearScreen();
        List<String> pedidos;
        if (tipo.equals("expresso")) {
            ReaderWriter.printString("Pedidos de reparação expresso atuais:\n");
            pedidos = sisFac.encontraPedidosExpresso();

        } else if (tipo.equals("normal")) {
            ReaderWriter.printString("Pedidos de reparação normal atuais:\n");
            pedidos = sisFac.encontraPedidosNormal();

        } else if (tipo.equals("fora")) {
            ReaderWriter.printString("Pedidos de reparação fora de prazo:\n");
            pedidos = sisFac.encontraPedidosForaDeData();

        } else {
            ReaderWriter.printString("Todos os pedidos de reparação atuais:\n");
            pedidos = sisFac.encontraTodosPedidos();
        }

        for (String pedido : pedidos) {
            ReaderWriter.printString(pedido);
        }

        ReaderWriter.pressEnterToContinue();
    }

    /**
     * Cria o menu de cáculo do orçamento de um pedido.
     */
    public void menuCalcularOrcamento() {
        int idPedido = sisFac.proximoOrcamento();
        if (idPedido == -1) {
            ReaderWriter.clearScreen();
            ReaderWriter.printString("Não existem pedidos de reparação à espera de orçamento.");
            ReaderWriter.pressEnterToContinue();
        } else {
            try {
                sisFac.comecarOrcamento(idPedido);
                String titulo = "Cálculo do orçamento | ID: " + idPedido;
                String[] opcoes = { "Adicionar passo", "Concluir orçamento", "Ver orçamento atual" };

                Menu m = new Menu(titulo, opcoes);

                m.setPreCondition(1, () -> {
                    try {
                        return sisFac.esperaOrcamento(idPedido);
                    } catch (PedidoNaoExiste e) {
                        ReaderWriter.printString("Pedido não existe!");
                        return false;
                    }
                });
                m.setPreCondition(2, () -> {
                    try {
                        return sisFac.esperaOrcamento(idPedido);
                    } catch (PedidoNaoExiste e) {
                        ReaderWriter.printString("Pedido não existe!");
                        return false;
                    }
                });
                m.setPreCondition(3, () -> {
                    try {
                        return sisFac.esperaOrcamento(idPedido);
                    } catch (PedidoNaoExiste e) {
                        ReaderWriter.printString("Pedido não existe!");
                        return false;
                    }
                });

                m.setHandler(1, () -> adicionaPasso(idPedido));
                m.setHandler(2, () -> concluirOrcamento(idPedido));
                m.setHandler(3, () -> verOrcamentoAtual(idPedido));

                m.run();

                if (sisFac.esperaOrcamento(idPedido)) {
                    sisFac.acabaOrcamento(idPedido, false);
                }
            } catch (PedidoNaoExiste e) {
                ReaderWriter.printString("Pedido obtido para calcular o orçamento inexistente!");
                ReaderWriter.pressEnterToContinue();
            } catch (EstadoIncorreto e) {
                ReaderWriter.printString("Pedido obtido não está à espera de receber um orçamento!");
                ReaderWriter.pressEnterToContinue();
            } catch (EntidadeNaoExiste e) {
                ReaderWriter.printString("Pedido obtido não contém um cliente correto!");
                ReaderWriter.pressEnterToContinue();
            } catch (MessagingException e) {
                ReaderWriter.printString("Email do cliente invalido!");
                ReaderWriter.pressEnterToContinue();
            }
        }
    }

    /**
     * Adiciona um passo ao orçamento de um pedido de reparação.
     * 
     * @param idPedido ID do pedido que pretendemos adicionar o passo.
     */
    public void adicionaPasso(int idPedido) {
        ReaderWriter.clearScreen();
        ReaderWriter.printString("Adicionar passo de reparação\n");

        String descricao = ReaderWriter.getString("Descrição do passo:");

        boolean hasPreco = false;
        Double precoPrevisto = -1.0;

        while (!hasPreco) {
            try {
                String precoPrevistoS = ReaderWriter.getString("Custo previsto do passo:");
                precoPrevisto = Double.parseDouble(precoPrevistoS);
                hasPreco = true;
            } catch (NumberFormatException e) {
                ReaderWriter.printString("Formato do preço dado está errado!");
            }
        }

        boolean hasTempo = false;
        Double tempoPrevisto = -1.0;

        while (!hasTempo) {
            try {
                String tempoPrevistoS = ReaderWriter.getString("Tempo previsto do passo:");
                tempoPrevisto = Double.parseDouble(tempoPrevistoS);
                hasTempo = true;
            } catch (NumberFormatException e) {
                ReaderWriter.printString("Formato do tempo dado está errado!");
            }
        }

        try {
            sisFac.addPassoOrcamento(idPedido, descricao, precoPrevisto, tempoPrevisto);
        } catch (PedidoNaoExiste e) {
            ReaderWriter.printString("Pedido não existe!");
        } catch (EstadoIncorreto e) {
            ReaderWriter.printString("Estado do pedido incorreto!");
        }
    }

    /**
     * Conclui o cálculo do orçamento de um pedido de reparação.
     * 
     * @param idPedido ID do pedido de pretendemos concluir o orçamento.
     */
    public void concluirOrcamento(int idPedido) {
        try {
            sisFac.acabaOrcamento(idPedido, true);
            ReaderWriter.clearScreen();
            ReaderWriter.printString("Orçamento concluido!");

        } catch (PedidoNaoExiste | EntidadeNaoExiste e) {
            ReaderWriter.printString("Pedido não existente!");
            ReaderWriter.pressEnterToContinue();
        } catch (EstadoIncorreto e) {
            ReaderWriter.printString("Estado incorreto!");
            ReaderWriter.pressEnterToContinue();
        } catch (MessagingException e) {
            ReaderWriter.printString("Email do cliente invalido!");
            ReaderWriter.pressEnterToContinue();
        }
    }

    /**
     * Mostra o estado do orçamento, que está a ser calculado, de um pedido de
     * reparação.
     * 
     * @param idPedido ID do pedido de reparação.
     */
    public void verOrcamentoAtual(int idPedido) {
        try {
            ReaderWriter.clearScreen();
            String orcamento = sisFac.verOrcamento(idPedido);
            ReaderWriter.printString(orcamento);
            ReaderWriter.pressEnterToContinue();
        } catch (PedidoNaoExiste e) {
            ReaderWriter.printString("Pedido não existente!");
        } catch (EstadoIncorreto e) {
            ReaderWriter.printString("Estado do pedido incorreto!");
        }
    }

    /**
     * Cria o menu para atualizar a reparação de pedido.
     */
    public void menuReparacao() {
        int idPedido = sisFac.proximaReparacao();
        if (idPedido == -1) {
            ReaderWriter.clearScreen();
            ReaderWriter.printString("Não existem pedidos de reparação à espera de reparação.");
            ReaderWriter.pressEnterToContinue();
        } else {
            try {
                sisFac.comecarReparacao(idPedido);
                String titulo = "Reparação do equipamento";
                String[] opcoes = { "Ver passo atual", "Avançar passo", "Ver restantes passos" };

                Menu m = new Menu(titulo, opcoes);

                m.setPreCondition(1, () -> {
                    try {
                        return sisFac.esperaReparacao(idPedido);
                    } catch (PedidoNaoExiste e1) {
                        ReaderWriter.printString("Pedido inexistente!");
                        ReaderWriter.pressEnterToContinue();
                        return false;
                    }
                });
                m.setPreCondition(2, () -> {
                    try {
                        return sisFac.esperaReparacao(idPedido);
                    } catch (PedidoNaoExiste e1) {
                        ReaderWriter.printString("Pedido inexistente!");
                        ReaderWriter.pressEnterToContinue();
                        return false;
                    }
                });
                m.setPreCondition(3, () -> {
                    try {
                        return sisFac.esperaReparacao(idPedido);
                    } catch (PedidoNaoExiste e1) {
                        ReaderWriter.printString("Pedido inexistente!");
                        ReaderWriter.pressEnterToContinue();
                        return false;
                    }
                });

                m.setHandler(1, () -> proximoPasso(idPedido));
                m.setHandler(2, () -> avancarPasso(idPedido));
                m.setHandler(3, () -> verRestantesPassos(idPedido));

                m.run();

                if (sisFac.esperaReparacao(idPedido)) {
                    try {
                        sisFac.paragemExecucao(idPedido);
                    } catch (PlanoDeTrabalhoVazio e) {
                        ReaderWriter.printString("Plano de trabalho vazio!");
                    }
                }
            } catch (PedidoNaoExiste e) {
                ReaderWriter.printString("Pedido inexistente!");
            } catch (EstadoIncorreto e) {
                ReaderWriter.printString("Estado incorreto!");
            }
        }
    }

    /**
     * Mostra o próximo passo do plano de reparação para um dado pedido de
     * reparação.
     * 
     * @param idPedido ID do pedido de reparação.
     */
    public void proximoPasso(int idPedido) {
        String passo;
        try {
            passo = sisFac.getProximoPasso(idPedido);

            ReaderWriter.clearScreen();
            ReaderWriter.printString(passo + "\n");
            ReaderWriter.pressEnterToContinue();
        } catch (PedidoNaoExiste e) {
            ReaderWriter.printString("Pedido inexistente!");
        } catch (EstadoIncorreto e) {
            ReaderWriter.printString("Estado incorreto!");
        } catch (PlanoDeTrabalhoVazio e) {
            ReaderWriter.printString("Plano de trabalho vazio!");
        }
    }

    /**
     * Conclui o passo atual do plano de reparação e avança para o próximo.
     * 
     * @param idPedido ID do pedido de reparação.
     */
    public void avancarPasso(int idPedido) {
        ReaderWriter.clearScreen();
        
        try {
            if (sisFac.isExpresso(idPedido)) {
                ReaderWriter.printString("Pedido expresso concluído!\n");
                sisFac.terminadoProximoPasso(idPedido, 0, 0);
            } else {
                ReaderWriter.printString("Conclusão do passo atual\n");
                boolean hasPreco = false;
                Double precoReal = -1.0;

                while (!hasPreco) {
                    try {
                        String precoRealS = ReaderWriter.getString("Preço de resolução do passo:");
                        precoReal = Double.parseDouble(precoRealS);
                        hasPreco = true;
                    } catch (NumberFormatException e) {
                        ReaderWriter.printString("Formato do preço dado está errado!");
                    }
                }

                boolean hasTempo = false;
                Double tempoReal = -1.0;

                while (!hasTempo) {
                    try {
                        String tempoRealS = ReaderWriter.getString("Tempo de resolução do passo:");
                        tempoReal = Double.parseDouble(tempoRealS);
                        hasTempo = true;
                    } catch (NumberFormatException e) {
                        ReaderWriter.printString("Formato do preço dado está errado!");
                    }
                }

                sisFac.terminadoProximoPasso(idPedido, precoReal, tempoReal);

            }
            ReaderWriter.pressEnterToContinue();
        } catch (PedidoNaoExiste e) {
            ReaderWriter.printString("Pedido inexistente!");
            ReaderWriter.pressEnterToContinue();
        } catch (EstadoIncorreto e) {
            ReaderWriter.printString("Estado incorreto!");
            ReaderWriter.pressEnterToContinue();
        } catch (PlanoDeTrabalhoVazio e) {
            ReaderWriter.printString("Plano de trabalho vazio!");
            ReaderWriter.pressEnterToContinue();
        } catch (MessagingException e) {
            ReaderWriter.printString("Email do cliente invalido!");
            ReaderWriter.pressEnterToContinue();
        } catch (EntidadeNaoExiste e) {
            ReaderWriter.printString("Entidade não!");
            ReaderWriter.pressEnterToContinue();
        }
    }

    /**
     * Mostra os restantes passos do plano de reparação de um dado pedido.
     * 
     * @param idPedido ID do pedido de reparalção.
     */
    public void verRestantesPassos(int idPedido) {
        try {
            List<String> passosRestantes = sisFac.getInfoPassos(idPedido);

            ReaderWriter.clearScreen();
            ReaderWriter.printString("Passos restantes da reparação:\n");

            for (String passo : passosRestantes) {
                ReaderWriter.printString(passo);
            }
            ReaderWriter.pressEnterToContinue();
        } catch (PedidoNaoExiste e) {
            ReaderWriter.printString("Pedido inexistente!");
            ReaderWriter.pressEnterToContinue();
        } catch (EstadoIncorreto e) {
            ReaderWriter.printString("Estado incorreto!");
            ReaderWriter.pressEnterToContinue();
        } catch (PlanoDeTrabalhoVazio e) {
            ReaderWriter.printString("Plano de trabalho vazio!");
            ReaderWriter.pressEnterToContinue();
        }
    }

    /**
     * Adiciona um funcionário ao sistema.
     */
    public void adicionaFuncionario() {
        boolean gestor = false;
        boolean tecnico = false;
        boolean empregadoBalcao = false;

        ReaderWriter.clearScreen();
        ReaderWriter.printString("Adicionar novo funcionário\n");

        String nome = ReaderWriter.getString("Indique o nome do funcionário:");
        String password = ReaderWriter.getString("Indique a palavra-passe do funcionário:");

        String gestorS = "";
        while (!gestorS.equals("sim") && !gestorS.equals("nao")) {
            gestorS = ReaderWriter.getString("É gestor? (Responder: sim/nao)");

            if (gestorS.equals("nao")) {
                gestor = false;
            } else if (gestorS.equals("sim")) {
                gestor = true;
            } else {
                ReaderWriter.printString("\nFormatação errada!");
            }
        }

        String tecnicoS = "";
        while (!tecnicoS.equals("sim") && !tecnicoS.equals("nao")) {
            tecnicoS = ReaderWriter.getString("É técnico de reparação? (Responder: sim/nao)");

            if (tecnicoS.equals("nao")) {
                tecnico = false;
            } else if (tecnicoS.equals("sim")) {
                tecnico = true;
            } else {
                ReaderWriter.printString("\nFormatação errada!");
            }
        }

        String empregadoBalcaoS = "";
        while (!empregadoBalcaoS.equals("sim") && !empregadoBalcaoS.equals("nao")) {
            empregadoBalcaoS = ReaderWriter.getString("É empregado de balcão? (Responder: sim/nao)");

            if (empregadoBalcaoS.equals("nao")) {
                empregadoBalcao = false;
            } else if (empregadoBalcaoS.equals("sim")) {
                empregadoBalcao = true;
            } else {
                ReaderWriter.printString("\nFormatação errada!");
            }
        }

        sisFac.addFuncionario(nome, password, gestor, tecnico, empregadoBalcao);

        ReaderWriter.printString("\n\nFuncionário adicionado com sucesso!");
        ReaderWriter.pressEnterToContinue();
    }

    /**
     * Mostra a tabela de preços das reparações expresso e dá a opção de adicionar
     * ou alterar preços.
     */
    public void adicionarPrecoRepExpresso() {
        ReaderWriter.clearScreen();
        ReaderWriter.printString("Tabela de preços expresso atual\n");
        List<String> precos = sisFac.verTabelaPrecoExpresso();

        for (String preco : precos) {
            ReaderWriter.printString(preco);
        }

        String alterarS = "";
        boolean alterar = false;
        while (!alterarS.equals("sim") && !alterarS.equals("nao")) {
            alterarS = ReaderWriter
                    .getString("Pretende adicionar ou atualizar um valor na tabela? (Responder: sim/nao)");

            if (alterarS.equals("nao")) {
                alterar = false;
            } else if (alterarS.equals("sim")) {
                alterar = true;
            } else {
                ReaderWriter.printString("\nFormatação errada!");
            }
        }

        if (alterar) {
            ReaderWriter.clearScreen();
            String reparacao = ReaderWriter.getString("Indique a descrição da reparação:");

            boolean hasPreco = false;
            Double precoN = -1.0;

            while (!hasPreco) {
                try {
                    String precoNS = ReaderWriter.getString("Indique o preço desta reparação:");
                    precoN = Double.parseDouble(precoNS);

                    hasPreco = true;
                } catch (NumberFormatException e) {
                    ReaderWriter.printString("Formato do ID dado está errado!");
                }
            }
            sisFac.addTabelaPrecoExpresso(reparacao, precoN);

            ReaderWriter.printString("Valor de reparação adicionado/atualizado!");
            ReaderWriter.pressEnterToContinue();
        }
    }

    /**
     * Mostra o preço por hora de trabalho e dá a opção de alterar este valor.
     */
    public void alterarPrecoPorHora() {
        ReaderWriter.clearScreen();

        Double precoAtual = sisFac.getPrecoPorHora();
        ReaderWriter.printString("Preço por hora atual: " + precoAtual);

        String alterarS = "";
        boolean alterar = false;
        while (!alterarS.equals("sim") && !alterarS.equals("nao")) {
            alterarS = ReaderWriter.getString("Pretende alterar este valor? (Responder: sim/nao)");

            if (alterarS.equals("nao")) {
                alterar = false;
            } else if (alterarS.equals("sim")) {
                alterar = true;
            } else {
                ReaderWriter.printString("\nFormatação errada!");
            }
        }

        if (alterar) {
            ReaderWriter.clearScreen();

            boolean hasPreco = false;
            Double precoN = -1.0;

            while (!hasPreco) {
                try {

                    String precoNS = ReaderWriter.getString("Insira o novo valor para o preço por hora:");
                    precoN = Double.parseDouble(precoNS);

                    hasPreco = true;
                } catch (NumberFormatException e) {
                    ReaderWriter.printString("Formato do ID dado está errado!");
                }
            }

            sisFac.setPrecoPorHora(precoN);

            ReaderWriter.printString("Preço alterado!");
            ReaderWriter.pressEnterToContinue();
        }
    }

    /**
     * Cria o menu com as listagens de dados sobre o trabalho realizado pelos
     * funcionários.
     */
    public void menuListagens() {
        String titulo = "Listagens";
        String[] opcoes = { "Médias de duração da reparação dos técnicos",
                "Receções e entregas de equipamentos dos funcionários de balcão",
                "Todas as intervenções realizadas para cada técnico" };

        Menu m = new Menu(titulo, opcoes);

        m.setHandler(1, () -> menuMedias());
        m.setHandler(2, () -> menuRececoesEntregas());
        m.setHandler(3, () -> menuIntervencoesTecnicos());

        m.run();
    }

    /**
     * Cria o menu sobre as médias dos técnicos de reparação.
     */
    public void menuMedias() {
        String titulo = "Médias dos técnicos";
        String[] opcoes = { "Último mês", "Desde sempre" };

        Menu m = new Menu(titulo, opcoes);

        m.setHandler(1, () -> mediasTecnicos(true));
        m.setHandler(2, () -> mediasTecnicos(false));

        m.runOnce();
    }

    /**
     * Mostra as médias do técnicos de reparação para um dado periodo de tempo.
     * 
     * @param ultimoMes Indica se mostra os dados do último mês ou desde o início.
     */
    public void mediasTecnicos(boolean ultimoMes) {
        String tempo = ultimoMes ? "no último mês\n" : "desde sempre\n";

        ReaderWriter.clearScreen();
        ReaderWriter.printString("Médias dos técnicos de reparação " + tempo);

        List<String> listagem = sisFac.listagemPorFuncionario(1, ultimoMes);

        for (String ele : listagem) {
            ReaderWriter.printString(ele);
        }

        ReaderWriter.pressEnterToContinue();
    }

    /**
     * Cria o menu de receções e entregas dos empregados de balcão.
     */
    public void menuRececoesEntregas() {
        String titulo = "Receções e entregas dos empregados de balcão";
        String[] opcoes = { "Último mês", "Desde sempre" };

        Menu m = new Menu(titulo, opcoes);

        m.setHandler(1, () -> rececoesEntregas(true));
        m.setHandler(2, () -> rececoesEntregas(false));

        m.runOnce();
    }

    /**
     * Mostra as receções e entregas dos empregados de balcão para um dado período
     * de tempo.
     * 
     * @param ultimoMes Indica se mostra os dados do último mês ou desde o início.
     */
    public void rececoesEntregas(boolean ultimoMes) {
        String tempo = ultimoMes ? "no último mês\n" : "desde sempre\n";

        ReaderWriter.clearScreen();
        ReaderWriter.printString("Receções e entregas feitas pelos empregados de balcão " + tempo);

        List<String> listagem = sisFac.listagemPorFuncionario(2, ultimoMes);

        for (String ele : listagem) {
            ReaderWriter.printString(ele);
        }

        ReaderWriter.pressEnterToContinue();
    }

    /**
     * Cria o menu de intervenções do técnicos de reparação.
     */
    public void menuIntervencoesTecnicos() {
        String titulo = "Todas as intervenções efetuadas pelos técnicos";
        String[] opcoes = { "Último mês", "Desde sempre" };

        Menu m = new Menu(titulo, opcoes);

        m.setHandler(1, () -> intervencoesEfetuadas(true));
        m.setHandler(2, () -> intervencoesEfetuadas(false));

        m.runOnce();
    }

    /**
     * Mostra todas intervenções dos técnicos de reparação para um dado período de
     * tempo.
     * 
     * @param ultimoMes Indica se mostra os dados do último mês ou desde o início.
     */
    public void intervencoesEfetuadas(boolean ultimoMes) {
        String tempo = ultimoMes ? "no último mês\n" : "desde sempre\n";

        ReaderWriter.clearScreen();
        ReaderWriter.printString("Todas as intervenções efetuadas pelos técnicos " + tempo);

        List<String> listagem = sisFac.listagemPorFuncionario(3, ultimoMes);

        for (String ele : listagem) {
            ReaderWriter.printString(ele);
        }

        ReaderWriter.pressEnterToContinue();
    }
}