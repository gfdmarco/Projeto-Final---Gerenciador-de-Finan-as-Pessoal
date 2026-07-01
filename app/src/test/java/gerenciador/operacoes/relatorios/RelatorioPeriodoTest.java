package gerenciador.operacoes.relatorios;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import gerenciador.base.Usuario;
import gerenciador.operacoes.movimentacoes.Receita;
import gerenciador.operacoes.movimentacoes.Transacao;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;

class RelatorioPeriodoTest {

    @Test
    void gerarDeveCalcularSaldoDoPeriodo() {
        Usuario usuario = new Usuario("Ana", "ana", "senha");
        Conta conta = usuario.abrirConta("Banco", "1", 0.0);
        Categoria categoria = new Categoria("Salario", 0.0);
        Transacao receita = new Receita("Salario", "r1", 100.0, new ArrayList<>(), categoria, LocalDate.now(), conta);
        usuario.adicionarTransacao(receita);

        RelatorioPeriodo relatorio = new RelatorioPeriodo(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        String texto = relatorio.gerar(usuario.getHistorico(), usuario);

        assertTrue(texto.contains("Receitas"));
        assertTrue(texto.contains("Evolução de Saldo"));
    }
}
