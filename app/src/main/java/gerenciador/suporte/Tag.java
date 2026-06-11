package gerenciador.suporte;

import java.util.ArrayList;
import gerenciador.operacoes.movimentacoes.Transacao;

public class Tag {
    private String nome;
    HistoricoTransacoes transacoes;

    public Tag(String nome){
        this.nome = nome;
        this.transacoes = new HistoricoTransacoes();
    }

    public String getNome(){
        return this.nome;
    }

    public ArrayList<Transacao> getTransacoesAssociadas(){
        return this.transacoes.getHistorico();
    }

    public void adicionarTransacao(Transacao transacao){
        this.transacoes.getHistorico().add(transacao);
    }
}
