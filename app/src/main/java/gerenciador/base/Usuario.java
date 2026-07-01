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
    private Set<Categoria> categorias;
    private Set<Tag> tags;
    private HistoricoTransacoes transacoes;
    private ArrayList<DespesaRecorrente> despesasRecorrentes; //para construir o custo de vida mensal do usuario
    //JSON ignora esta serialização por não conter estado em si do usuário, mas ação durante a aplicação
    @JsonIgnore
    private List<GastoMensalListener> gastoMensalListeners;

    //JSON precisa de um construtor vazio para conseguir construir os objetos quando carregar
    public Usuario(){
            this.contas = new ArrayList<>();
            this.fundos = new ArrayList<>();
            this.categorias = new HashSet<>();
            this.tags = new HashSet<>();
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
            this.categorias = new HashSet<>();
            this.tags = new HashSet<>();
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

    public void transferirEntreContas(double valor, Conta c1, Conta c2){
        if (valor <= 0){
            throw new IllegalArgumentException("O valor da transferência deve ser positivo.");
        }
        if (c1 == null || c2 == null){
            throw new IllegalArgumentException("Selecione duas contas válidas.");
        }
        if (!this.contas.contains(c1) || !this.contas.contains(c2)){
            throw new IllegalArgumentException("As contas devem pertencer ao usuário.");
        }
        if (c1.getMontante() < valor){
            throw new IllegalArgumentException("Saldo insuficiente na conta de origem.");
        }

        c1.debitar(valor);
        c2.creditar(valor);

        Transacao transferencia = new Receita("Transferência entre contas", UUID.randomUUID().toString(), valor, new ArrayList<>(), null, LocalDate.now(), c2);
        this.transacoes.getHistorico().add(transferencia);
        c2.adicionarTransacao(transferencia);
    }

    public double projetarSaldoFuturo(int meses) {
        double saldoAtual = getSaldoGeral();
        double saldoLiquidoMensal = GanhoLiquidoMensal(); // receitaMensal - gastoMensal
        return saldoAtual + (saldoLiquidoMensal * meses);
    }

    public Fundo criarFundo(String nome, TipoFundo tipo, double objetivo, double taxaDeValorizacao, LocalDate depositoInicial, Conta conta){
        Fundo novoFundo = switch (tipo){
            case EMERGENCIA -> new FundoEmergencia(nome, TipoFundo.EMERGENCIA, objetivo, taxaDeValorizacao, depositoInicial, 6);
            case INVESTIMENTO -> new FundoInvestimento(nome, TipoFundo.INVESTIMENTO, objetivo, taxaDeValorizacao, depositoInicial);
        };
        this.fundos.add(novoFundo);
        return novoFundo;
    }

    public Conta abrirConta(String banco,String id, double montanteInicial){
        // criar conta e associar ao usuario
        Conta novaConta = new Conta(banco, id, montanteInicial);
        this.contas.add(novaConta);
        return novaConta;
    }

    public void setCategorias() {
        if (this.categorias == null) {
            this.categorias = new HashSet<>();
        }
        for (Transacao transacao : transacoes.getHistorico()) {
            if (transacao != null && transacao.getCategoria() != null) {
                this.categorias.add(transacao.getCategoria());
            }
        }
    }

    public void setTags() {
        if (this.tags == null) {
            this.tags = new HashSet<>();
        }
        for (Transacao transacao : transacoes.getHistorico()){
            if (transacao != null && transacao.getTags() != null) {
                for (Tag t : transacao.getTags()){
                    if (t != null){
                        this.tags.add(t);
                    }
                }
            }
        }
    }

    public Set<Categoria> categoriasSistema() {
        return this.categorias;
    }

    public Set<Tag> tagsSistema() {
        return this.tags;
    }

    public Categoria buscarCategoria(String nome){
        for (Categoria c : this.categoriasSistema()){
            if (c.getNome().equals(nome)){
                return c;
            }
        }
        return null; //não encontrado
    }

    public void gerarRelatorio(Relatorio relatorio){
        relatorio.gerar(this.transacoes.getHistorico(), this);
    }

    public void registrarSalario(Conta conta, double valor){
        Categoria categoria = new Categoria("Salário", valor);
        String id = UUID.randomUUID().toString();
        this.salario = new ReceitaRecorrente("Salário", id, valor, new ArrayList<>(), categoria, LocalDate.now(), conta, Frequencia.MENSAL, LocalDate.now());
        conta.creditar(valor);
        conta.adicionarTransacao((Transacao) this.salario);
        this.getHistorico().add(this.salario);
    }

    public void adicionarTransacao(Transacao transacao){
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
        if (id == null || id.isBlank()){
            return;
        }

        ArrayList<Transacao> aRemoverGeral = new ArrayList<>();

        for (Transacao transacao : transacoes.getHistorico()){
            if (transacao != null && id.equals(transacao.getID())){
                aRemoverGeral.add(transacao);
            }
        }

        for (Transacao transacao : aRemoverGeral) {
            if (transacao.getConta() != null) {
                transacao.getConta().getTransacoesAssociadas().remove(transacao);
            }
            if (transacao.getCategoria() != null) {
                transacao.getCategoria().getTransacoesAssociadas().remove(transacao);
            }
            if (transacao.getTags() != null) {
                for (Tag tag : transacao.getTags()) {
                    if (tag != null) {
                        tag.getTransacoesAssociadas().remove(transacao);
                    }
                }
            }

            if (transacao instanceof DespesaRecorrente) {
                despesasRecorrentes.remove(transacao);
                notificarGastoMensalListeners();
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
            if (Math.abs(transacao.getValor() - valor) < 0.00000000000001){ //double tem imprecisão de comparação e não pode usar equals com ele
                encontradas.add(transacao);
            }
        }
        return encontradas;
    }
}
