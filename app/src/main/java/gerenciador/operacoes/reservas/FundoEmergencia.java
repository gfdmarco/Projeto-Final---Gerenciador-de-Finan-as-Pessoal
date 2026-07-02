package gerenciador.operacoes.reservas;

import java.time.LocalDate;

import gerenciador.base.Usuario;
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

    public int coberturaMeses(Usuario u){
        if (u.calcularGastoMensal() <= 0.0){
            return 0;
        }
        return (int) (this.getSaldo() / u.calcularGastoMensal());
    }

    public void updateGastoMensal(double novoGastoMensal, Usuario u) {
        this.setObjetivo(coberturaMeses(u) * novoGastoMensal);
    }

    public int coberturaIdeal(Usuario u){
        return (int)(6 * u.calcularGastoMensal());
    }

    public double getValorObjetivo() {
        return this.getObjetivo();
    }
}
