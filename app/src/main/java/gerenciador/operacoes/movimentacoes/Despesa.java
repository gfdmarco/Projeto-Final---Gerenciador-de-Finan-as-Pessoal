package gerenciador.operacoes.movimentacoes;

import java.time.LocalDate;
import java.util.ArrayList;

import gerenciador.exceptions.OrcamentoExcedidoException;
import gerenciador.suporte.*;

public class Despesa extends Transacao{
    public Despesa(String nome, String id, double valor, ArrayList<Tag> tags, Categoria categoria, LocalDate data, Conta conta){
        super(nome, id, valor, tags, categoria, data, conta);
    }

    @Override
    public void realizarTransacao(){
        //consertar isso aqui: preciso primeiro acumular o gasto total no mes, depois ver se o valor + o que ja foi gasto supera o orcamento
        if (this.getValor() > this.getCategoria().getOrcamento()){
            throw new OrcamentoExcedidoException(this.getCategoria(), this.getValor());
        }
        else {
            this.getConta().debitar(this.getValor());
        }
    }
}
