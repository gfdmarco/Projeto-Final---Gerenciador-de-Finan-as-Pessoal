package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.enums.TipoFundo;

public abstract class Fundo {
    private String nome;
    private TipoFundo tipo;
    private double valorObjetivo;
    private double taxaDeValorizacao;
    private LocalDate dataInicio;

    public Fundo(String nome, TipoFundo tipo, double valorObjetivo, double taxaDeValorizacao, LocalDate dataInicio) {
        this.nome = nome;
        this.tipo = tipo;
        this.valorObjetivo = valorObjetivo;
        this.taxaDeValorizacao = taxaDeValorizacao;
        this.dataInicio = dataInicio;
    }

    public String getNome() {
        return this.nome;
    }
    
    public TipoFundo getTipoFundo() {
        return this.tipo;
    }

    public double getObjetivo() {
        return this.valorObjetivo;
    }

    public double getTaxaValorizacao() {
        return this.taxaDeValorizacao;
    }

    public LocalDate getDataInicio() {
        return this.dataInicio;
    }

    public void setValorObjetivo(double novoValorObjetivo) {
        this.valorObjetivo = novoValorObjetivo;
    }
}
