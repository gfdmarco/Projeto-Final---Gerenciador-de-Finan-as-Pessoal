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
import java.util.Comparator;

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
            if (transacao.getData().isAfter(inicioPeriodo) && transacao.getData().isBefore(fimPeriodo)){
                transacoesPeriodo.add(transacao);
            }
        }
        //2a parte: as 3 maiores despesas
        transacoesPeriodo.sort(Comparator.comparing(Transacao::getValor));

        int tamanho_lista = transacoesPeriodo.size();

        int qtd_disponivel = Math.min(3, tamanho_lista);
        //se tiver menos que 3 transacoes ele vai pegar o maximo disponivel
        Transacao primeira = null;
        Transacao segunda = null;
        Transacao terceira = null;
        if (qtd_disponivel >= 1){
            primeira = transacoesPeriodo.get(transacoesPeriodo.size() - 1);
        }
        if (qtd_disponivel >= 2){
            segunda = transacoesPeriodo.get(transacoesPeriodo.size() - 2);
        }
        if (qtd_disponivel >= 3){
            terceira = transacoesPeriodo.get(transacoesPeriodo.size() - 3);
        }

        //transacoes que nao foram preenchidas ficam como null

        //3a parte: construção da evolução do saldo
        long diferencaDias = ChronoUnit.DAYS.between(this.inicioPeriodo, this.fimPeriodo);
        double receitas = 0.0;
        double despesas = 0.0;
        for (Transacao transacaoP : transacoesPeriodo){
            if (transacaoP instanceof DespesaRecorrente){
                DespesaRecorrente despesa = (DespesaRecorrente) transacaoP;
                Frequencia freq = despesa.getRecorrencia();
                switch(freq){
                    case SEMANAL:
                        despesas += despesa.getValor() * (diferencaDias / 7);
                        break;
                    case QUINZENAL:
                        despesas += despesa.getValor() * (diferencaDias / 15);
                        break;
                    case MENSAL:
                        despesas += despesa.getValor() * (diferencaDias / 30);
                        break;
                    case TRIMESTRAL:
                        despesas += despesa.getValor() * (diferencaDias / 90);
                        break;
                    case SEMESTRAL:
                        despesas += despesa.getValor() * (diferencaDias / 180);
                        break;
                    case ANUAL:
                        despesas += despesa.getValor() * (diferencaDias / 360);
                        break;
                }
            }
            else if (transacaoP instanceof ReceitaRecorrente){
                ReceitaRecorrente receita = (ReceitaRecorrente) transacaoP;
                Frequencia freq = receita.getRecorrencia();
                switch(freq){
                    case SEMANAL:
                        receitas += receita.getValor() * (diferencaDias / 7);
                        break;
                    case QUINZENAL:
                        receitas += receita.getValor() * (diferencaDias / 15);
                        break;
                    case MENSAL:
                        receitas += receita.getValor() * (diferencaDias / 30);
                        break;
                    case TRIMESTRAL:
                        receitas += receita.getValor() * (diferencaDias / 90);
                        break;
                    case SEMESTRAL:
                        receitas += receita.getValor() * (diferencaDias / 180);
                        break;
                    case ANUAL:
                        receitas += receita.getValor() * (diferencaDias / 360);
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
                + "Evolução de Saldo: R$ " + circulacao + "\n \n"
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
