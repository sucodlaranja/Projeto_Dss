package dss.projeto.Model.Entidades;

import dss.projeto.Data.dataDAO;

import java.util.Map;

/// Classe que vai gerir todas as entidades.
public class GestorEntidades implements IGestorEntidades {

    /// Um conjunto de funcionarios.
    private final Map<Integer,Funcionario> funcionarios;
    /// O identificador do próximo funcionario adicionado ao conjunto.
    private int nextIdFuncionario;
    /// Um conjunto de clientes.
    private final Map<Integer,Cliente> clientes;
    /// O identificador do próximo cliente adicionado ao conjunto.
    private int nextIdCliente;
    /// Um conjunto de equipamentos.
    private final Map<Integer,Equipamento> equipamentos;
    /// O identificador do próximo equipamento adicionado ao conjunto.
    private int nextIdEquipamento;

    /// Construtor que usa o pacote DATA para ler as informações previamente carregadas em ficheiros.
    public GestorEntidades(){
        funcionarios = dataDAO.getInstanceHashMap("funcionarios");

        if (funcionarios.isEmpty())
            addFuncionario("admin", "admin", true, true, true);
        clientes = dataDAO.getInstanceHashMap("clientes");
        equipamentos = dataDAO.getInstanceHashMap("equipamentos");

        nextIdFuncionario = dataDAO.maxId(funcionarios.keySet());
        nextIdCliente = dataDAO.maxId(clientes.keySet());
        nextIdEquipamento = dataDAO.maxId(equipamentos.keySet());
    }

    /**
     * Adiciona um funcionário ao conjunto. \n
     * Prepara o id para o próximo funcionario.
     * @param nome Nome de um funcionario.
     * @param password Senha de um funcionario.
     * @param gestor Se este funcionario desempenha a funcao de gestor.
     * @param tecnico Se este funcionario desempenha a funcao de tecnico.
     * @param empregadoBalcao Se este funcionario desempenha a funcao de empregadoBalcao.
     */
    public void addFuncionario(String nome, String password, boolean gestor, boolean tecnico, boolean empregadoBalcao){
        funcionarios.put(nextIdFuncionario,
                new Funcionario(nextIdFuncionario,nome,password,gestor,tecnico,empregadoBalcao));
        nextIdFuncionario++;
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
        clientes.put(nextIdCliente,
                new Cliente(nextIdCliente,nome,email,nif));
        return nextIdCliente++;
    }

    /**
     * Adiciona o equipamento no sistema. \n
     * Prepara o id para o próximo equipamento.
     * @param nome Nome do equipamento.
     */
    public int addEquipamento(String nome) {
        equipamentos.put(nextIdEquipamento, new Equipamento(nextIdEquipamento,nome));
        return nextIdEquipamento++;
    }

    /**
     * Verificar que existe tal funcionario.
     * @param nome Nome do funcionario.
     * @param password Password do funcionario.
     */
    public int isFuncionario(String nome,String password){
        return funcionarios.values().stream()
                .filter(funcionario -> nome.equals(funcionario.getNome()) && password.equals(funcionario.getPassword()))
                .findFirst().map(Funcionario::getId).orElse(-1);
    }

    /**
     * Encontra um cliente se estiver no conjunto
     * @param nif NIF do cliente.
     * @return ID do cliente no conjunto.
     */
    public int getIdCliente(String nif){
        return clientes.values().stream()
                .filter(cliente -> nif.equals( cliente.getNif()))
                .findFirst().map(Cliente::getId).orElse(-1);
    }



    /**
     * Encontra o endereço eletrónico de um cliente se estiver no conjunto
     * @return Email do cliente no conjunto.
     */
    public String getEmailCliente(int idCliente) throws EntidadeNaoExiste {
        Cliente c = clientes.get(idCliente);
        if (c == null) throw new EntidadeNaoExiste();
        return c.getEmail();
    }

    /**
     * Encontra o endereço eletrónico de um cliente se estiver no conjunto
     * @return Nome do cliente no conjunto.
     */
    public String getNomeCliente(int idCliente) throws EntidadeNaoExiste {
        Cliente c = clientes.get(idCliente);
        if (c == null) throw new EntidadeNaoExiste();
        return c.getNome();
    }

    /**
     * Encontra o equipamento associado a este id.
     * @param id O id do equipamento
     * @return O equipamento
     */
    public String getEquipamento(int id) throws EntidadeNaoExiste {
        Equipamento e = equipamentos.get(id);
        if (e == null) throw new EntidadeNaoExiste();
        return e.getNome();
    }

    /**
     * Encontra o nome de um funcionario.
     * @param id id do funcionario.
     * @return Nome do funcionario.
     */
    public String getNomeFuncionario(int id) throws EntidadeNaoExiste {
        Funcionario f = funcionarios.get(id);
        if (f == null) throw new EntidadeNaoExiste();
        return f.getNome();
    }

    /**
     * Verifica se um funcionario é gestor.
     * @param id id do funcionario.
     * @return Se o funcionario é ou não gestor
     */
    public boolean isGestor(int id) throws EntidadeNaoExiste {
        Funcionario f = funcionarios.get(id);
        if (f == null) throw new EntidadeNaoExiste();
        return f.isGestor();
    }

    /**
     * Verifica se um funcionario é tecnico.
     * @param id id do funcionario.
     * @return Se o funcionario é ou não gestor.
     */
    public boolean isTecnico(int id) throws EntidadeNaoExiste {
        Funcionario f = funcionarios.get(id);
        if (f == null) throw new EntidadeNaoExiste();
        return f.isTecnico();
    }

    /**
     * Verifica se um funcionario é empregado de balcao.
     * @param id id do funcionario.
     * @return Se o funcionario é ou não empregado de balcao.
     */
    public  boolean isEmpregadoBalcao(int id)throws EntidadeNaoExiste {
        Funcionario f = funcionarios.get(id);
        if (f == null) throw new EntidadeNaoExiste();
        return f.isEmpregadoBalcao();
    }

    public int getNumberOfTecnicos(){
        return (int) funcionarios.values().stream().filter(Funcionario :: isTecnico).count();
    }

    /**
     * Guardar todas as entidades em ficheiros que depois vai conseguir ler
     */
    public void saveInfo(){
        dataDAO.saveInstanceHashMap(funcionarios,"funcionarios");
        dataDAO.saveInstanceHashMap(clientes,"clientes");
        dataDAO.saveInstanceHashMap(equipamentos,"equipamentos");
    }

}
