package gerenciador.interfaces;

import java.time.LocalDate;
import gerenciador.operacoes.movimentacoes.Transacao;
import gerenciador.enums.*;

public interface Recorrencia {
    Frequencia getRecorrencia();
    LocalDate getDataInicio();
    Transacao gerarRecorrencia(LocalDate data);
    boolean isAtivo();
}
