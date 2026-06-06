package gerenciador.suporte;

import java.util.ArrayList;

import gerenciador.operacoes.movimentacoes.Transacao;

public class Categoria {
    private String nome;
    private double orcamento;
    HistoricoTransacoes transacoes;

    public Categoria(String nome, double orcamento){
        this.nome = nome;
        this.orcamento = orcamento;
        this.transacoes = new HistoricoTransacoes();
    }

    public String getNome(){
        return this.nome;
    }

    public double getOrcamento(){
        return this.orcamento;
    }

    public ArrayList<Transacao> getTransacoesAssociadas(){
        return this.transacoes.getHistorico(this);
    }

    public void adicionarTransacao(Transacao transacao){
        this.transacoes.getHistorico(this).add(transacao);
    }
}
