package gerenciador.operacoes.relatorios;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import gerenciador.base.Usuario;
import gerenciador.interfaces.Relatorio;
import gerenciador.operacoes.movimentacoes.*;
import java.util.ArrayList;

public class RelatorioPeriodo implements Relatorio{
    private LocalDate inicioPeriodo;
    private LocalDate fimPeriodo;
    
    public RelatorioPeriodo(LocalDate inicioPeriodo, LocalDate fimPeriodo){
        this.inicioPeriodo = inicioPeriodo;
        this.fimPeriodo = fimPeriodo;
    }

    @Override
    public String gerar(ArrayList<Transacao> transacoes, Usuario usuario){
        //1a parte: transações realizadas\
        ArrayList<Transacao> transacoesPeriodo = new ArrayList<>();
        for (Transacao transacao : transacoes){
            boolean depoisOuNoInicio = !transacao.getData().isBefore(inicioPeriodo);
            boolean antesOuNoFim = !transacao.getData().isAfter(fimPeriodo);
            if (depoisOuNoInicio && antesOuNoFim){
                transacoesPeriodo.add(transacao);
            }
        }

        //2a parte: construção da evolução do saldo
        double receitas = 0.0;
        double despesas = 0.0;
        for (Transacao transacaoP : transacoesPeriodo){
            if (transacaoP instanceof Despesa){
                despesas += transacaoP.getValor();
            }
            else if (transacaoP instanceof Receita){
                receitas += transacaoP.getValor();
            }
        }
        double circulacao = receitas - despesas;
        String conteudoExibir =
                "Período: " + inicioPeriodo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " até " 
                + fimPeriodo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "Receitas: R$ " + receitas + "\n"
                + "Despesas: R$ " + despesas + "\n"
                + "Evolução de Saldo: R$ " + circulacao + "\n"
                + "Projeção de Saldo: \n"
                + "Daqui a 1 mes: R$ " + usuario.projetarSaldoFuturo(1) + "\n"
                + "Daqui a 3 meses: R$ " + usuario.projetarSaldoFuturo(3) + "\n"
                + "Daqui a 6 meses: R$ " + usuario.projetarSaldoFuturo(6) + "\n"
                + "Daqui a 12 meses: R$ " + usuario.projetarSaldoFuturo(12) + "\n \n";
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
