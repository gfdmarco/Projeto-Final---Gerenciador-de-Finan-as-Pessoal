package gerenciador.interfaces;

import java.util.ArrayList;

import gerenciador.base.Usuario;
import gerenciador.operacoes.movimentacoes.Transacao;

public interface Relatorio {
    String gerar(ArrayList<Transacao> transacoes, Usuario usuario);
    void exportar(String caminhoArq, Usuario usuario);
}
