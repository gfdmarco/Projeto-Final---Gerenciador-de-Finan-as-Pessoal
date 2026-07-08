package gerenciador.fluxo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import gerenciador.base.Usuario;
import gerenciador.exceptions.OrcamentoExcedidoException;
import gerenciador.exceptions.SaldoInsuficienteException;
import gerenciador.operacoes.movimentacoes.Despesa;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;

// Cobre o cenario: Estourar orcamento de proposito (deve pedir confirmacao) e estourar saldo de
// proposito (deve dar erro, nao crash)
class FluxoOrcamentoSaldoTest {

    @Test
    void despesaQueEstouraOrcamentoLancaExcecaoTratavel() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 1000.0);
        Categoria cat = new Categoria("Lazer", 100.0); // orcamento de R$100

        Despesa despesa = new Despesa("Cinema", UUID.randomUUID().toString(), 150.0, new ArrayList<>(), cat, LocalDate.now(), conta);

        // é isto que dispara o Alert.CONFIRMATION em AdicionarTransacaoController.confirmarOrcamentoExcedido
        assertThrows(OrcamentoExcedidoException.class, () -> u.adicionarTransacao(despesa));
        // a transacao rejeitada nao pode ter efeito colateral
        assertEquals(1000.0, conta.getMontante(), 0.0001);
        assertTrue(u.getHistorico().isEmpty());
    }

    @Test
    void confirmarMesmoAssimIgnoraOrcamentoEDebitaNormalmente() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 1000.0);
        Categoria cat = new Categoria("Lazer", 100.0);

        Despesa despesa = new Despesa("Cinema", UUID.randomUUID().toString(), 150.0, new ArrayList<>(), cat, LocalDate.now(), conta);
        assertThrows(OrcamentoExcedidoException.class, () -> u.adicionarTransacao(despesa));

        // simula o usuario respondendo "Sim" no Alert de confirmacao
        assertDoesNotThrow(() -> u.adicionarTransacao(despesa, true));
        assertEquals(850.0, conta.getMontante(), 0.0001);
    }

    @Test
    void despesaQueEstouraSaldoLancaErroTratavelNaoCrash() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 50.0);
        Categoria cat = new Categoria("Mercado", 0.0); // sem orcamento definido

        Despesa despesa = new Despesa("Compra grande", UUID.randomUUID().toString(), 200.0, new ArrayList<>(), cat, LocalDate.now(), conta);

        assertThrows(SaldoInsuficienteException.class, () -> u.adicionarTransacao(despesa));
        assertEquals(50.0, conta.getMontante(), 0.0001, "saldo nao deve ter sido alterado numa transacao rejeitada");
        assertTrue(u.getHistorico().isEmpty());
    }
}
