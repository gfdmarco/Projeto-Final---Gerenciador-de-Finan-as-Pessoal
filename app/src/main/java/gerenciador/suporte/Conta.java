package gerenciador.suporte;

import java.util.ArrayList;

import gerenciador.exceptions.SaldoInsuficienteException;
import gerenciador.operacoes.movimentacoes.Transacao;

public class Conta {
    private String banco;
    String id;
    double montante; 
    HistoricoTransacoes transacoes;

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
        this.transacoes.getHistorico().add(transacao);
    }
}
