package gerenciador.base;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import gerenciador.operacoes.movimentacoes.Despesa;
import gerenciador.operacoes.movimentacoes.Transacao;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;
import gerenciador.suporte.Tag;

class UsuarioTest {

    @Test
    void removerTransacaoDeveReverterSaldoERemoverDoHistorico() {
        Usuario usuario = new Usuario("Ana", "ana", "senha");
        Conta conta = usuario.abrirConta("Banco", "c1", 100.0);
        Categoria categoria = new Categoria("Alimentacao", 50.0);
        Tag tag = new Tag("Mercado");
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag);
        Transacao despesa = new Despesa("Mercado", "d1", 30.0, tags, categoria, LocalDate.now(), conta);

        usuario.adicionarTransacao(despesa);
        usuario.removerTransacao("d1");

        assertEquals(100.0, conta.getMontante(), 0.0001);
        assertTrue(usuario.getHistorico().isEmpty());
        assertTrue(conta.getTransacoesAssociadas().isEmpty());
        assertTrue(categoria.getTransacoesAssociadas().isEmpty());
        assertTrue(tag.getTransacoesAssociadas().isEmpty());
    }

    @Test
    void transferirEntreContasDeveAtualizarSaldos() {
        Usuario usuario = new Usuario("Ana", "ana", "senha");
        Conta origem = usuario.abrirConta("Banco", "c1", 100.0);
        Conta destino = usuario.abrirConta("Banco", "c2", 50.0);

        usuario.transferirEntreContas(25.0, origem, destino);

        assertEquals(75.0, origem.getMontante(), 0.0001);
        assertEquals(75.0, destino.getMontante(), 0.0001);
    }
}
