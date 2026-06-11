package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.enums.TipoFundo;
import gerenciador.interfaces.GastoMensalListener;

public class FundoEmergencia extends Fundo implements GastoMensalListener {
    private int mesesDeCoberturaIdeal;
    private double valorObjetivo;

    public FundoEmergencia(String nome, TipoFundo tipo, double valorObjetivo, double taxaDeValorizacao, LocalDate dataInicio, 
        int mesesDeCoberturaIdeal){

        super(nome, tipo, valorObjetivo, taxaDeValorizacao, dataInicio);
        this.mesesDeCoberturaIdeal = mesesDeCoberturaIdeal;
    }

    public int coberturaMeses(){
        return this.mesesDeCoberturaIdeal;
    }

    public void updateGastoMensal(double novoGastoMensal) {
        this.valorObjetivo = coberturaMeses() * novoGastoMensal;
    }

    public double getValorObjetivo() {
        return this.valorObjetivo;
    }
}
