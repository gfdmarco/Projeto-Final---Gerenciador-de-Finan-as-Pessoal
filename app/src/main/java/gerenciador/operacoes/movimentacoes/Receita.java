package gerenciador.operacoes.movimentacoes;

import java.time.LocalDate;
import java.util.ArrayList;

import gerenciador.suporte.*;

public class Receita extends Transacao{
    private String fonte;

    public Receita(String nome, String id, double valor, ArrayList<Tag> tags, Categoria categoria, LocalDate data, Conta conta, String fonte){
        super(nome, id, valor, tags, categoria, data, conta);
        this.fonte = fonte;
    }

    public String getFonte(){
        return this.fonte;
    }

    @Override
    public void realizarTransacao(){
        this.getConta().creditar(this.getValor());
    }
}
