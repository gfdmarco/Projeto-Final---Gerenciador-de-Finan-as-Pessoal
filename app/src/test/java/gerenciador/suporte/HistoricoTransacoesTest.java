package gerenciador.suporte;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gerenciador.base.Usuario;
import gerenciador.operacoes.movimentacoes.Despesa;
import gerenciador.operacoes.movimentacoes.Transacao;

class HistoricoTransacoesTest {

    private HistoricoTransacoes historico;
    private Conta contaPrincipal;
    private Conta contaSecundaria;
    private Categoria categoriaAlimentacao;
    private Categoria categoriaLazer;
    private Tag tagEssencial;
    private Tag tagFimDeSemana;

    @BeforeEach
    void setUp() {
        // Inicializa o histórico zerado antes de cada teste
        historico = new HistoricoTransacoes();
        
        // Criando instâncias base para associar às transações
        Usuario usuario = new Usuario("Testador", "teste", "senha123");
        contaPrincipal = usuario.abrirConta("Principal", "1", 1000.0);
        contaSecundaria = usuario.abrirConta("Secundaria", "2", 500.0);
        
        categoriaAlimentacao = new Categoria("Alimentação", 500.0);
        categoriaLazer = new Categoria("Lazer", 200.0);
        
        tagEssencial = new Tag("Essencial");
        tagFimDeSemana = new Tag("Fim de Semana");
    }

    @Test
    void adicionarEGetHistoricoGeralDeveRetornarTodasAsTransacoes() {
        Transacao t1 = new Despesa("Compra 1", "d1", 50.0, new ArrayList<>(), categoriaAlimentacao, LocalDate.now(), contaPrincipal);
        Transacao t2 = new Despesa("Compra 2", "d2", 30.0, new ArrayList<>(), categoriaLazer, LocalDate.now(), contaSecundaria);
        
        historico.adicionarTransacao(t1);
        historico.adicionarTransacao(t2);
        
        ArrayList<Transacao> todasTransacoes = historico.getHistorico();
        
        assertEquals(2, todasTransacoes.size());
        assertTrue(todasTransacoes.contains(t1));
        assertTrue(todasTransacoes.contains(t2));
    }

    @Test
    void getHistoricoPorPeriodoDeveFiltrarCorretamente() {
        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);
        LocalDate amanha = hoje.plusDays(1);
        LocalDate semanaQueVem = hoje.plusDays(7);

        // Transações em datas diferentes
        Transacao tOntem = new Despesa("Ontem", "d1", 10.0, new ArrayList<>(), categoriaAlimentacao, ontem, contaPrincipal);
        Transacao tHoje = new Despesa("Hoje", "d2", 20.0, new ArrayList<>(), categoriaAlimentacao, hoje, contaPrincipal);
        Transacao tSemanaQueVem = new Despesa("Futuro", "d3", 30.0, new ArrayList<>(), categoriaAlimentacao, semanaQueVem, contaPrincipal);

        historico.adicionarTransacao(tOntem);
        historico.adicionarTransacao(tHoje);
        historico.adicionarTransacao(tSemanaQueVem);

        // Buscando apenas transações entre "ontem" e "amanhã"
        ArrayList<Transacao> filtradas = historico.getHistorico(ontem, amanha);

        // Deve conter as de ontem e hoje, e ignorar a da semana que vem
        assertEquals(2, filtradas.size());
        assertTrue(filtradas.contains(tOntem));
        assertTrue(filtradas.contains(tHoje));
        assertFalse(filtradas.contains(tSemanaQueVem));
    }

    @Test
    void getHistoricoPorContaDeveRetornarApenasTransacoesDaConta() {
        Transacao tPrincipal = new Despesa("Conta 1", "d1", 100.0, new ArrayList<>(), categoriaAlimentacao, LocalDate.now(), contaPrincipal);
        Transacao tSecundaria = new Despesa("Conta 2", "d2", 50.0, new ArrayList<>(), categoriaLazer, LocalDate.now(), contaSecundaria);
        
        historico.adicionarTransacao(tPrincipal);
        historico.adicionarTransacao(tSecundaria);
        
        ArrayList<Transacao> filtradas = historico.getHistorico(contaPrincipal);
        
        assertEquals(1, filtradas.size());
        assertTrue(filtradas.contains(tPrincipal));
    }

    @Test
    void getHistoricoPorCategoriaDeveRetornarApenasDaCategoria() {
        Transacao tAlimentacao = new Despesa("Mercado", "d1", 150.0, new ArrayList<>(), categoriaAlimentacao, LocalDate.now(), contaPrincipal);
        Transacao tLazer = new Despesa("Cinema", "d2", 40.0, new ArrayList<>(), categoriaLazer, LocalDate.now(), contaPrincipal);
        
        historico.adicionarTransacao(tAlimentacao);
        historico.adicionarTransacao(tLazer);
        
        ArrayList<Transacao> filtradas = historico.getHistorico(categoriaAlimentacao);
        
        assertEquals(1, filtradas.size());
        assertTrue(filtradas.contains(tAlimentacao));
    }

    @Test
    void getHistoricoPorTagUnicaDeveEncontrarTransacaoComATag() {
        ArrayList<Tag> tagsCompra1 = new ArrayList<>();
        tagsCompra1.add(tagEssencial);
        
        ArrayList<Tag> tagsCompra2 = new ArrayList<>();
        tagsCompra2.add(tagFimDeSemana);
        
        Transacao tEssencial = new Despesa("Farmácia", "d1", 50.0, tagsCompra1, categoriaAlimentacao, LocalDate.now(), contaPrincipal);
        Transacao tFimDeSemana = new Despesa("Bar", "d2", 80.0, tagsCompra2, categoriaLazer, LocalDate.now(), contaPrincipal);
        
        historico.adicionarTransacao(tEssencial);
        historico.adicionarTransacao(tFimDeSemana);
        
        ArrayList<Transacao> filtradas = historico.getHistorico(tagEssencial);
        
        assertEquals(1, filtradas.size());
        assertTrue(filtradas.contains(tEssencial));
    }

    @Test
    void getHistoricoPorListaDeTagsDeveExigirTamanhoETagsIguais() {
        ArrayList<Tag> duasTags = new ArrayList<>();
        duasTags.add(tagEssencial);
        duasTags.add(tagFimDeSemana);
        
        ArrayList<Tag> umaTag = new ArrayList<>();
        umaTag.add(tagEssencial);
        
        Transacao tDuasTags = new Despesa("Feira Sábado", "d1", 60.0, duasTags, categoriaAlimentacao, LocalDate.now(), contaPrincipal);
        Transacao tUmaTag = new Despesa("Mercado Terça", "d2", 100.0, umaTag, categoriaAlimentacao, LocalDate.now(), contaPrincipal);
        
        historico.adicionarTransacao(tDuasTags);
        historico.adicionarTransacao(tUmaTag);
        ArrayList<Transacao> filtradas = historico.getHistorico(duasTags);
        
        assertEquals(1, filtradas.size());
        assertTrue(filtradas.contains(tDuasTags));
        assertFalse(filtradas.contains(tUmaTag));
    }
}