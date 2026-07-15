package gerenciador.operacoes.movimentacoes;

import java.time.LocalDate;
import java.util.ArrayList;
import gerenciador.exceptions.OrcamentoExcedidoException;
import gerenciador.exceptions.SaldoInsuficienteException;
import gerenciador.suporte.*;

public class Despesa extends Transacao{

    //JSON precisa de um construtor vazio para conseguir construir os objetos quando carregar
    public Despesa(){
        super();
    }

    public Despesa(String nome, String id, double valor, ArrayList<Tag> tags, Categoria categoria, LocalDate data, Conta conta){
        super(nome, id, valor, tags, categoria, data, conta);
    }

    public void realizarTransacao() {
        //chama a função abaixo sem ignorar orçamento -> ato de ignorar: parte da lógica do front
        realizarTransacao(false);
    }

    public void realizarTransacao(boolean ignorarOrcamento) {
        double gastoAtual = this.getCategoria().gastoNoMes();
        double orcamento = this.getCategoria().getOrcamento();

        if (!ignorarOrcamento && orcamento > 0 && (gastoAtual + this.getValor()) > orcamento) {
            throw new OrcamentoExcedidoException(this.getCategoria(), gastoAtual + this.getValor());
        }

        if (this.getValor() > this.getConta().getMontante()){
            throw new SaldoInsuficienteException(this.getConta().getMontante(), this.getValor());
        }

        if (!this.getData().isAfter(LocalDate.now())){
            this.getConta().debitar(this.getValor());
        }
    }
}
