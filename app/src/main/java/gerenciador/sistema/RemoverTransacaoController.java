package gerenciador.sistema;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.exceptions.SaldoInsuficienteException;
import gerenciador.interfaces.UsuarioNecessario;
import gerenciador.operacoes.movimentacoes.Transacao;
import gerenciador.suporte.Tag;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RemoverTransacaoController implements UsuarioNecessario {

    private Usuario usuarioAtual;
    @FXML private Label erroTroca;
    @FXML private TableView<Transacao> tabelaTransacoes;
    @FXML private TableColumn<Transacao, String> colunaNome;
    @FXML private TableColumn<Transacao, String> colunaID;
    @FXML private TableColumn<Transacao, Double> colunaValor;
    @FXML private TableColumn<Transacao, String> colunaTags;
    @FXML private TableColumn<Transacao, String> colunaCategoria;
    @FXML private TableColumn<Transacao, String> colunaData;
    @FXML private TableColumn<Transacao, String> colunaConta;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioAtual = usuario;

        this.usuarioAtual.setCategorias();
        this.usuarioAtual.setTags();

        colunaNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        colunaID.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getID()));
        colunaValor.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getValor()));
        //para as tags, exibimos concatenando a lista de tags em uma string que separa as tags por vírgula
        colunaTags.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTags().stream().map(Tag::getNome)
            .collect(Collectors.joining(", "))));
        colunaCategoria.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategoria() != null ? cell.getValue().getCategoria().getNome() : ""));
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("d/MMMM/yyyy");
        colunaData.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getData() != null ? cell.getValue().getData().format(formatador) : ""));
        colunaConta.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getConta() != null ? cell.getValue().getConta().getBanco() : ""));

        tabelaTransacoes.getItems().addAll(usuario.getHistorico());

        //parte do botão direito para remover
        ContextMenu menuContexto = new ContextMenu();
        MenuItem itemRemover = new MenuItem("Remover Transação");
        menuContexto.getItems().add(itemRemover);

        itemRemover.setOnAction(event -> {
            Transacao selecionada = tabelaTransacoes.getSelectionModel().getSelectedItem();
            if (selecionada != null) {
                try {
                    this.usuarioAtual.removerTransacao(selecionada.getID());
                    tabelaTransacoes.getItems().remove(selecionada);
                    PersistenciaJSON.salvar(this.usuarioAtual);
                }
                catch (SaldoInsuficienteException e) {
                    erroTroca.setText(e.getMessage());
                }
            }
        });
        tabelaTransacoes.setContextMenu(menuContexto);
    }

    @FXML
    void onVoltar(){
        try {
            App.trocarTela("transacoes", this.usuarioAtual);

        }
        catch (Exception e){
            erroTroca.setText("Erro ao trocar de tela");
        }
    }
}
