package gerenciador.operacoes.movimentacoes;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gerenciador.enums.Frequencia;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Tag;

class ReceitaRecorrenteTest {

    private ReceitaRecorrente receitaRecorrente;
    private Categoria categoria;
    private ArrayList<Tag> tags;
    private LocalDate dataInicio;

    @BeforeEach
    void setUp() {
        categoria = new Categoria("Salário", 5000.0);
        tags = new ArrayList<>();
        tags.add(new Tag("Fixo"));

        dataInicio = LocalDate.now();
        receitaRecorrente = new ReceitaRecorrente(
            "Salário Mensal", 
            "rec001", 
            3000.0, 
            tags, 
            categoria, 
            dataInicio, 
            null, // Conta nula apenas para simplificar a injeção do teste
            Frequencia.MENSAL, 
            dataInicio
        );
    }

    @Test
    void construtorEGettersDevemInicializarComValoresCorretos() {
        // Verifica se a inicialização padrão definida no construtor ocorreu perfeitamente
        assertTrue(receitaRecorrente.isAtivo()); // Deve iniciar como ativo
        assertEquals(dataInicio, receitaRecorrente.getDataInicio());
        assertEquals(Frequencia.MENSAL, receitaRecorrente.getRecorrencia());
        
        // A última data aplicada deve ser exatamente igual à data de início logo após a criação
        assertEquals(dataInicio, receitaRecorrente.getUltimaDataAplicada()); 
    }

    @Test
    void setUltimaDataAplicadaDeveAtualizarADaTaComSucesso() {
        LocalDate novaData = LocalDate.now().plusMonths(1);
        
        receitaRecorrente.setUltimaDataAplicada(novaData);
        
        assertEquals(novaData, receitaRecorrente.getUltimaDataAplicada());
    }

    @Test
    void gerarTransacaoRecorrenteDeveCriarNovaReceitaIndependente() {
        LocalDate dataFutura = LocalDate.now().plusMonths(1);

        // Dispara o método para fabricar uma nova transação com base na recorrente
        Transacao transacaoGerada = receitaRecorrente.gerarTransacaoRecorrente(dataFutura);

        // Validações de herança dos dados (nome, valor, categoria, tags)
        assertNotNull(transacaoGerada);
        assertEquals("Salário Mensal", transacaoGerada.getNome());
        assertEquals(3000.0, transacaoGerada.getValor());
        assertEquals(categoria, transacaoGerada.getCategoria());
        assertEquals(tags, transacaoGerada.getTags());

        // Validações exclusivas da nova transação gerada
        assertEquals(dataFutura, transacaoGerada.getData()); // A data não pode ser a de início, mas sim a injetada
        assertNotEquals("rec001", transacaoGerada.getID()); // O ID deve ser único
        assertFalse(transacaoGerada.getID().isEmpty()); // Garante que o UUID gerado não veio vazio
    }
}