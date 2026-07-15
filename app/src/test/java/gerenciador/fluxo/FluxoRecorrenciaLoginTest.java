package gerenciador.fluxo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import gerenciador.base.Usuario;
import gerenciador.enums.Frequencia;
import gerenciador.operacoes.movimentacoes.DespesaRecorrente;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;

// Cobre o cenario: Logout e login de novo com uma despesa recorrente pendente, para ver o fluxo de
// processarRecorrencias() + Alert (o Alert em si é responsabilidade de LoginController; aqui
// validamos os "avisos" que alimentam esse Alert.WARNING)
class FluxoRecorrenciaLoginTest {

    @Test
    void processarRecorrenciasAplicaOcorrenciasPendentesSemCrash() {
        // Regressao do bug critico: Usuario.processarRecorrencias() (base/Usuario.java) iterava
        // "this.transacoes.getHistorico()" com um for-each enquanto adicionarTransacao(...), chamado
        // dentro do proprio loop, inseria na MESMA ArrayList -- lancando ConcurrentModificationException
        // assim que qualquer ocorrencia recorrente era aplicada com sucesso (exatamente este cenario:
        // despesa recorrente pendente + logout/login). Corrigido iterando uma copia da lista.
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 10000.0);
        Categoria cat = new Categoria("Assinatura", 0.0);
        LocalDate inicio = LocalDate.now().minusMonths(3);

        DespesaRecorrente assinatura = new DespesaRecorrente("Streaming", UUID.randomUUID().toString(), 40.0,
            new ArrayList<>(), cat, inicio, conta, Frequencia.MENSAL, inicio);
        u.adicionarTransacao(assinatura); // 1a cobranca, no dia da criacao (simula cadastro ha 3 meses)

        // simula "logout" (estado ja esta em memoria/persistido) e "login de novo"
        List<String> avisos = assertDoesNotThrow(u::processarRecorrencias);

        assertTrue(avisos.isEmpty(), "com saldo suficiente nao deve haver avisos (nao dispara o Alert)");
        // 1 cobranca inicial + 3 ocorrencias mensais geradas retroativamente (mes 1, 2 e 3)
        assertEquals(4, u.getHistorico().size());
        assertEquals(10000.0 - (4 * 40.0), conta.getMontante(), 0.0001);
    }

    @Test
    void processarRecorrenciasComSaldoInsuficienteGeraAvisoEmVezDeCrash() {
        Usuario u = new Usuario("Fulano", "fulano", "hash");
        Conta conta = u.abrirConta("Banco X", "1", 50.0); // saldo baixo de proposito
        Categoria cat = new Categoria("Assinatura", 0.0);
        LocalDate inicio = LocalDate.now().minusMonths(3);

        DespesaRecorrente assinatura = new DespesaRecorrente("Streaming caro", UUID.randomUUID().toString(), 40.0,
            new ArrayList<>(), cat, inicio, conta, Frequencia.MENSAL, inicio);
        u.adicionarTransacao(assinatura); // cobranca inicial: 50 - 40 = 10

        // isto é o que popula o Alert.WARNING("Recorrências com pendências") em LoginController.onLogin()
        List<String> avisos = assertDoesNotThrow(u::processarRecorrencias);

        // as 3 ocorrencias seguintes (mes 1, 2, 3) nao cabem no saldo de 10 -> viram avisos, nao excecoes
        assertEquals(3, avisos.size());
        assertEquals(10.0, conta.getMontante(), 0.0001, "ocorrencias com saldo insuficiente nao devem debitar nada");
        avisos.forEach(aviso -> assertTrue(aviso.contains("Streaming caro")));
    }
}
