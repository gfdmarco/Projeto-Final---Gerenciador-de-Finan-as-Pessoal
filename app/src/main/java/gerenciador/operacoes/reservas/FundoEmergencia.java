package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.base.Usuario;
import gerenciador.enums.TipoFundo;

public class FundoEmergencia extends Fundo {
    private Usuario usuarioAssociado;
    private int mesesDeCoberturaIdeal;

    public FundoEmergencia(String nome, TipoFundo tipo, double valorObjetivo, double taxaDeValorizacao, LocalDate dataInicio, 
        Usuario usuarioAssociado){
        super(nome, tipo, valorObjetivo, taxaDeValorizacao, dataInicio);
        this.usuarioAssociado = usuarioAssociado;
    }

    public int coberturaMeses(){
        //olhar o que escrevi na classe do usuario
        /*gasto mensal: 6 vezes as despesas recorrentes num periodo de mes (1x mensais, 2x quinzenais, 1x mensal, 1/3x trimestral,
        1/6x semestral e 1/12x anual)*/
        return 6; //só colocando qualquer valor pra não ficar aparecendo um erro aqui
    }
}
