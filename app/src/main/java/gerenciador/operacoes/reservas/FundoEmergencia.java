package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.enums.TipoFundo;
import gerenciador.interfaces.GastoMensalListener;

public class FundoEmergencia extends Fundo implements GastoMensalListener {
    private int mesesDeCoberturaIdeal;

    //JSON precisa de um construtor vazio para conseguir construir os objetos quando carregar
    public FundoEmergencia(){
        super();
    }

    public FundoEmergencia(String nome, TipoFundo tipo, double valorObjetivo, double taxaDeValorizacao, LocalDate dataInicio,
        int mesesDeCoberturaIdeal){

        super(nome, tipo, valorObjetivo, taxaDeValorizacao, dataInicio);
        this.mesesDeCoberturaIdeal = mesesDeCoberturaIdeal;
    }

    public int coberturaMeses(){
        return this.mesesDeCoberturaIdeal;
    }

    public void updateGastoMensal(double novoGastoMensal) {
        this.setObjetivo(coberturaMeses() * novoGastoMensal);
    }

    public double getValorObjetivo() {
        return this.getObjetivo();
    }
}
