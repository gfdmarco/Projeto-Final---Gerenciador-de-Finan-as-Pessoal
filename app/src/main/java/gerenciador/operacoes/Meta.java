package gerenciador.operacoes;

import java.time.LocalDate;

import gerenciador.enums.*;
import gerenciador.base.*;

public class Meta {
    private TipoMeta tipo;
    private double valor;
    private LocalDate prazo;

    public Meta(TipoMeta tipo, double valor, LocalDate prazo){
        this.tipo = tipo;
        this.valor = valor;
        this.prazo = prazo;
    }

    public TipoMeta getTipo(){
        return this.tipo;
    }

    public double getValor(){
        return this.valor;
    }

    public LocalDate getPrazo(){
        return this.prazo;
    }

    public String getProgresso(Usuario usuario){
        
    }

    public boolean isAtingida(Usuario usuario){
        return this.getProgresso(usuario).equals("100%");
    }

    public boolean expirou(Usuario usuario){
        return (!this.getProgresso(usuario).equals("100%") && this.prazo.isAfter(LocalDate.now()));
    }
}
