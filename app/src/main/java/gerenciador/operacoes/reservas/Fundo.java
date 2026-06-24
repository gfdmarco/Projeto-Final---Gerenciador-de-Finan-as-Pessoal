package gerenciador.operacoes.reservas;

import gerenciador.enums.*;

import java.time.LocalDate;

import gerenciador.exceptions.*;
import java.time.temporal.ChronoUnit;

public abstract class Fundo {
    private String nome;
    private TipoFundo tipo;
    private double valorAcumulado;
    private double valorObjetivo;
    private double taxaDeValorizacao;
    private LocalDate dataInicio;

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

    public String getProgresso(){
        double progressoNum = this.valorAcumulado / this.valorObjetivo;
        String progresso = (progressoNum + "%");
        return progresso;
    }

    public void depositar(double valor){
        this.valorAcumulado += valor;
    }

    public void sacar(double valor){
        if (valor > valorAcumulado){
            throw new SaldoInsuficienteException(valorAcumulado, valor);
        }
        else {
            valorAcumulado -= valor;
        }
    }
}
