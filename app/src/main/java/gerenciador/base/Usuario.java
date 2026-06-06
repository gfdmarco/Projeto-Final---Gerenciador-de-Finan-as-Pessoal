package gerenciador.base;

import java.time.LocalDate;
import java.util.ArrayList;

import gerenciador.operacoes.Meta;
import gerenciador.operacoes.movimentacoes.Transacao;
import gerenciador.operacoes.reservas.Fundo;
import gerenciador.suporte.*;

public class Usuario {
    //
    //MUITAS FUNCOES PRA ADICIONAR AINDA
    //
    private String nome;
    private String login;
    private String senhaHasheada;
    private double saldo;
    private double salario;
    private ArrayList<Conta> contas;
    private ArrayList<Meta> metas;
    private ArrayList<Fundo> fundos;
    private HistoricoTransacoes transacoes;

    public Usuario(String nome, String login, String senhaHasheada){
            this.nome = nome;
            this.login = login;
            this.senhaHasheada = senhaHasheada;
            this.saldo = 0.0;
            this.salario = 0.0;
            this.contas = new ArrayList<>();
            this.metas = new ArrayList<>();
            this.fundos = new ArrayList<>();
            this.transacoes = new HistoricoTransacoes();
    }

    public String getNome(){
        return this.nome;
    }

    public String getLogin(){
        return this.login;
    }

    public String getSenhaHasheada(){
        return this.senhaHasheada;
    }

    public double getSaldo(){
        int saldo = 0;
        for (Conta conta: contas){
            saldo += conta.getMontante();
        }
        this.saldo = saldo;
        return this.saldo;
    }

    public double getSalario(){
        return this.salario;
    }

    public ArrayList<Conta> getContas(){
        return this.contas;
    }
    public ArrayList<Meta> getMetas(){
        return this.metas;
    }
    public ArrayList<Fundo> getFundos(){
        return this.fundos;
    }

    public HistoricoTransacoes getHistorico(){
        for (Conta conta : this.contas){
            for (Transacao transacao : conta.getTransacoesAssociadas()){
                this.transacoes.getHistorico().add(transacao);
            }
        }
        return this.transacoes;
    }

    public void gerarRelatorio(){
        //a fazer depois
    }

    public void criarMeta(){
        //fazer depois
    }

    public void registrarSalario(double valor){
        this.salario = valor;
    }

    public void adicionarTransacao(Transacao transacao){
        this.transacoes.getHistorico().add(transacao);
    }

    public void removerTransacao(String id){
       for (Transacao transacao : transacoes.getHistorico()){
            if (transacao.getID().equals(id)){
                //remoção da transação em todas as instâncias que ela existe
                transacao.getCategoria().getTransacoesAssociadas().remove(transacao);
                transacao.getConta().getTransacoesAssociadas().remove(transacao);
                for (Tag tag : transacao.getTags()){
                    tag.getTransacoesAssociadas().remove(transacao);
                }
                this.transacoes.getHistorico().remove(transacao);
            }
       }

    }

    public ArrayList<Transacao> buscarTransacao(LocalDate data){
        ArrayList<Transacao> encontradas = new ArrayList<>();
        for (Transacao transacao : transacoes.getHistorico()){
            if (transacao.getData().equals(data)){
                encontradas.add(transacao);
            }
        }
        return encontradas;
    }

    public ArrayList<Transacao> buscarTransacao(Categoria categoria){
        ArrayList<Transacao> encontradas = new ArrayList<>();
        for (Transacao transacao : transacoes.getHistorico()){
            if (transacao.getCategoria().equals(categoria)){
                encontradas.add(transacao);
            }
        }
        return encontradas;
    }

    public ArrayList<Transacao> buscarTransacao(Tag tag){
        ArrayList<Transacao> encontradas = new ArrayList<>();
        for (Transacao transacao : transacoes.getHistorico()){
            for (Tag tagTransacao : transacao.getTags()){
                if (tagTransacao.equals(tag)){
                    encontradas.add(transacao);
                }
            }
        }
        return encontradas;
    }

    public ArrayList<Transacao> buscarTransacao(Conta conta){
        ArrayList<Transacao> encontradas = new ArrayList<>();
        for (Transacao transacao : transacoes.getHistorico()){
            if (transacao.getConta().equals(conta)){
                encontradas.add(transacao);
            }
        }
        return encontradas;
    }

    public ArrayList<Transacao> buscarTransacao(double valor){
        ArrayList<Transacao> encontradas = new ArrayList<>();
        for (Transacao transacao : transacoes.getHistorico()){
            if (transacao.getValor() == valor){
                encontradas.add(transacao);
            }
        }
        return encontradas;
    }
}
