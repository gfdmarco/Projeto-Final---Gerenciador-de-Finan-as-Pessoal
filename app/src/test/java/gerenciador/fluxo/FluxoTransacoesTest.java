package gerenciador.fluxo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import gerenciador.base.Usuario;
import gerenciador.enums.Frequencia;
import gerenciador.operacoes.movimentacoes.Despesa;
import gerenciador.operacoes.movimentacoes.DespesaRecorrente;
import gerenciador.operacoes.movimentacoes.Receita;
import gerenciador.operacoes.movimentacoes.ReceitaRecorrente;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;
import gerenciador.suporte.Tag;

// Cobre o cenario: Abrir conta, adicionar transacao (despesa e receita, avulsa e recorrente), remover, buscar
class FluxoTransacoesTest {

    @Test
    void abrirContaAdicionarDespesaEReceitaAvulsasNaoCrasha() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 1000.0);

        Categoria catMercado = new Categoria("Mercado", 0.0);
        Despesa despesa = new Despesa("Compras", UUID.randomUUID().toString(), 150.0, new ArrayList<>(), catMercado, LocalDate.now(), conta);
        assertDoesNotThrow(() -> u.adicionarTransacao(despesa));

        Categoria catSalario = new Categoria("Salario", 0.0);
        Receita receita = new Receita("Bonus", UUID.randomUUID().toString(), 300.0, new ArrayList<>(), catSalario, LocalDate.now(), conta);
        assertDoesNotThrow(() -> u.adicionarTransacao(receita));

        assertEquals(1000.0 - 150.0 + 300.0, conta.getMontante(), 0.0001);
        assertEquals(2, u.getHistorico().size());
    }

    @Test
    void adicionarDespesaRecorrenteNaoCrasha() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 1000.0);
        Categoria cat = new Categoria("Assinaturas", 0.0);

        DespesaRecorrente despesaRec = new DespesaRecorrente("Streaming", UUID.randomUUID().toString(), 40.0,
            new ArrayList<>(), cat, LocalDate.now(), conta, Frequencia.MENSAL, LocalDate.now());

        assertDoesNotThrow(() -> u.adicionarTransacao(despesaRec));
        assertEquals(960.0, conta.getMontante(), 0.0001);
    }

    @Test
    void adicionarReceitaRecorrenteNaoCrasha() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 0.0);
        Categoria cat = new Categoria("Freela", 0.0);

        ReceitaRecorrente receitaRec = new ReceitaRecorrente("Freela mensal", UUID.randomUUID().toString(), 500.0,
            new ArrayList<>(), cat, LocalDate.now(), conta, Frequencia.MENSAL, LocalDate.now());

        assertDoesNotThrow(() -> u.adicionarTransacao(receitaRec));
        assertEquals(500.0, conta.getMontante(), 0.0001);
    }

    @Test
    void removerTransacaoAvulsaReverteSaldoSemCrash() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 1000.0);
        Categoria cat = new Categoria("Mercado", 0.0);
        String id = UUID.randomUUID().toString();
        Despesa despesa = new Despesa("Compras", id, 150.0, new ArrayList<>(), cat, LocalDate.now(), conta);
        u.adicionarTransacao(despesa);

        assertDoesNotThrow(() -> u.removerTransacao(id));
        assertEquals(1000.0, conta.getMontante(), 0.0001);
        assertTrue(u.getHistorico().isEmpty());
    }

    @Test
    void buscarPorCategoriaContaTagValorEDataNaoCrasha() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 1000.0);
        Categoria cat = new Categoria("Mercado", 0.0);
        Tag tag = new Tag("essencial");
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag);
        Despesa despesa = new Despesa("Compras", UUID.randomUUID().toString(), 150.0, tags, cat, LocalDate.now(), conta);
        u.adicionarTransacao(despesa);

        assertEquals(1, u.buscarTransacao(cat).size());
        assertEquals(1, u.buscarTransacao(conta).size());
        assertEquals(1, u.buscarTransacao(tag).size());
        assertEquals(1, u.buscarTransacao(150.0).size());
        assertEquals(1, u.buscarTransacao(LocalDate.now()).size());
    }

    @Test
    void buscarSemNenhumCriterioListaTudoSemCrash() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        assertTrue(u.getHistorico().isEmpty());
        // reproduz o fallback de BuscarTransacaoController.onBuscar() quando nenhum campo é preenchido
        assertDoesNotThrow(u::getHistorico);
    }
}
