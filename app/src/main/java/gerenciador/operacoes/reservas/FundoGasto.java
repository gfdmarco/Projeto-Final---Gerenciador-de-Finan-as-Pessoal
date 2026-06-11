package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.enums.TipoFundo;

public class FundoGasto extends Fundo{
        private double valorObjetivo;

    //Fundo de gasto tem valor de objetivo definido 
    public FundoGasto (String nome, TipoFundo tipo, double taxaDeValorizacao, LocalDate dataInicio, double depositoInicial, double valorObjetivo) 
    {
        super(nome, tipo, taxaDeValorizacao, dataInicio, depositoInicial);
        this.valorObjetivo = valorObjetivo;
    }

    public double getValorObjetivo() {
        return this.valorObjetivo;
    }
}
