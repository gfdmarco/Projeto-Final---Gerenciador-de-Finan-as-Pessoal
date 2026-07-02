package gerenciador.operacoes.movimentacoes;

import gerenciador.interfaces.Recorrencia;
import gerenciador.suporte.*;
import gerenciador.enums.Frequencia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class ReceitaRecorrente extends Receita implements Recorrencia {

    private LocalDate dataInicio;
    private boolean ativo;
    private Frequencia frequencia;
    private LocalDate ultimaDataAplicada;

    //JSON precisa de um construtor vazio para conseguir construir os objetos quando carregar
    public ReceitaRecorrente(){
        super();
    }

    public ReceitaRecorrente(String nome, String id, double valor, ArrayList<Tag> tags, Categoria categoria, LocalDate data, Conta conta, Frequencia frequencia, LocalDate dataInicio) {
        super(nome, id, valor, tags, categoria, data, conta);
        this.dataInicio = dataInicio;
        this.ativo = true;
        this.frequencia = frequencia;
        this.ultimaDataAplicada = dataInicio; //inicialmente
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDate getDataInicio() {
        return this.dataInicio;
    }
    
    public Frequencia getRecorrencia() {
        return frequencia;
    }

    public LocalDate getUltimaDataAplicada(){
        return this.ultimaDataAplicada;
    }

    public void setUltimaDataAplicada(LocalDate data){
        this.ultimaDataAplicada = data;
    }

    @Override
    public Transacao gerarTransacaoRecorrente(LocalDate data) {
        return new Receita(this.getNome(), UUID.randomUUID().toString(), this.getValor(), this.getTags(), this.getCategoria(), data, this.getConta());
    }
}
