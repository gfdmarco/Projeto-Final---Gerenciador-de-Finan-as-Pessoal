package gerenciador.base;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gerenciador.interfaces.GanhoMensalListener;
import gerenciador.interfaces.GastoMensalListener;
import gerenciador.interfaces.Recorrencia;
import gerenciador.operacoes.movimentacoes.*;
import gerenciador.operacoes.reservas.*;
import gerenciador.suporte.*;
import gerenciador.interfaces.Relatorio;
import gerenciador.enums.*;
import gerenciador.exceptions.SaldoInsuficienteException;

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
    private ArrayList<ReceitaRecorrente> receitasRecorrentes; //para construir o ganho liquido mensal do usuario
    private ArrayList<DespesaRecorrente> despesasRecorrentes; //para construir o custo de vida mensal do usuario
    //JSON ignora esta serialização por não conter estado em si do usuário, mas ação durante a aplicação
    @JsonIgnore
    private List<GastoMensalListener> gastoMensalListeners;
    @JsonIgnore
    private List<GanhoMensalListener> ganhoMensalListeners;

    //JSON precisa de um construtor vazio para conseguir construir os objetos quando carregar
    public Usuario(){
            this.contas = new ArrayList<>();
            this.fundos = new ArrayList<>();
            this.categorias = new HashSet<>();
            this.tags = new HashSet<>();
            this.transacoes = new HistoricoTransacoes();
            this.receitasRecorrentes = new ArrayList<>();
            this.despesasRecorrentes = new ArrayList<>();
            this.gastoMensalListeners = new ArrayList<>();
            this.ganhoMensalListeners = new ArrayList<>();
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
            this.receitasRecorrentes = new ArrayList<>();
            this.despesasRecorrentes = new ArrayList<>();
            this.gastoMensalListeners = new ArrayList<>();
            this.ganhoMensalListeners = new ArrayList<>();
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
        double receitaMensal = ((salario != null) ? salario.getValor() : 0.0) + calcularGanhoMensal();
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
            throw new SaldoInsuficienteException(c1.getMontante(), valor);
        }

        c1.debitar(valor);
        c2.creditar(valor);
        
        //orcamento dessas transferencias: nunca é checado, entao nao precisa ter orcamento definido valido
        Transacao transferencia = new Receita("Transferência entre contas", UUID.randomUUID().toString(), valor, new ArrayList<>(), new Categoria("Transferências", 0.0), LocalDate.now(), c2);
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
            case EMERGENCIA -> new FundoEmergencia(nome, TipoFundo.EMERGENCIA, objetivo, taxaDeValorizacao, depositoInicial);
            case INVESTIMENTO -> new FundoInvestimento(nome, TipoFundo.INVESTIMENTO, objetivo, taxaDeValorizacao, depositoInicial);
        };
        if (novoFundo.getTipo() != null && novoFundo.getTipo().equals(TipoFundo.EMERGENCIA)){
            adicionarGastoMensalListener((FundoEmergencia) novoFundo);
        }
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
            if (c.getNome() != null && c.getNome().equals(nome)){
                return c;
            }
        }
        return null; //não encontrado
    }

    public void gerarRelatorio(Relatorio relatorio){
        relatorio.gerar(this.transacoes.getHistorico(), this);
    }

    public void registrarSalario(Conta conta, double valor, LocalDate dataRecebimento){
        Categoria categoria = new Categoria("Salário", valor);
        String id = UUID.randomUUID().toString();
        this.salario = new ReceitaRecorrente("Salário", id, valor, new ArrayList<>(), categoria, dataRecebimento, conta, Frequencia.MENSAL, dataRecebimento);
        this.salario.realizarTransacao();
        conta.adicionarTransacao((Transacao) this.salario);
        this.getHistorico().add(this.salario);
    }

    public void editarSalario(double novoValor){
        if (this.salario == null){
            throw new IllegalStateException("Nenhum salário registrado para editar.");
        }
        this.salario.setValor(novoValor);
    }

    public void removerSalario(){
        this.salario = null;
    }

    public void adicionarTransacao(Transacao transacao) {
        adicionarTransacao(transacao, false);
    }

    public void adicionarTransacao(Transacao transacao, boolean ignorarOrcamento) {
        if (transacao instanceof Despesa && ignorarOrcamento) {
            ((Despesa) transacao).realizarTransacao(true);
        } else {
            transacao.realizarTransacao(); // lança OrcamentoExcedidoException normalmente
        }
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
        if (transacao instanceof ReceitaRecorrente){
            receitasRecorrentes.add((ReceitaRecorrente) transacao);
            notificarGanhoMensalListeners();
        }
    }

    public void adicionarDespesaRecorrente(DespesaRecorrente despesaRecorrente) {
        this.despesasRecorrentes.add(despesaRecorrente);
        notificarGastoMensalListeners();
    }

    public void adicionarReceitaRecorrente(ReceitaRecorrente receitaRecorrente){
        this.receitasRecorrentes.add(receitaRecorrente);
        notificarGanhoMensalListeners();
    }

    public void adicionarGastoMensalListener(GastoMensalListener listener) {
        if (this.gastoMensalListeners == null){
            this.gastoMensalListeners = new ArrayList<>();
        }
        this.gastoMensalListeners.add(listener);
    }

    public void adicionarGanhoMensalListener(GanhoMensalListener listener) {
        if (this.ganhoMensalListeners == null){
            this.ganhoMensalListeners = new ArrayList<>();
        }
        this.ganhoMensalListeners.add(listener);
    }

    public double calcularGastoMensal() {
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

    public double calcularGanhoMensal(){
        double ganhoMensal = 0;

        for (ReceitaRecorrente receitaRecorrente : receitasRecorrentes){
            switch(receitaRecorrente.getRecorrencia()){
                case SEMANAL:
                    ganhoMensal += 4 * receitaRecorrente.getValor();
                    break;
                case QUINZENAL:
                    ganhoMensal += 2 * receitaRecorrente.getValor();
                    break;
                case MENSAL:
                    ganhoMensal += receitaRecorrente.getValor();
                    break;
                case TRIMESTRAL:
                    ganhoMensal += 0.34 * receitaRecorrente.getValor();
                    break;
                case SEMESTRAL:
                    ganhoMensal += 0.167 * receitaRecorrente.getValor();
                    break;
                case ANUAL:
                    ganhoMensal += 0.083 * receitaRecorrente.getValor();
                    break;
                default:
                    break;
            }
        }
        return ganhoMensal;
    }

    public void notificarGastoMensalListeners() {
        double novoGastoMensal = calcularGastoMensal();
        if (gastoMensalListeners == null){
            return;
        }
        for (GastoMensalListener gastoMensalListener : gastoMensalListeners) {
            gastoMensalListener.updateGastoMensal(novoGastoMensal, this);
        }
    }

    public void notificarGanhoMensalListeners(){
        double novoGanhoMensal = calcularGanhoMensal();
        if (ganhoMensalListeners == null){
            return;
        }
        for (GanhoMensalListener ganhoMensalListener : ganhoMensalListeners){
            ganhoMensalListener.updateGanhoMensal(novoGanhoMensal, this);
        }
    }

    public List<String> processarRecorrencias() {
        //retornamos list para, na tela de login, serem exibidos avisos de transacoes recorrente que deram algum problema no saldo durante a recorrencia
        LocalDate hoje = LocalDate.now();
        List<String> avisos = new ArrayList<>();

        // iteramos uma cópia porque adicionarTransacao() logo abaixo insere na mesma lista viva
        // do histórico -- iterar a lista original causaria erro assim que qualquer ocorrência recorrente fosse aplicada com sucesso
        for (Transacao t : new ArrayList<>(this.transacoes.getHistorico())) {
            if (!(t instanceof Recorrencia rec) || !rec.isAtivo()){
                continue;
            }

            LocalDate proxima = rec.calcularProximaData(rec.getUltimaDataAplicada(), rec.getRecorrencia());

            while (!proxima.isAfter(hoje)) {
                Transacao ocorrencia = rec.gerarTransacaoRecorrente(proxima);
                try {
                    if (ocorrencia instanceof Despesa despesa) {
                        this.adicionarTransacao(despesa, true); // ignora orçamento: já é compromisso existente
                    } else {
                        this.adicionarTransacao(ocorrencia);
                    }
                    rec.setUltimaDataAplicada(proxima); // só avança se realmente aplicou
                } catch (SaldoInsuficienteException e) {
                    avisos.add(ocorrencia.getNome() + " (" + proxima + "): " + e.getMessage());
                    rec.setUltimaDataAplicada(proxima); // decide se quer parar aqui em vez de avançar — te explico a troca abaixo
                }
                proxima = rec.calcularProximaData(proxima, rec.getRecorrencia());
            }
        }
        return avisos;
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
                //precisamos reverter a cobranca ou aumento de saldo por conta da transacao antes de remove-la
                if (transacao instanceof Despesa){
                    if (!transacao.getData().isAfter(LocalDate.now())){
                        transacao.getConta().creditar(transacao.getValor());
                    }
                }
                if (transacao instanceof Receita){
                    if (!transacao.getData().isAfter(LocalDate.now())){
                        transacao.getConta().debitar(transacao.getValor());
                    }
                }
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

            if (transacao instanceof ReceitaRecorrente){
                receitasRecorrentes.remove(transacao);
                notificarGanhoMensalListeners();
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
