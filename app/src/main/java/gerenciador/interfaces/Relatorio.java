package gerenciador.interfaces;

import java.util.ArrayList;
import gerenciador.operacoes.*;

public interface Relatorio {
    String gerar(ArrayList<Transacao> transacoes);
}
