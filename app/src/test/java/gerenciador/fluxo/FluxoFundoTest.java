package gerenciador.fluxo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import gerenciador.base.Usuario;
import gerenciador.enums.TipoFundo;
import gerenciador.exceptions.SaldoInsuficienteException;
import gerenciador.operacoes.reservas.Fundo;
import gerenciador.suporte.Conta;

// Cobre o cenario: Criar fundo, depositar, sacar mais do que tem (deve dar erro, nao crash)
class FluxoFundoTest {

    @Test
    void criarFundoEDepositarNaoCrasham() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 1000.0);

        Fundo fundo = u.criarFundo("Reserva", TipoFundo.EMERGENCIA, 5000.0, 0.5, LocalDate.now(), conta);
        assertEquals(0.0, fundo.getSaldo(), 0.0001);

        assertDoesNotThrow(() -> fundo.depositar(300.0));
        assertEquals(300.0, fundo.getSaldo(), 0.0001);
    }

    @Test
    void sacarMaisDoQueTemLancaErroTratavelNaoCrash() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 1000.0);
        Fundo fundo = u.criarFundo("Reserva", TipoFundo.EMERGENCIA, 5000.0, 0.5, LocalDate.now(), conta);
        fundo.depositar(100.0);

        assertThrows(SaldoInsuficienteException.class, () -> fundo.sacar(500.0));
        assertEquals(100.0, fundo.getSaldo(), 0.0001, "saque rejeitado nao deve alterar o saldo do fundo");
    }

    @Test
    void depositarOuSacarValorInvalidoNaoCrashaComExcecaoNaoTratada() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 1000.0);
        Fundo fundo = u.criarFundo("Reserva", TipoFundo.EMERGENCIA, 5000.0, 0.5, LocalDate.now(), conta);

        // FundosController.onRealizarOperacao ja bloqueia valor <= 0 antes de chamar Fundo,
        // mas a propria classe Fundo tambem rejeita (com NumberFormatException) como segunda camada de defesa
        assertThrows(NumberFormatException.class, () -> fundo.depositar(-50.0));
        assertThrows(NumberFormatException.class, () -> fundo.sacar(-50.0));
    }

    @Test
    void fundoDeInvestimentoTambemFuncionaSemCrash() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 1000.0);

        Fundo fundo = u.criarFundo("Acoes", TipoFundo.INVESTIMENTO, 10000.0, 1.2, LocalDate.now(), conta);
        assertDoesNotThrow(() -> fundo.depositar(1000.0));
        assertEquals(0.1, fundo.getProgresso(), 0.0001);
    }
}
