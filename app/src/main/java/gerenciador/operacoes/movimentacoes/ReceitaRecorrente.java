package gerenciador.operacoes.movimentacoes;

import gerenciador.interfaces.Recorrencia;
import gerenciador.suporte.*;
import gerenciador.enums.Frequencia;

import java.time.LocalDate;
import java.util.ArrayList;

public class ReceitaRecorrente extends Receita implements Recorrencia {

    private LocalDate dataInicio;
    private boolean ativo;
    private Frequencia frequencia;

    public ReceitaRecorrente(String nome, String id, double valor, ArrayList<Tag> tags, Categoria categoria, LocalDate data, Conta conta, String fonte, Frequencia frequencia, LocalDate dataInicio) {
        super(nome, id, valor, tags, categoria, data, conta, fonte);
        this.dataInicio = dataInicio;
        this.ativo = true;
        this.frequencia = frequencia;
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

    public Transacao gerarTransacaoRecorrente(LocalDate data) {
        return new Despesa(nome, id, valor, tags, categoria, data, contaAtrelada);
    }
}