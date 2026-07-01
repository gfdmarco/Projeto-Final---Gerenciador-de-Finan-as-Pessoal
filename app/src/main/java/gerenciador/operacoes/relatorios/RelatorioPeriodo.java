package gerenciador.operacoes.relatorios;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import gerenciador.base.Usuario;
import gerenciador.enums.Frequencia;
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
        long diferencaDias = ChronoUnit.DAYS.between(this.inicioPeriodo, this.fimPeriodo);
        double receitas = 0.0;
        double despesas = 0.0;
        for (Transacao transacaoP : transacoesPeriodo){
            if (transacaoP instanceof DespesaRecorrente){
                DespesaRecorrente despesa = (DespesaRecorrente) transacaoP;
                switch(despesa.getRecorrencia()){
                    case SEMANAL:
                        despesas += despesa.getValor() * (diferencaDias / 7.0);
                        break;
                    case QUINZENAL:
                        despesas += despesa.getValor() * (diferencaDias / 15.0);
                        break;
                    case MENSAL:
                        despesas += despesa.getValor() * (diferencaDias / 30.0);
                        break;
                    case TRIMESTRAL:
                        despesas += despesa.getValor() * (diferencaDias / 90.0);
                        break;
                    case SEMESTRAL:
                        despesas += despesa.getValor() * (diferencaDias / 180.0);
                        break;
                    case ANUAL:
                        despesas += despesa.getValor() * (diferencaDias / 360.0);
                        break;
                }
            }
            else if (transacaoP instanceof ReceitaRecorrente){
                ReceitaRecorrente receita = (ReceitaRecorrente) transacaoP;
                switch(receita.getRecorrencia()){
                    case SEMANAL:
                        receitas += receita.getValor() * (diferencaDias / 7.0);
                        break;
                    case QUINZENAL:
                        receitas += receita.getValor() * (diferencaDias / 15.0);
                        break;
                    case MENSAL:
                        receitas += receita.getValor() * (diferencaDias / 30.0);
                        break;
                    case TRIMESTRAL:
                        receitas += receita.getValor() * (diferencaDias / 90.0);
                        break;
                    case SEMESTRAL:
                        receitas += receita.getValor() * (diferencaDias / 180.0);
                        break;
                    case ANUAL:
                        receitas += receita.getValor() * (diferencaDias / 360.0);
                        break;
                }
            }
            else if (transacaoP instanceof Despesa){
                despesas += transacaoP.getValor();
            }
            else if (transacaoP instanceof Receita){
                receitas += transacaoP.getValor();
            }
        }
        //ERRO DE NULL NA EXIBICAO TALVEZ
        double circulacao = receitas - despesas;
        String conteudoExibir =
                "Período: " + inicioPeriodo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " até " 
                + fimPeriodo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "Receitas: R$ " + receitas + "\n"
                + "Despesas: R$ " + despesas + "\n"
                + "Evolução de Saldo: R$ " + circulacao + "\n \n";
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
