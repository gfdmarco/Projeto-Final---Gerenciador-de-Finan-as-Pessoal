package gerenciador.suporte;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import gerenciador.operacoes.movimentacoes.Despesa;
import gerenciador.operacoes.movimentacoes.Transacao;

class CategoriaTest {

    @Test
    void gastoNoMesDeveSomarDespesasAssociadas() {
        Categoria categoria = new Categoria("Alimentacao", 100.0);
        Conta conta = new Conta("Banco", "1", 1000.0);
        Transacao despesa = new Despesa("Mercado", "d1", 30.0, new ArrayList<>(), categoria, LocalDate.now(), conta);
        categoria.adicionarTransacao(despesa);

        assertEquals(30.0, categoria.gastoNoMes(), 0.0001);
    }
}
