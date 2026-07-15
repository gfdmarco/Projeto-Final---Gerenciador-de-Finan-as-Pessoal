package gerenciador.operacoes.movimentacoes;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;
import gerenciador.suporte.Tag;

class TransacaoTest {

    // 1. Classe concreta criada exclusivamente para viabilizar o teste da classe abstrata
    private static class TransacaoTesteConcreto extends Transacao {
        public TransacaoTesteConcreto(String nome, String id, double valor, ArrayList<Tag> tags, Categoria categoria, LocalDate data, Conta conta) {
            super(nome, id, valor, tags, categoria, data, conta);
        }

        // Método obrigatório por conta da assinatura abstrata na classe mãe
        @Override
        public void realizarTransacao() {
            // Fica vazio no teste, pois só queremos testar os getters e setters da classe base
        }
    }

    private Transacao transacao;
    private ArrayList<Tag> tagsDaTransacao;
    private Categoria categoria;
    private LocalDate dataHoje;

    @BeforeEach
    void setUp() {
        tagsDaTransacao = new ArrayList<>();
        tagsDaTransacao.add(new Tag("Urgente"));
        
        categoria = new Categoria("Saúde", 300.0);
        dataHoje = LocalDate.now();
        
        // Instanciamos a nossa classe "dublê" de transação
        // Passando 'null' para Conta apenas para simplificar, já que a Transacao não exige validação de nulidade no construtor
        transacao = new TransacaoTesteConcreto("Remédios", "t001", 150.0, tagsDaTransacao, categoria, dataHoje, null);
    }

    @Test
    void construtorEGettersDevemAtribuirERetornarDadosCorretamente() {
        // Valida se todos os parâmetros passados no construtor estão sendo devolvidos corretamente pelos Getters
        assertEquals("Remédios", transacao.getNome());
        assertEquals("t001", transacao.getID());
        assertEquals(150.0, transacao.getValor());
        assertEquals(tagsDaTransacao, transacao.getTags());
        assertEquals(categoria, transacao.getCategoria());
        assertEquals(dataHoje, transacao.getData());
        assertNull(transacao.getConta());
    }

    @Test
    void setValorDeveAtualizarOValorQuandoPassadoNumeroPositivo() {
        transacao.setValor(250.50);
        assertEquals(250.50, transacao.getValor());
    }

    @Test
    void setValorDeveLancarExcecaoQuandoPassadoZero() {
        // Valida a regra "valor <= 0" do seu código
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transacao.setValor(0.0);
        });
        
        // Verifica se a mensagem da exceção é exatamente a que você programou
        assertEquals("O valor da transferência deve ser positivo.", exception.getMessage());
    }

    @Test
    void setValorDeveLancarExcecaoQuandoPassadoNumeroNegativo() {
        // Garante que tentativas de injetar valores negativos falhem de forma segura
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transacao.setValor(-100.0);
        });
        
        assertEquals("O valor da transferência deve ser positivo.", exception.getMessage());
    }
}