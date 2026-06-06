package gerenciador.interfaces;

import java.time.LocalDate;
import gerenciador.operacoes.movimentacoes.Transacao;

public interface Recorrencia {
    String getRecorrencia();
    LocalDate getDataInicio();
    Transacao gerarRecorrencia(LocalDate data);
    boolean isAtivo();
}
