package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.enums.TipoFundo;

public class FundoInvestimento extends Fundo {

    //JSON precisa de um construtor vazio para conseguir construir os objetos quando carregar
public FundoInvestimento(){
        super();
    }

    public FundoInvestimento (String nome, TipoFundo tipo, double objetivo, double taxaDeValorizacao, LocalDate dataInicio){
        super(nome, tipo, objetivo, taxaDeValorizacao, dataInicio);
    }
}
