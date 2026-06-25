package gerenciador.suporte;

import java.util.ArrayList;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gerenciador.operacoes.movimentacoes.Transacao;

public class Categoria {
    private String nome;
    private double orcamento;
    //JSON ignora esta serialização para não duplicar dados, salvando apenas o histórico geral do usuário
    @JsonIgnore
    private HistoricoTransacoes transacoes;

    public Categoria(){
        this.transacoes = new HistoricoTransacoes();
    }

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
        return this.transacoes.getHistorico();
    }

    public void adicionarTransacao(Transacao transacao){
        if (this.transacoes == null){
            this.transacoes = new HistoricoTransacoes();
        }
        this.transacoes.getHistorico().add(transacao);
    }

    public void editarOrcamento(){
        //opção de editar orçamento
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Categoria)){
            return false;
        }
        Categoria outra = (Categoria) obj;
        return Objects.equals(this.nome, outra.nome);
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.nome);
    }

    //método necessário para exibir o nome e não o objeto no front
    @Override
    public String toString(){
        return this.nome;
    }
}
