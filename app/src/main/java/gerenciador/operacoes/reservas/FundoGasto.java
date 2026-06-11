package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.enums.TipoFundo;

public class FundoGasto extends Fundo{
    private double valorObjetivo;

    //Fundo de gasto tem valor de objetivo definido 
    public FundoGasto (String nome, TipoFundo tipo, double valorObjetivo, double taxaDeValorizacao, LocalDate dataInicio) 
    {
        super(nome, tipo, valorObjetivo, taxaDeValorizacao, dataInicio);
        this.valorObjetivo = valorObjetivo;
    }

    public double getValorObjetivo() {
        return this.valorObjetivo;
    }
}
