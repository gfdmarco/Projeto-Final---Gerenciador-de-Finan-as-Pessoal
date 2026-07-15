package gerenciador.operacoes.movimentacoes;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gerenciador.enums.Frequencia;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Tag;

class DespesaRecorrenteTest {

    private DespesaRecorrente despesaRecorrente;
    private Categoria categoria;
    private ArrayList<Tag> tags;
    private LocalDate dataInicio;

    @BeforeEach
    void setUp() {
        categoria = new Categoria("Moradia", 1500.0);
        tags = new ArrayList<>();
        tags.add(new Tag("Fixa"));

        dataInicio = LocalDate.now();
        despesaRecorrente = new DespesaRecorrente(
            "Aluguel", 
            "desp001", 
            1200.0, 
            tags, 
            categoria, 
            dataInicio, 
            null,
            Frequencia.MENSAL, 
            dataInicio
        );
    }

    @Test
    void construtorEGettersDevemInicializarComValoresCorretos() {
        // Valida as atribuições padrão feitas dentro do construtor da DespesaRecorrente
        assertTrue(despesaRecorrente.isAtivo()); 
        assertEquals(dataInicio, despesaRecorrente.getDataInicio());
        assertEquals(Frequencia.MENSAL, despesaRecorrente.getRecorrencia());
        
        // A última data aplicada deve iniciar com o mesmo valor de dataInicio
        assertEquals(dataInicio, despesaRecorrente.getUltimaDataAplicada()); 
    }

    @Test
    void setUltimaDataAplicadaDeveAtualizarADaTaComSucesso() {
        LocalDate novaData = LocalDate.now().plusMonths(1);
        
        despesaRecorrente.setUltimaDataAplicada(novaData);
        
        assertEquals(novaData, despesaRecorrente.getUltimaDataAplicada());
    }

    @Test
    void gerarTransacaoRecorrenteDeveCriarNovaDespesaIndependente() {
        LocalDate dataFutura = LocalDate.now().plusMonths(1);

        // Dispara o método que fabrica a transação para o mês seguinte
        Transacao transacaoGerada = despesaRecorrente.gerarTransacaoRecorrente(dataFutura);

        // Verifica se a transação gerada é, de fato, do tipo correto
        assertTrue(transacaoGerada instanceof Despesa);

        // Validações de herança dos dados copiados (nome, valor, categoria, tags)
        assertNotNull(transacaoGerada);
        assertEquals("Aluguel", transacaoGerada.getNome());
        assertEquals(1200.0, transacaoGerada.getValor());
        assertEquals(categoria, transacaoGerada.getCategoria());
        assertEquals(tags, transacaoGerada.getTags());

        // Validações exclusivas da nova transação gerada
        assertEquals(dataFutura, transacaoGerada.getData()); // Garante que a data nova foi aplicada
        assertNotEquals("desp001", transacaoGerada.getID()); // O ID deve ser um UUID novo, não o original
        assertFalse(transacaoGerada.getID().isEmpty()); // Certifica que o UUID não está vazio
    }
}