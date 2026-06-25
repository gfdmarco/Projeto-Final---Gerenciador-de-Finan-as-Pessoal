/* package gerenciador.sistema;

import java.time.LocalDate;
import java.util.UUID;

import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.exceptions.OrcamentoExcedidoException;
import gerenciador.exceptions.SaldoInsuficienteException;
import gerenciador.interfaces.UsuarioNecessario;
import gerenciador.operacoes.movimentacoes.Despesa;
import gerenciador.operacoes.movimentacoes.DespesaRecorrente;
import gerenciador.operacoes.movimentacoes.Receita;
import gerenciador.operacoes.movimentacoes.ReceitaRecorrente;
import gerenciador.operacoes.movimentacoes.Transacao;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;
import gerenciador.suporte.Tag;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

public class AdicionarTransacaoController implements UsuarioNecessario {

    @FXML private TextField campoValor;
    @FXML private TextField campoNome;
    @FXML private DatePicker campoData;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private ComboBox<Conta> comboConta;
    @FXML private ComboBox<Tag> comboTag;
    @FXML private RadioButton radioDespesa;
    @FXML private RadioButton radioReceita;
    @FXML private CheckBox checkRecorrente;
    @FXML private Label labelErro;

    private Usuario usuarioAtual;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioAtual = usuario;
        comboCategoria.getItems().addAll(usuarioAtual.categoriasSistema());
        comboConta.getItems().addAll(usuarioAtual.getContas());
    }

    @FXML
    void onConfirmar() {
        try {
            double valor = Double.parseDouble(campoValor.getText());
            String nome = campoNome.getText();
            LocalDate data = campoData.getValue();
            Categoria categoria = comboCategoria.getValue();
            Conta conta = comboConta.getValue();
            Tag tag
            boolean recorrente = checkRecorrente.isSelected();

            Transacao novaTransacao;

            if (radioDespesa.isSelected()) {
                if (recorrente) {
                    String id = UUID.randomUUID().toString();
                    novaTransacao = new DespesaRecorrente(nome, id, valor, tags, categoria, data, conta, LocalDate.now(), frequencia);
                } else {
                    String id = UUID.randomUUID().toString();
                    novaTransacao = new Despesa(nome, id, valor, tags, categoria, data, conta);
                }
            } else if (radioReceita.isSelected()) {
                if (recorrente) {
                    String id = UUID.randomUUID().toString();
                    novaTransacao = new ReceitaRecorrente(nome, id, valor, tags, categoria, data, conta, fonte, dataInicio, frequencia);
                } else {
                    String id = UUID.randomUUID().toString();
                    novaTransacao = new Receita(nome, id, valor, tags, categoria, data, conta);
                }
            } else {
                labelErro.setText("Selecione Receita ou Despesa.");
                return; // sai do método sem fazer nada
            }

            novaTransacao.realizarTransacao();
            usuarioAtual.adicionarTransacao(novaTransacao);
            PersistenciaJSON.salvar(usuarioAtual);

        }
        catch (NumberFormatException e) {
            labelErro.setText("Valor inválido.");
        }
        catch (SaldoInsuficienteException e) {
            labelErro.setText(e.getMessage());
        }
        catch (OrcamentoExcedidoException e) {
            // tratar com Alert de confirmação
        }
    }
}
*/