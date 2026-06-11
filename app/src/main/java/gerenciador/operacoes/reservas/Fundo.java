package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.enums.TipoFundo;

public abstract class Fundo {
    private String nome;
    private TipoFundo tipo;
    private double taxaDeValorizacao;
    private double valorAtual;
    private LocalDate dataInicio;
    

    public Fundo(String nome, TipoFundo tipo, double taxaDeValorizacao, LocalDate dataInicio, double depositoInicial) {
        this.nome = nome;
        this.tipo = tipo;
        this.taxaDeValorizacao = taxaDeValorizacao;
        this.dataInicio = dataInicio;
        this.valorAtual = depositoInicial;
    }

    public String getNome() {
        return this.nome;
    }
    
    public TipoFundo getTipoFundo() {
        return this.tipo;
    }

    public double getTaxaValorizacao() {
        return this.taxaDeValorizacao;
    }

    public LocalDate getDataInicio() {
        return this.dataInicio;
    }

    public void atualizarValorFundo() {
        valorAtual *= taxaDeValorizacao;
        //Possivelmente utilizar padrão observer para notificar se passou um mês, e assim atualizar o valor.
        //Quando definirmos a maneira de guardar a data atual, podemos implementar isso.
    }

    public double getValorAtual() {
        return this.valorAtual;
    }
}
