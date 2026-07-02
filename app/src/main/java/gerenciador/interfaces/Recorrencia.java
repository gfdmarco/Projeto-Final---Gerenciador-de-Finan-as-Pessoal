package gerenciador.interfaces;

import java.time.LocalDate;
import gerenciador.operacoes.movimentacoes.Transacao;
import gerenciador.enums.Frequencia;

// Recorrencia.java
public interface Recorrencia {
    LocalDate getDataInicio();
    Transacao gerarTransacaoRecorrente(LocalDate data);
    boolean isAtivo();
    Frequencia getRecorrencia();
    LocalDate getUltimaDataAplicada();

    void setUltimaDataAplicada(LocalDate data);

    // qualquer implementador herda gratuitamente com o "default" ali
    default LocalDate calcularProximaData(LocalDate base, Frequencia frequencia) {
        return switch (frequencia) {
            case SEMANAL    -> base.plusWeeks(1);
            case QUINZENAL  -> base.plusDays(15);
            case MENSAL     -> base.plusMonths(1);
            case TRIMESTRAL -> base.plusMonths(3);
            case SEMESTRAL  -> base.plusMonths(6);
            case ANUAL      -> base.plusYears(1);
        };
    }
}