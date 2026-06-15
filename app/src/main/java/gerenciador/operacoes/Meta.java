package gerenciador.operacoes;

import java.time.LocalDate;

import gerenciador.base.Usuario;
import gerenciador.enums.*;
import gerenciador.operacoes.reservas.Fundo;
import gerenciador.suporte.Conta;

public class Meta {
    private TipoMeta tipo;
    private double valorObjetivo;
    private LocalDate prazo;

    public Meta(TipoMeta tipo, double valorObjetivo, LocalDate prazo){
        this.tipo = tipo;
        this.valorObjetivo = valorObjetivo;
        this.prazo = prazo;
    }

    public TipoMeta getTipo(){
        return this.tipo;
    }

    public double getValorAcumulado(Usuario usuario){
        double valorAcumulado = 0.0;
        if (this.tipo.equals(TipoMeta.ECONOMIA)){
            for (Conta conta : usuario.getContas()){
                valorAcumulado += conta.getMontante();
            }
        }
        else if (this.tipo.equals(TipoMeta.INVESTIMENTO)){
            for (Fundo fundo : usuario.getFundos()){
                if (fundo.getTipo().equals(TipoFundo.INVESTIMENTO)){
                    valorAcumulado += fundo.getSaldo();
                }
            }
        }
        return valorAcumulado;
    }

    public LocalDate getPrazo(){
        return this.prazo;
    }

    public String getProgresso(Usuario usuario){
        if(this.valorObjetivo == 0){//nao divide por zero
            return "Sem valor objetivo definido ainda";
        }
        else{
            double progressoNum = Math.min(1.0, this.getValorAcumulado(usuario) / this.valorObjetivo);
            //limita a 100% 
            String progresso = String.format("%.2f%%", 100 * progressoNum);
            //o format evita que fique algo estranho aqui
            return progresso;
        }
    }

    public boolean isAtingida(Usuario usuario){
        return this.getValorAcumulado(usuario) >= this.valorObjetivo;
    }

    public boolean expirou(Usuario usuario){
        return (!this.isAtingida(usuario) && !this.prazo.isAfter(LocalDate.now()));
        //pequena mudanca que inclui o proprio dia de prazo como valido
    }
}
