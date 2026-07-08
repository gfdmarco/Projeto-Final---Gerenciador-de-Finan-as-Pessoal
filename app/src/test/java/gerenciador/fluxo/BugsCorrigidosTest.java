package gerenciador.fluxo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import gerenciador.base.Usuario;
import gerenciador.enums.Frequencia;
import gerenciador.exceptions.SaldoInsuficienteException;
import gerenciador.operacoes.movimentacoes.Despesa;
import gerenciador.operacoes.movimentacoes.DespesaRecorrente;
import gerenciador.operacoes.movimentacoes.Receita;
import gerenciador.operacoes.relatorios.RelatorioPeriodo;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;

// Testes de regressao: cada um reproduz, no nivel da camada de negocio (a mesma API que os
// Controllers usam), um comportamento que ja foi confirmado como bug e corrigido. Os asserts
// documentam o comportamento CORRETO esperado agora, para travar qualquer regressao futura.
class BugsCorrigidosTest {

    @Test
    void processarRecorrenciasComOcorrenciaAplicavelNaoLancaMaisConcurrentModificationException() {
        // Corrigido: Usuario.processarRecorrencias() (base/Usuario.java) agora itera uma copia do
        // historico em vez da lista viva, entao adicionarTransacao(...) chamado dentro do loop nao
        // causa mais ConcurrentModificationException quando uma ocorrencia recorrente e aplicada.
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 10000.0);
        Categoria cat = new Categoria("Assinatura", 0.0);
        LocalDate inicio = LocalDate.now().minusMonths(2);

        DespesaRecorrente assinatura = new DespesaRecorrente("Streaming", UUID.randomUUID().toString(), 40.0,
            new ArrayList<>(), cat, inicio, conta, Frequencia.MENSAL, inicio);
        u.adicionarTransacao(assinatura);

        assertDoesNotThrow(u::processarRecorrencias);
    }

    @Test
    void removerReceitaJaGastaAgoraDaErroTratadoEmVezDeExcecaoNaoCapturada() {
        // O comportamento de negocio continua o mesmo -- Usuario.removerTransacao ainda lanca
        // SaldoInsuficienteException ao tentar reverter uma Receita cujo valor ja foi gasto, e isso
        // continua correto (e um conflito de estado real que precisa ser sinalizado). O que foi
        // corrigido foi RemoverTransacaoController, que agora captura essa excecao e mostra a
        // mensagem em vez de deixá-la subir sem tratamento na UI.
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 0.0);
        Categoria catSalario = new Categoria("Salario", 0.0);
        String idReceita = UUID.randomUUID().toString();
        Receita receita = new Receita("Salario", idReceita, 500.0, new ArrayList<>(), catSalario, LocalDate.now(), conta);
        u.adicionarTransacao(receita); // credita 500 -> saldo = 500

        Categoria catGasto = new Categoria("Gastos", 0.0);
        Despesa gasto = new Despesa("Gastou tudo", UUID.randomUUID().toString(), 500.0, new ArrayList<>(), catGasto, LocalDate.now(), conta);
        u.adicionarTransacao(gasto); // debita 500 -> saldo = 0

        assertThrows(SaldoInsuficienteException.class, () -> u.removerTransacao(idReceita));
    }

    @Test
    void editarSalarioSemNuncaTerRegistradoDaErroClaroEmVezDeNPE() {
        // Corrigido em duas camadas: DadosPessoaisController.onEditarSalario agora tem o "return"
        // que faltava apos detectar que nao ha salario definido, e Usuario.editarSalario agora
        // lanca IllegalStateException (em vez de deixar acontecer NullPointerException) caso seja
        // chamado diretamente sem um salario registrado.
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        assertEquals(0.0, u.getSalario(), 0.0001);

        assertThrows(IllegalStateException.class, () -> u.editarSalario(1000.0));
    }

    @Test
    void gastoNoMesNaoSomaMaisReceitasDaMesmaCategoria() {
        // Corrigido: Categoria.gastoNoMes() (suporte/Categoria.java) agora so soma transacoes que
        // sao Despesa, entao uma Receita associada a mesma categoria nao infla mais o "gasto".
        Categoria cat = new Categoria("Compartilhada", 0.0);
        Conta conta = new Conta("Banco X", "1", 1000.0);

        Despesa despesa = new Despesa("Gasto", UUID.randomUUID().toString(), 100.0, new ArrayList<>(), cat, LocalDate.now(), conta);
        Receita receita = new Receita("Ganho", UUID.randomUUID().toString(), 50.0, new ArrayList<>(), cat, LocalDate.now(), conta);
        cat.adicionarTransacao(despesa);
        cat.adicionarTransacao(receita);

        assertEquals(100.0, cat.gastoNoMes(), 0.0001);
    }

    @Test
    void relatorioPeriodoNaoInflaMaisValorDeDespesaRecorrente() {
        // Corrigido: RelatorioPeriodo.gerar (operacoes/relatorios/RelatorioPeriodo.java) nao rateia
        // mais o valor do template DespesaRecorrente/ReceitaRecorrente por uma fracao do periodo --
        // agora soma o valor cheio de cada transacao (recorrente ou nao) uma unica vez, igual ao que
        // realmente foi debitado/creditado na conta.
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 10000.0);
        Categoria cat = new Categoria("Assinatura", 0.0);
        LocalDate inicio = LocalDate.now().minusMonths(3);

        DespesaRecorrente assinatura = new DespesaRecorrente("Streaming", UUID.randomUUID().toString(), 100.0,
            new ArrayList<>(), cat, inicio, conta, Frequencia.MENSAL, inicio);
        u.adicionarTransacao(assinatura);
        u.processarRecorrencias(); // agora seguro, gera as ocorrencias concretas dos meses seguintes

        double saldoRealmenteDebitado = 10000.0 - conta.getMontante();

        RelatorioPeriodo relatorio = new RelatorioPeriodo(inicio, LocalDate.now());
        String texto = relatorio.gerar(u.getHistorico(), u);

        Matcher m = Pattern.compile("Despesas: R\\$ (-?[0-9.]+)").matcher(texto);
        assertTrue(m.find());
        double despesasReportadas = Double.parseDouble(m.group(1));

        assertEquals(saldoRealmenteDebitado, despesasReportadas, 0.0001);
    }
}
