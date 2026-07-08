package gerenciador.operacoes.reservas;

import gerenciador.enums.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import gerenciador.exceptions.*;
import java.time.temporal.ChronoUnit;

//PARTE NECESSÁRIA PARA IDENTIFICAR QUAL TIPO DE TRANSACAO ESTÁ SENDO SALVA E CONSEGUIR CARREGAR/SALVAR CORRETAMENTE
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipoFundoClasse")
@JsonSubTypes({
    @JsonSubTypes.Type(value = FundoEmergencia.class, name = "emergencia"),
    @JsonSubTypes.Type(value = FundoInvestimento.class, name = "investimento")
})
public abstract class Fundo {
    private String nome;
    private TipoFundo tipo;
    private double valorAcumulado;
    private double valorObjetivo;
    private double taxaDeValorizacao;
    private LocalDate dataInicio;

    //JSON precisa de um construtor vazio para conseguir construir os objetos quando carregar
    public Fundo(){
    }

    public Fundo(String nome, TipoFundo tipo, double valorObjetivo, double taxaDeValorizacao, LocalDate dataInicio){
        this.nome = nome;
        this.tipo = tipo;
        this.valorAcumulado = 0;
        this.valorObjetivo = valorObjetivo;
        this.taxaDeValorizacao = taxaDeValorizacao;
        this.dataInicio = dataInicio;
    }

    public void jurosCompostos(){
        LocalDate dataFim = LocalDate.now();
        long diasPassados = ChronoUnit.DAYS.between(this.dataInicio, dataFim); //este método trabalha com retorno de longs

        double taxaDiaria = Math.pow(1 + (this.taxaDeValorizacao / 100), 1.0 / 365) - 1;
        this.valorAcumulado = this.valorAcumulado * Math.pow((1 + taxaDiaria), diasPassados);
    }

    public String getNome(){
        return this.nome;
    }

    public double getSaldo(){
        return this.valorAcumulado;
    }

    public TipoFundo getTipo(){
        return this.tipo;
    }

    public double getObjetivo(){
        return this.valorObjetivo;
    }

    public void setObjetivo(double objetivo){
        this.valorObjetivo = objetivo;
    }

    public double getProgresso(){
        if (valorObjetivo > 0.0){
            double progresso = this.valorAcumulado / this.valorObjetivo;
            return progresso;
        }
        return 0;
    }

    public void depositar(double valor){
        if (valor > 0){
            this.valorAcumulado += valor;
        }
        else {
            throw new NumberFormatException("Insira um valor válido");
        }
    }

    public void sacar(double valor){
        if (valor > valorAcumulado){
            throw new SaldoInsuficienteException(valorAcumulado, valor);
        }
        else {
            if (valor > 0){
                this.valorAcumulado -= valor;
            }
            else {
                throw new NumberFormatException("Insira um valor válido");
            }
        }
    }
}
