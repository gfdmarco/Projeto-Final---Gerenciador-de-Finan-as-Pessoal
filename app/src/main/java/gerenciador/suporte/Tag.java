package gerenciador.suporte;

import java.util.ArrayList;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gerenciador.operacoes.movimentacoes.Transacao;

public class Tag {
    private String nome;
    //JSON ignora esta serialização para não duplicar dados, salvando apenas o histórico geral do usuário
    @JsonIgnore
    private HistoricoTransacoes transacoes;

    public Tag(){
        this.transacoes = new HistoricoTransacoes();
    }

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
        if (this.transacoes == null){
            this.transacoes = new HistoricoTransacoes();
        }
        this.transacoes.getHistorico().add(transacao);
    }

    /* Os overrides abaixo são para reescrever métodos padrões da classe Object,
    pois precisamos disso para comparar e mexer com os objetos específicos do projeto */

    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Tag)){
            return false;
        }
        Tag outra = (Tag) obj;
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
