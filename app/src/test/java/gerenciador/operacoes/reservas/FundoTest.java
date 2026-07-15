package gerenciador.operacoes.reservas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gerenciador.enums.TipoFundo;
import gerenciador.exceptions.SaldoInsuficienteException;

class FundoTest {

    // 1. Criamos uma classe concreta estendendo a classe abstrata Fundo apenas para fins de teste
    private static class FundoTesteConcreto extends Fundo {
        public FundoTesteConcreto(String nome, TipoFundo tipo, double valorObjetivo, double taxaDeValorizacao, LocalDate dataInicio) {
            super(nome, tipo, valorObjetivo, taxaDeValorizacao, dataInicio);
        }
    }

    private Fundo fundo;

    @BeforeEach
    void setUp() {
        // 2. Inicializa o ambiente antes de cada teste com um objetivo de R$ 1000,00 e taxa de 10%
        // O valor acumulado inicia em 0 conforme o construtor da sua classe.
        fundo = new FundoTesteConcreto("Reserva Viagem", null, 1000.0, 10.0, LocalDate.now());
    }

    @Test
    void depositarDeveAumentarOSaldoCorretamente() {
        fundo.depositar(200.0);
        assertEquals(200.0, fundo.getSaldo()); // Verifica se o saldo refletiu o depósito
    }

    @Test
    void depositarValorNegativoOuZeroDeveLancarExcecao() {
        // Valida a regra "valor > 0" do método depositar
        assertThrows(NumberFormatException.class, () -> fundo.depositar(0.0));
        assertThrows(NumberFormatException.class, () -> fundo.depositar(-50.0));
    }

    @Test
    void sacarDeveReduzirOSaldoCorretamente() {
        fundo.depositar(500.0);
        fundo.sacar(150.0);
        assertEquals(350.0, fundo.getSaldo()); // 500 - 150 = 350
    }

    @Test
    void sacarValorMaiorQueOSaldoDeveLancarSaldoInsuficienteException() {
        fundo.depositar(100.0);
        
        // Garante que a exceção customizada é lançada ao tentar sacar mais do que tem
        assertThrows(SaldoInsuficienteException.class, () -> fundo.sacar(150.0));
    }

    @Test
    void sacarValorNegativoOuZeroDeveLancarExcecao() {
        fundo.depositar(100.0);
        
        // Valida o bloco `else` interno do método sacar, que previne saques <= 0
        assertThrows(NumberFormatException.class, () -> fundo.sacar(0.0));
        assertThrows(NumberFormatException.class, () -> fundo.sacar(-20.0));
    }

    @Test
    void getProgressoDeveRetornarFracaoCorretaDoObjetivo() {
        // Depositando 250.0, a fração (valorAcumulado / valorObjetivo) deve ser 0.25 (ou 25%)
        fundo.depositar(250.0);
        assertEquals(0.25, fundo.getProgresso());
    }

    @Test
    void getProgressoDeveRetornarZeroSeObjetivoForZero() {
        // Modifica o objetivo para 0.0 para testar a prevenção de divisão por zero na sua classe
        fundo.setObjetivo(0.0); 
        fundo.depositar(500.0);
        
        assertEquals(0.0, fundo.getProgresso()); 
    }

    @Test
    void jurosCompostosDeveAplicarRentabilidadeCorretaComBaseNaData() {
        // Para testar o juros, precisamos de um fundo que tenha sido criado no passado
        // Subtraímos exatamente 365 dias da data de hoje para simular 1 ano de rendimento
        LocalDate dataPassada = LocalDate.now().minusDays(365);
        Fundo fundoAntigo = new FundoTesteConcreto("Ações", null, 5000.0, 10.0, dataPassada);
        
        fundoAntigo.depositar(1000.0); // Saldo inicial antes dos juros
        
        fundoAntigo.jurosCompostos();
        
        assertEquals(1100.0, fundoAntigo.getSaldo(), 0.01);
    }
}