package gerenciador.suporte;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ContaTest {

    @Test
    void debitarDeveLancarExcecaoSeSaldoInsuficiente() {
        Conta conta = new Conta("Banco", "1", 10.0);
        assertThrows(RuntimeException.class, () -> conta.debitar(20.0));
    }

    @Test
    void creditarDeveAumentarSaldo() {
        Conta conta = new Conta("Banco", "1", 10.0);
        conta.creditar(5.0);
        assertEquals(15.0, conta.getMontante(), 0.0001);
    }
}
