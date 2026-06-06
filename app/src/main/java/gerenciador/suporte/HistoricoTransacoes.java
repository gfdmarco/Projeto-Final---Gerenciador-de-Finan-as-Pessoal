package gerenciador.suporte;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import gerenciador.operacoes.movimentacoes.Transacao;

public class HistoricoTransacoes {
    private ArrayList<Transacao> transacoes;

    public HistoricoTransacoes(){
        this.transacoes = new ArrayList<>();
    }

    public ArrayList<Transacao> getHistorico(){
        return transacoes;
    }

    public ArrayList<Transacao> getHistorico(LocalDate inicio, LocalDate fim){
        ArrayList<Transacao> transacoesPeriodo = new ArrayList<>();
        for (Transacao transacao : transacoes){
            if (transacao.getData().isAfter(inicio) && transacao.getData().isBefore(fim)){
                transacoesPeriodo.add(transacao);
            }
        }
        return transacoesPeriodo;
    }

    public ArrayList<Transacao> getHistorico(Conta conta){
        ArrayList<Transacao> transacoesConta = new ArrayList<>();
        for (Transacao transacao : transacoes){
            if (transacao.getConta().equals(conta)){
                transacoesConta.add(transacao);
            }
        }
        return transacoesConta;
    }

    public ArrayList<Transacao> getHistorico(Categoria categoria){
        ArrayList<Transacao> transacoesCategoria = new ArrayList<>();
        for (Transacao transacao : transacoes){
            if (transacao.getCategoria().equals(categoria)){
                transacoesCategoria.add(transacao);
            }
        }
        return transacoesCategoria;
    }

    public ArrayList<Transacao> getHistorico(ArrayList<Tag> tags){
        ArrayList<Transacao> transacoesTags = new ArrayList<>();
        for (Transacao transacao : transacoes){
            if (transacao.getTags().size() == tags.size() && tags.containsAll(transacao.getTags())){
                transacoesTags.add(transacao);
            }
        }
        return transacoesTags;
    }

    public ArrayList<Transacao> getHistorico(Tag tag){
        ArrayList<Transacao> transacoesTag = new ArrayList<>();
        for (Transacao transacao : transacoes){
            for (Tag tagTransacao : transacao.getTags()){
                if (tagTransacao.equals(tag)){
                    transacoesTag.add(transacao);
                }
            }
        }
        return transacoesTag;
    }

    void adicionarTransacao(Transacao transacao, Conta conta){
        transacoes.add(transacao);
    }
}
