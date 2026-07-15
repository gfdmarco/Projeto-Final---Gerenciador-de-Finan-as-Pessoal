package gerenciador.operacoes.relatorios;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import gerenciador.base.Usuario;
import gerenciador.operacoes.movimentacoes.Despesa;
import gerenciador.operacoes.movimentacoes.Receita;
import gerenciador.operacoes.movimentacoes.Transacao;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;

class RelatorioCategoriaTest {

    @Test
    void gerarDeveCalcularSaldoEFiltrarPorCategoria() {
        Usuario usuario = new Usuario("Lucas", "lucas", "senha123");
        Conta conta = usuario.abrirConta("Banco Principal", "1", 1000.0);
        Categoria categoriaAlimentacao = new Categoria("Alimentação", 500.0);
        Categoria categoriaLazer = new Categoria("Lazer", 200.0);
        Transacao receitaAlimentacao = new Receita("Vale Refeição", "r1", 600.0, new ArrayList<>(), categoriaAlimentacao, LocalDate.now(), conta);
        Transacao despesaAlimentacao = new Despesa("Supermercado", "d1", 150.0, new ArrayList<>(), categoriaAlimentacao, LocalDate.now(), conta);
        Transacao despesaLazer = new Despesa("Cinema", "d2", 50.0, new ArrayList<>(), categoriaLazer, LocalDate.now(), conta);

        usuario.adicionarTransacao(receitaAlimentacao);
        usuario.adicionarTransacao(despesaAlimentacao);
        usuario.adicionarTransacao(despesaLazer);

        RelatorioCategoria relatorio = new RelatorioCategoria(categoriaAlimentacao);
        String texto = relatorio.gerar(usuario.getHistorico(), usuario);

        assertTrue(texto.contains("Categoria: Alimentação"));
        assertTrue(texto.contains("Orçamento previsto: R$ 500.0"));
        assertTrue(texto.contains("Receitas: R$ 600.0"));
        assertTrue(texto.contains("Despesas: R$ 150.0")); 
        assertTrue(texto.contains("Evolução de Saldo: R$ 450.0")); // 600 - 150
        assertTrue(texto.contains("30.0% atingido")); // (150 de despesa / 500 de orçamento) * 100
    }

@Test
    void gerarDeveExibirSemOrcamentoDefinidoQuandoZerado() {
        Usuario usuario = new Usuario("Lucas", "lucas", "senha123");
        Conta conta = usuario.abrirConta("Banco", "1", 500.0); 
        
        Categoria categoriaSemOrcamento = new Categoria("Extra", 0.0);
        Transacao despesa = new Despesa("Compra", "d1", 100.0, new ArrayList<>(), categoriaSemOrcamento, LocalDate.now(), conta);
        usuario.adicionarTransacao(despesa);

        RelatorioCategoria relatorio = new RelatorioCategoria(categoriaSemOrcamento);
        
        String texto = relatorio.gerar(usuario.getHistorico(), usuario);

        System.out.println("=== TEXTO GERADO PELO RELATÓRIO ===");
        System.out.println(texto);

        assertTrue(texto.contains("Orçamento previsto: R$ 0.0"), 
            "Falha: O texto não contém 'Orçamento previsto: R$ 0.0'.");
            
        assertTrue(texto.contains("Sem orçamento definido"), 
            "Falha: O texto não contém 'Sem orçamento definido'.");
    }

}