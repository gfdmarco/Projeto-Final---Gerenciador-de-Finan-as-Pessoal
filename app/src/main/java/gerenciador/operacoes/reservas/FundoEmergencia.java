package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.enums.TipoFundo;
import gerenciador.interfaces.GastoMensalListener;

public class FundoEmergencia extends Fundo implements GastoMensalListener{
    private int mesesDeCoberturaIdeal;

    public FundoEmergencia(String nome, TipoFundo tipo, double valorObjetivo, double taxaDeValorizacao, LocalDate dataInicio, int mesesDeCoberturaIdeal){
        super(nome, tipo, valorObjetivo, taxaDeValorizacao, dataInicio);
        this.mesesDeCoberturaIdeal = mesesDeCoberturaIdeal;
    }

    public int coberturaMeses(){
        //olhar o que escrevi na classe do usuario
        /*gasto mensal: 6 vezes as despesas recorrentes num periodo de mes (1x mensais, 2x quinzenais, 1x mensal, 1/3x trimestral,
        1/6x semestral e 1/12x anual)*/
        return this.mesesDeCoberturaIdeal; //só colocando qualquer valor pra não ficar aparecendo um erro aqui
    }

    public void updateGastoMensal(double novoGastoMensal) {
        setValorObjetivo(coberturaMeses() * novoGastoMensal);
    }
}
