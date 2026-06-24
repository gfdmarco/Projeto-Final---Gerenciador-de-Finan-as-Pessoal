package gerenciador.operacoes.movimentacoes;

import gerenciador.interfaces.Recorrencia;
import gerenciador.suporte.*;
import gerenciador.enums.Frequencia;

import java.time.LocalDate;
import java.util.ArrayList;

public class DespesaRecorrente extends Despesa implements Recorrencia {

    private LocalDate dataInicio;
    private boolean ativo;
    private Frequencia frequencia;

    public DespesaRecorrente(String nome, String id, double valor, ArrayList<Tag> tags, Categoria categoria, LocalDate data, Conta conta, Frequencia frequencia, LocalDate dataInicio) {
        super(nome, id, valor, tags, categoria, data, conta);
        this.dataInicio = dataInicio;
        this.ativo = true;
        this.frequencia = frequencia;//ERRO DE NULL NA EXIBICAO TALVEZ
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

    @Override
    public Transacao gerarTransacaoRecorrente(LocalDate data) {
        return new Despesa(this.getNome(), this.getID(), this.getValor(), this.getTags(), this.getCategoria(), this.getData(), this.getConta());
    }
}