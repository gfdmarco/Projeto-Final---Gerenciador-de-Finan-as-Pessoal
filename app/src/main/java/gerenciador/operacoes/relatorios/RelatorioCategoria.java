package gerenciador.operacoes.relatorios;

import gerenciador.base.Usuario;
import gerenciador.interfaces.Relatorio;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import gerenciador.operacoes.movimentacoes.*;
import gerenciador.suporte.Categoria;

public class RelatorioCategoria implements Relatorio{
    private Categoria categoria;

    public RelatorioCategoria(Categoria categoria){
        this.categoria = categoria;
    }

    @Override
    public String gerar(ArrayList<Transacao> transacoes, Usuario usuario){
        ArrayList<Transacao> transacoesCategoria = new ArrayList<>();
        for (Transacao transacao : transacoes){
            if (transacao.getCategoria().equals(this.categoria)){
                transacoesCategoria.add(transacao);
            }
        }

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

        double circulacao = receitas - despesas;
        String statusOrcamento = this.categoria.getOrcamento() == 0
                ? "Sem orçamento definido"
                : (100 * (despesas / this.categoria.getOrcamento())) + "% atingido";

        String conteudoExibir =
                "Categoria: " + this.categoria.getNome() + "\n"
                + "Orçamento previsto: R$ " + this.categoria.getOrcamento() + "\n"
                + "Receitas: R$ " + receitas + "\n"
                + "Despesas: R$ " + despesas + "\n"
                + "Evolução de Saldo: R$ " + circulacao + "\n"
                + "Status orçamento: " + statusOrcamento + "\n \n";
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
