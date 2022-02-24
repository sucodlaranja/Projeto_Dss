package dss.projeto.Model.Pedidos.Pedido;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record InfoPedidoTerminado(LocalDateTime time,
                                  String descricao,
                                  boolean isExpresso,
                                  boolean isBaixa,
                                  int idCliente,
                                  String equipamento,
                                  int idEmpregadoBalcaoRegisto,
                                  int idEmpregadoBalcaoEntrega,
                                  int idFuncionarioExpresso,
                                  List<Passo> passosExecutados) implements Serializable {

    public Set<Integer> getIdsFuncionarios(){
        Set<Integer> ids = new HashSet<>();
        if (isExpresso) ids.add(idFuncionarioExpresso);
        else if(passosExecutados != null) {
            for(Passo p : passosExecutados) ids.add(p.getIdTecnicoExecucao());
        }
        return ids;
    }


    /// Passar todos os passos para texto.
    public String passosToString(int idReparacao){
        if (isExpresso && idReparacao == idFuncionarioExpresso)
            return "Reparacao Expresso do equipamento:" + equipamento +
                    "\n  Descricao: " + descricao + ".\n";
        else{
            StringBuilder sb = new StringBuilder();

            sb.append("Reparacao Normal do equipamento:").append(equipamento)
                    .append("\n  Descricao: ").append(descricao).append(".\n");

            int cc = 1;
            for(Passo p : passosExecutados){
                if (p.getIdTecnicoExecucao() == idReparacao){
                    sb.append("  Passo Nº").append(cc).append(":\n")
                            .append("   ").append(p.toStringPassoConcluido()).append("\n");
                }
                cc++;
            }


            return sb.toString();
        }
    }
}
