package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.enums.TipoFundo;

public class FundoInvestimento extends Fundo {

    private String tipoInvestimento;

    //JSON precisa de um construtor vazio para conseguir construir os objetos quando carregar
    public FundoInvestimento(){
        super();
    }

    //Fundo de investimento não tem valor objetivo definido
    public FundoInvestimento (String nome, TipoFundo tipo, double taxaDeValorizacao, LocalDate dataInicio, String tipoInvestimento) 
    {
        super(nome, tipo, 0, taxaDeValorizacao, dataInicio);
        this.tipoInvestimento = tipoInvestimento;
    }

    public String getTipoInvestimento() {
        return this.tipoInvestimento;
    }
}
