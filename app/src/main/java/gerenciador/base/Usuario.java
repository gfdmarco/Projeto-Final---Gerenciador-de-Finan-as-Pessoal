package gerenciador.base;

import java.util.ArrayList;

import gerenciador.suporte.Conta;
import gerenciador.suporte.HistoricoTransacoes;

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
    private HistoricoTransacoes historicoGeral;

    public Usuario(String nome, String login, String senhaHasheada){
            this.nome = nome;
            this.login = login;
            this.senhaHasheada = senhaHasheada;
            this.saldo = 0.0;
            this.salario = 0.0;
            this.contas = new ArrayList<>();
            this.metas = new ArrayList<>();
            this.fundos = new ArrayList<>();
            this.historicoGeral = new HistoricoTransacoes();
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
        return this.saldo;
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
        return this.historicoGeral;
    }

    void gerarRelatorio(){
        //a fazer depois
    }

    void criarMeta(){
        //fazer depois
    }

    void registrarSalario(double valor){
        this.salario = valor;
    }
}
