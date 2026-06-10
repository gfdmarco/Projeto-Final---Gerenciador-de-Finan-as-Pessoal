package gerenciador.interfaces;

import java.time.LocalDate;
import gerenciador.operacoes.movimentacoes.Transacao;
import gerenciador.enums.Frequencia;;

public interface Recorrencia {
    LocalDate getDataInicio();
    Transacao gerarTransacaoRecorrente(LocalDate data);
    boolean isAtivo();
    Frequencia getRecorrencia();
}
