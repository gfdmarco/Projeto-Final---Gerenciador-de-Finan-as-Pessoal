package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.enums.TipoFundo;

public class FundoInvestimento extends Fundo {

    private String tipoInvestimento;

    //Fundo de investimento não tem valor objetivo definido
    public FundoInvestimento (String nome, TipoFundo tipo, double taxaDeValorizacao, LocalDate dataInicio, double depositoInicial, String tipoInvestimento) 
    {
        super(nome, tipo, taxaDeValorizacao, dataInicio, depositoInicial);
        this.tipoInvestimento = tipoInvestimento;
    }

    public String getTipoInvestimento() {
        return this.tipoInvestimento;
    }
}