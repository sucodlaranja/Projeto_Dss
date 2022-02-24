package dss.projeto.Model.Pedidos.Pedido;

/// Os possíveis estados de um pedido.
public enum Estados {
    OrcamentoPorCalcular,
    ACalcularOrcamento,
    EsperaConfirmacaoOrcamento,
    NecessitaReparacao,
    EmReparacao,
    ProntoParaLevantar,
    NecessitaBaixa
}
