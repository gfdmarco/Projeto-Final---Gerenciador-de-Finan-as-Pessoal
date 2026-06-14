package gerenciador.operacoes.relatorios;

import gerenciador.base.Usuario;
import gerenciador.interfaces.Relatorio;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

import gerenciador.operacoes.Meta;
import gerenciador.operacoes.movimentacoes.*;
import gerenciador.suporte.Categoria;

public class RelatorioCategoria implements Relatorio{
    private Categoria categoria;

    public RelatorioCategoria(Categoria categoria){
        this.categoria = categoria;
    }

    @Override
    public String gerar(ArrayList<Transacao> transacoes, Usuario usuario){
        //1a parte: transações realizadas
        ArrayList<Transacao> transacoesCategoria = new ArrayList<>();
        for (Transacao transacao : transacoes){
            if (transacao.getCategoria().equals(this.categoria)){
                transacoesCategoria.add(transacao);
            }
        }
        //2a parte: as 3 maiores despesas
        transacoesCategoria.sort(Comparator.comparing(Transacao::getValor));

        int tamanho_lista = transacoesCategoria.size();

        int qtd_disponivel = Math.min(3, tamanho_lista);
        //se tiver menos que 3 transacoes ele vai pegar o maximo disponivel
        Transacao primeira = null;
        Transacao segunda = null;
        Transacao terceira = null;
        if (qtd_disponivel >= 1){
            primeira = transacoesCategoria.get(transacoesCategoria.size() - 1);
        }
        if (qtd_disponivel >= 2){
            segunda = transacoesCategoria.get(transacoesCategoria.size() - 2);
        }
        if (qtd_disponivel >= 3){
            terceira = transacoesCategoria.get(transacoesCategoria.size() - 3);
        }
        //transacoes que nao foram preenchidas ficam como null


        //3a parte: construção da evolução do saldo
        double receitas = 0.0;
        double despesas = 0.0;
        for (Transacao transacaoC : transacoesCategoria){
            if (transacaoC instanceof Despesa){
                despesas += transacaoC.getValor();
            }
            else if (transacaoC instanceof Receita){
                receitas += transacaoC.getValor();
            }
        }
        //4a parte: feedback de metas
        int qtdMetas = 0;
        int qtdAtingidas = 0;
        for (Meta meta : usuario.getMetas()){
            qtdMetas++;
            if (meta.isAtingida(usuario)){
                qtdAtingidas++;
            }
        }
        //ERRO DE NULL NA EXIBICAO TALVEZ
        double circulacao = receitas - despesas;
        String conteudoExibir = 
                "Categoria: " + this.categoria.getNome() + "\n"
                + "Orçamento previsto: R$ " + this.categoria.getOrcamento() + "\n"
                + "Receitas: R$ " + receitas + "\n"
                + "Despesas: R$ " + despesas + "\n"
                + "Evolução de Saldo: R$ " + circulacao + "\n"
                + "Status orçamento: " + 100 * (circulacao / this.categoria.getOrcamento()) + "atingido \n"
                + "Feedback de Metas: " + qtdAtingidas + "/" + qtdMetas + "foram atingidas \n \n"
                + "Três maiores movimentações: \n"
                + "1ª) Nome: " + primeira.getNome() + "\n" 
                + "Valor:" + primeira.getValor() + "\n"
                + "Data: " + primeira.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "Conta: " + primeira.getConta() + "\n"
                + "Categoria: " + primeira.getCategoria() + "\n \n"
                + "2ª) Nome: " + segunda.getNome() + "\n" 
                + "Valor:" + segunda.getValor() + "\n"
                + "Data: " + segunda.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "Conta: " + segunda.getConta() + "\n"
                + "Categoria: " + segunda.getCategoria() + "\n \n"
                + "3ª) Nome: " + terceira.getNome() + "\n" 
                + "Valor:" + terceira.getValor() + "\n"
                + "Data: " + terceira.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "Conta: " + terceira.getConta() + "\n"
                + "Categoria: " + terceira.getCategoria() + "\n \n";
        return conteudoExibir;
    }

    @Override
    public void exportar(String caminhoArq, Usuario usuario){
        String paraExportar = gerar(usuario.getHistorico(), usuario);
        try (FileWriter arquivo = new FileWriter(caminhoArq)){
            arquivo.write(paraExportar);
        }
        catch (IOException e){
            throw new RuntimeException("Erro ao exportar o relatório", e);
        }
    }
}

