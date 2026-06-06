package gerenciador.interfaces;

import java.util.ArrayList;
import gerenciador.operacoes.movimentacoes.Transacao;

public interface Relatorio {
    String gerar(ArrayList<Transacao> transacoes);
}
