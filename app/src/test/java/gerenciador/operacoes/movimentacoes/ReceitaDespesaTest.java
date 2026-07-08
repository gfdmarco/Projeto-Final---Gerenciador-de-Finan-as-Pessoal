package gerenciador.operacoes.movimentacoes;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import gerenciador.base.Usuario;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;

class ReceitaDespesaTest {

    @Test
    void receitaDeveCreditarConta() {
        Conta conta = new Conta("Banco", "1", 10.0);
        Categoria categoria = new Categoria("Salario", 0.0);
        Receita receita = new Receita("Salario", "r1", 20.0, new ArrayList<>(), categoria, LocalDate.now(), conta);

        receita.realizarTransacao();

        assertEquals(30.0, conta.getMontante(), 0.0001);
    }

    @Test
    void despesaDeveDebitarConta() {
        Usuario u = new Usuario();
        u.abrirConta("Banco", "1", 100);
        Categoria categoria = new Categoria("Mercado", 0.0);
        Despesa despesa = new Despesa("Mercado", "d1", 40.0, new ArrayList<>(), categoria, LocalDate.now(), u.getContas().get(0));

        despesa.realizarTransacao();

        assertEquals(60.0, u.getContas().get(0).getMontante(), 0.0001);
    }
}
