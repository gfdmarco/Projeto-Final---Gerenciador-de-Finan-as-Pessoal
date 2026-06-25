package gerenciador.suporte;

import java.util.ArrayList;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gerenciador.exceptions.SaldoInsuficienteException;
import gerenciador.operacoes.movimentacoes.Transacao;

public class Conta {
    private String banco;
    private String id;
    private double montante; 
    //JSON ignora esta serialização para não duplicar dados, salvando apenas o histórico geral do usuário
    @JsonIgnore
    private HistoricoTransacoes transacoes;

    public Conta(){
        this.transacoes = new HistoricoTransacoes();
    }

    public Conta(String banco, String id, double montante){
        this.banco = banco;
        this.id = id;
        this.montante = montante;
        this.transacoes = new HistoricoTransacoes();
    }

    public String getBanco(){
        return this.banco;
    }

    public String getID(){
        return this.id;
    }

    public double getMontante(){
        return this.montante;
    }

    public void debitar(double valor){
        if (valor > this.montante){
            throw new SaldoInsuficienteException(this.montante, valor);
        }
        else {
            this.montante -= valor;
        }
    }

    public void creditar(double valor){
        this.montante += valor;
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

    @Override
    public boolean equals(Object obj){
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Conta)){
            return false;
        }
        Conta outra = (Conta) obj;
        //Conta compara por id, pois podem ter contas diferentes com o mesmo nome (bancos iguais)
        return Objects.equals(this.id, outra.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.id);
    }

     //método necessário para exibir o nome e não o objeto no front
    @Override
    public String toString(){
        return this.banco + " (" + this.id + ")";
    }
}
