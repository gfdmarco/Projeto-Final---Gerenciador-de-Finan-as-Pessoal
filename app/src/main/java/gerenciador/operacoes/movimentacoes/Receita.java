package gerenciador.operacoes.movimentacoes;

import java.time.LocalDate;
import java.util.ArrayList;

import gerenciador.suporte.*;

public class Receita extends Transacao{;

    //JSON precisa de um construtor vazio para conseguir construir os objetos quando carregar
    public Receita(){
        super();
    }

    public Receita(String nome, String id, double valor, ArrayList<Tag> tags, Categoria categoria, LocalDate data, Conta conta){
        super(nome, id, valor, tags, categoria, data, conta);
    }

    @Override
    public void realizarTransacao(){
        this.getConta().creditar(this.getValor());
    }
}
