package dss.projeto.Model.Entidades;

/// ‘Interface’ que define aquilo que a classe \ref GestorEntidades permite fazer.
public interface IGestorEntidades {

    /**
     * Adiciona o funcionário ao conjunto. \n
     * Prepara o id para o próximo funcionario.
     *
     * @param nome            Nome de um funcionario.
     * @param password        Senha de um funcionario.
     * @param gestor          Se este funcionario desempenha a funcao de gestor.
     * @param tecnico         Se este funcionario desempenha a funcao de tecnico.
     * @param empregadoBalcao Se este funcionario desempenha a funcao de empregadoBalcao.
     */
    void addFuncionario(String nome, String password, boolean gestor, boolean tecnico, boolean empregadoBalcao);

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
     * Verificar que existe tal funcionario.
     * @param nome     Nome do funcionario.
     * @param password Password do funcionario.
     */
    int isFuncionario(String nome, String password);


    /**
     * Encontra um cliente se estiver no conjunto
     *
     * @param nif NIF do cliente.
     * @return ID do cliente no conjunto.
     */
    int getIdCliente(String nif);

    /**
     * Encontra o endereço eletrónico de um cliente se estiver no conjunto
     * @return Email do cliente no conjunto.
     */
    String getEmailCliente(int idCliente) throws EntidadeNaoExiste;

    /**
     * Encontra o endereço eletrónico de um cliente se estiver no conjunto
     * @return Nome do cliente no conjunto.
     */
    String getNomeCliente(int idCliente) throws EntidadeNaoExiste;

    /**
     * Encontra o equipamento associado a este id
     * @param id O id do equipamento
     * @return O equipamento
     */
     String getEquipamento(int id) throws EntidadeNaoExiste;

    /**
     * Encontra o nome de um funcionario.
     * @param id id do funcionario.
     * @return Nome do funcionario.
     */
    String getNomeFuncionario(int id) throws EntidadeNaoExiste;

    /**
     * Verifica se um funcionario é gestor.
     * @param id id do funcionario.
     * @return Se o funcionario é ou não gestor
     */
    boolean isGestor(int id) throws EntidadeNaoExiste;

    /**
     * Verifica se um funcionario é tecnico.
     * @param id id do funcionario.
     * @return Se o funcionario é ou não gestor.
     */
    boolean isTecnico(int id) throws EntidadeNaoExiste;

    /**
     * Verifica se um funcionario é empregado de balcao.
     * @param id id do funcionario.
     * @return Se o funcionario é ou não empregado de balcao.
     */
    boolean isEmpregadoBalcao(int id) throws EntidadeNaoExiste;

    /**
     * Guardar todas as entidades em ficheiros que depois vai conseguir ler
     */
     void saveInfo();

    int getNumberOfTecnicos();
}
