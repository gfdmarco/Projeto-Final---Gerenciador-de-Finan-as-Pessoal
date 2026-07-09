package gerenciador.fluxo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gerenciador.base.Usuario;
import gerenciador.exceptions.SaldoInsuficienteException;
import gerenciador.suporte.Conta;

// Cobre o cenario: Transferir entre contas com saldo insuficiente de proposito
class FluxoTransferenciaTest {

    @Test
    void transferenciaComSaldoSuficienteFunciona() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta origem = u.abrirConta("Banco A", "1", 500.0);
        Conta destino = u.abrirConta("Banco B", "2", 100.0);

        assertDoesNotThrow(() -> u.transferirEntreContas(200.0, origem, destino));

        assertEquals(300.0, origem.getMontante(), 0.0001);
        assertEquals(300.0, destino.getMontante(), 0.0001);
    }

    @Test
    void transferenciaComSaldoInsuficienteLancaErroTratavelNaoCrash() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta origem = u.abrirConta("Banco A", "1", 50.0);
        Conta destino = u.abrirConta("Banco B", "2", 100.0);

        assertThrows(SaldoInsuficienteException.class, () -> u.transferirEntreContas(200.0, origem, destino));
        // nenhum saldo deve ter sido alterado numa transferencia rejeitada
        assertEquals(50.0, origem.getMontante(), 0.0001);
        assertEquals(100.0, destino.getMontante(), 0.0001);
    }

    @Test
    void transferenciaComValorOuContasInvalidasNaoCrasha() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta origem = u.abrirConta("Banco A", "1", 500.0);
        Conta destino = u.abrirConta("Banco B", "2", 100.0);
        Conta contaDeOutroUsuario = new Conta("Banco C", "3", 999.0);

        assertThrows(IllegalArgumentException.class, () -> u.transferirEntreContas(-10.0, origem, destino));
        assertThrows(IllegalArgumentException.class, () -> u.transferirEntreContas(10.0, origem, contaDeOutroUsuario));
        assertThrows(IllegalArgumentException.class, () -> u.transferirEntreContas(10.0, null, destino));
    }
}
