package dss.projeto.Model.Pedidos.Exceptions;

import dss.projeto.Model.Pedidos.Pedido.Estados;

public class EstadoIncorreto extends Exception {
    public EstadoIncorreto(Estados atual, Estados pretendido){

        super("Estado atual: " + atual + " " + "Estado pretendido: " + pretendido + " \n");
    }
}