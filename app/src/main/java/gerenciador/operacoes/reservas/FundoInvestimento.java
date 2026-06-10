package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.enums.TipoFundo;

public class FundoInvestimento extends Fundo {

    private String tipoInvestimento;

    public FundoInvestimento (String nome, TipoFundo tipo, double valorObjetivo, double taxaDeValorizacao, LocalDate dataInicio, String tipoInvestimento) 
    {
        super(nome, tipo, valorObjetivo, taxaDeValorizacao, dataInicio);
        this.tipoInvestimento = tipoInvestimento;
    }
}