package gerenciador.base;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gerenciador.interfaces.GastoMensalListener;
import gerenciador.operacoes.movimentacoes.*;
import gerenciador.operacoes.reservas.*;
import gerenciador.suporte.*;
import gerenciador.interfaces.Relatorio;
import gerenciador.enums.*;
import java.util.UUID;

public class Usuario {
    //
    //MUITAS FUNCOES PRA ADICIONAR AINDA -> OBS: talvez seja interessante substituir arrays por conjuntos(sets) em alguns atributos
    //
    private String nome;
    private String login;
    private String senhaHasheada;
    private ReceitaRecorrente salario;
    private ArrayList<Conta> contas;
    private ArrayList<Fundo> fundos;
    private HistoricoTransacoes transacoes;
    private ArrayList<DespesaRecorrente> despesasRecorrentes; //para construir o custo de vida mensal do usuario
    //JSON ignora esta serialização por não conter estado em si do usuário, mas ação durante a aplicação
    @JsonIgnore
    private List<GastoMensalListener> gastoMensalListeners;

    //JSON precisa de um construtor vazio para conseguir construir os objetos quando carregar
    public Usuario(){
            this.contas = new ArrayList<>();
            this.fundos = new ArrayList<>();
            this.transacoes = new HistoricoTransacoes();
            this.despesasRecorrentes = new ArrayList<>();
            this.gastoMensalListeners = new ArrayList<>();
    }

    public Usuario(String nome, String login, String senhaHasheada){
            this.nome = nome;
            this.login = login;
            this.senhaHasheada = senhaHasheada;
            this.contas = new ArrayList<>();
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
        if(this.salario == null){
            return 0.0;
        }
        return this.salario.getValor();
    }

    public ArrayList<Conta> getContas(){
        return this.contas;
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

    public double GanhoLiquidoMensal(){
        double receitaMensal = (salario != null) ? salario.getValor() : 0.0;
        double gastoMensal = calcularGastoMensal();
        return receitaMensal - gastoMensal;
    }

    public double projetarSaldoFuturo(int meses) {
    double saldoAtual = getSaldoGeral();
    double saldoLiquidoMensal = GanhoLiquidoMensal(); // receitaMensal - gastoMensal
    return saldoAtual + (saldoLiquidoMensal * meses);
    }

    /* public Fundo criarFundo(String nome, double objetivo, double taxaDeValorizacao, LocalDate depositoInicial, Conta conta){
        Fundo novoFundo = new Fundo(nome, objetivo, taxaDeValorizacao, LocalDate.now(), depositoInicial); //NAO PODE INSTANCIAR FUNDO
        this.fundos.add(novoFundo);
        return novoFundo;
    }
        */

    public Conta abrirConta(String banco,String id, double montanteInicial){
        // criar conta e associar ao usuario
        Conta novaConta = new Conta(banco, id, montanteInicial);
        this.contas.add(novaConta);
        return novaConta;
    }

    public Set<Categoria> categoriasSistema() {
        Set<Categoria> categorias = new HashSet<>();
        for (Transacao transacao : transacoes.getHistorico()) {
            if (transacao.getCategoria() != null) {
                categorias.add(transacao.getCategoria());
            }
        }
        return categorias;
    }

    public void gerarRelatorio(Relatorio relatorio){
        relatorio.gerar(this.transacoes.getHistorico(), this);
    }

    public void registrarSalario(Conta conta, double valor){
        Categoria categoria = new Categoria("Salário", valor);
        String id = UUID.randomUUID().toString();
        this.salario = new ReceitaRecorrente("Salário", id, valor, new ArrayList<>(), categoria, LocalDate.now(), conta, Frequencia.MENSAL, LocalDate.now());
        conta.creditar(valor);
        this.getHistorico().add(this.salario);
    }

    public void adicionarTransacao(Transacao transacao){
        transacao.realizarTransacao();
        this.transacoes.getHistorico().add(transacao);
        if (transacao.getConta() != null){
            transacao.getConta().adicionarTransacao(transacao);
        }
        if (transacao.getCategoria() != null){
            transacao.getCategoria().adicionarTransacao(transacao);
        }
        if (transacao.getTags() != null){
            for (Tag t : transacao.getTags()){
                t.adicionarTransacao(transacao);
            }
        }
        if (transacao instanceof DespesaRecorrente){
            despesasRecorrentes.add((DespesaRecorrente) transacao);
            notificarGastoMensalListeners();
        }
    }

    public void adicionarDespesaRecorrente(DespesaRecorrente despesaRecorrente) {
        this.despesasRecorrentes.add(despesaRecorrente);
        notificarGastoMensalListeners();
    }

    public void adicionarGastoMensalListener(GastoMensalListener listener) {
        if (this.gastoMensalListeners == null){
            this.gastoMensalListeners = new ArrayList<>();
        }
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
        if (gastoMensalListeners == null){
            return;
        }
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
        for (Transacao t : aRemoverGeral){
            for (Conta c : this.getContas()){
                if (t instanceof Despesa){
                    c.creditar(t.getValor());
                }
                else if (t instanceof Receita){
                    c.debitar(t.getValor());
                }
            }
        }

        //falta remover dos historicos das contas, tags e categorias associadas
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
