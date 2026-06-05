package gerenciador.interfaces;

import java.time.LocalDate;
import gerenciador.operacoes.*;

public interface Recorrencia {
    String getRecorrencia();
    LocalDate getDataInicio();
    Transacao gerarRecorrencia(LocalDate data);
    boolean isAtivo();
}
