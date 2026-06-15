package gerenciador.base;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import gerenciador.interfaces.GastoMensalListener;
import gerenciador.operacoes.*;
import gerenciador.operacoes.movimentacoes.*;
import gerenciador.operacoes.reservas.*;
import gerenciador.suporte.*;
import gerenciador.interfaces.Relatorio;
import gerenciador.enums.*;

public class Usuario {
    //
    //MUITAS FUNCOES PRA ADICIONAR AINDA
    //
    private String nome;
    private String login;
    private String senhaHasheada;
    private ReceitaRecorrente salario;
    private ArrayList<Conta> contas;
    private ArrayList<Meta> metas;
    private ArrayList<Fundo> fundos;
    private HistoricoTransacoes transacoes;
    private ArrayList<DespesaRecorrente> despesasRecorrentes; //para construir o custo de vida mensal do usuario
    private List<GastoMensalListener> gastoMensalListeners;

    public Usuario(String nome, String login, String senhaHasheada){
            this.nome = nome;
            this.login = login;
            this.senhaHasheada = senhaHasheada;
            //this.salario = 0.0; ARRUMAR ISSO DAQUI
            this.contas = new ArrayList<>();
            this.metas = new ArrayList<>();
            this.fundos = new ArrayList<>();
            this.transacoes = new HistoricoTransacoes();
            this.despesasRecorrentes = new ArrayList<>();
            this.gastoMensalListeners = new ArrayList<>();
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

    public double getSalario(){
        //ERRO: verificar registro de salário antes
        return this.salario.getValor();
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

    public ArrayList<Transacao> getHistorico(){
        return this.transacoes.getHistorico();
    }

    public double getSaldoGeral(){
        double saldo = 0;
        for (Conta conta: contas){
            saldo += conta.getMontante();
        }
        return saldo;
    }

    //FALTA UM MÉTODO PARA PROJETAR SALDO FUTURO BASEADO NAS DESPESAS RECORRENTES ATUAIS

    public void gerarRelatorio(Relatorio relatorio){
        relatorio.gerar(this.transacoes.getHistorico(), this);
    }

    public void criarMeta(TipoMeta tipo, double objetivo, LocalDate prazo){
        Meta novaMeta = new Meta(tipo, objetivo, prazo);
        this.metas.add(novaMeta);
    }

    public void registrarSalario(Conta conta, double valor){
        Categoria categoria = new Categoria("Salário", valor);
        this.salario = new ReceitaRecorrente("Salário", "00", valor, new ArrayList<>(), categoria, LocalDate.now(), conta, Frequencia.MENSAL, LocalDate.now());;
    }

    public void adicionarTransacao(Transacao transacao){
        this.transacoes.getHistorico().add(transacao);
    }

    public void adicionarDespesaRecorrente(DespesaRecorrente despesaRecorrente) {
        this.despesasRecorrentes.add(despesaRecorrente);
        notificarGastoMensalListeners();
    }

    public void adicionarGastoMensalListener(GastoMensalListener listener) {
        this.gastoMensalListeners.add(listener);
    }

    private double calcularGastoMensal() {
        double gastoMensal = 0;

        for(DespesaRecorrente despesaRecorrente : despesasRecorrentes) {
            switch (despesaRecorrente.getRecorrencia()) {
                case SEMANAL:
                    gastoMensal += 4 * despesaRecorrente.getValor();
                    break;
                case QUINZENAL:
                    gastoMensal += 2 * despesaRecorrente.getValor();
                    break;
                case MENSAL:
                    gastoMensal += despesaRecorrente.getValor();
                    break;
                case TRIMESTRAL:
                    gastoMensal += 0.34 * despesaRecorrente.getValor();
                    break;
                case SEMESTRAL:
                    gastoMensal += 0.167 * despesaRecorrente.getValor();
                    break;
                case ANUAL:
                    gastoMensal += 0.083 * despesaRecorrente.getValor();
                    break;
                default:
                    break;
            }
        }
        return gastoMensal;
    }

    public void notificarGastoMensalListeners() {
        double novoGastoMensal = calcularGastoMensal();
        for (GastoMensalListener gastoMensalListener : gastoMensalListeners) {
            gastoMensalListener.updateGastoMensal(novoGastoMensal);
        }
    }

    public void removerTransacao(String id){
        //arrays abaixos utilizados para evitar remover durante iteração
        ArrayList<Transacao> aRemoverGeral = new ArrayList<>();

        for (Transacao transacao : transacoes.getHistorico()){
            if (transacao.getID().equals(id)){
                aRemoverGeral.add(transacao);
            }
        }
        this.transacoes.getHistorico().removeAll(aRemoverGeral);
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
            if (Math.abs(transacao.getValor() - valor) < 0.001){ //double tem imprecisão de comparação e não pode usar equals com ele
                encontradas.add(transacao);
            }
        }
        return encontradas;
    }
}
