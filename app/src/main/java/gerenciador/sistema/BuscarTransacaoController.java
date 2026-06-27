package gerenciador.sistema;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.interfaces.UsuarioNecessario;
import gerenciador.operacoes.movimentacoes.Transacao;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;
import gerenciador.suporte.Tag;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class BuscarTransacaoController implements UsuarioNecessario{
    
    @FXML private DatePicker campoData;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private ComboBox<Conta> comboConta;
    @FXML private ListView<Tag> listaTags;
    @FXML private TextField campoValor;
    @FXML private TableView<Transacao> tabelaTransacoes;
    @FXML private TableColumn<Transacao, String> colunaNome;
    @FXML private TableColumn<Transacao, String> colunaID;
    @FXML private TableColumn<Transacao, Double> colunaValor;
    @FXML private TableColumn<Transacao, String> colunaTags;
    @FXML private TableColumn<Transacao, String> colunaCategoria;
    @FXML private TableColumn<Transacao, String> colunaData;
    @FXML private TableColumn<Transacao, String> colunaConta;
    @FXML private Label labelErro;

    private Usuario usuarioAtual;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioAtual = usuario;

        this.usuarioAtual.setCategorias();
        this.usuarioAtual.setTags();

        comboCategoria.getItems().addAll(usuarioAtual.categoriasSistema());
        comboConta.getItems().addAll(usuarioAtual.getContas());

        colunaNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        colunaID.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getID()));
        colunaValor.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getValor()));
        //para as tags, exibimos concatenando a lista de tags em uma string que separa as tags por vírgula
        colunaTags.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTags().stream().map(Tag::getNome)
            .collect(Collectors.joining(", "))));
        colunaCategoria.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategoria().getNome()));
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("d/MMMM/yyyy");
        colunaData.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getData().format(formatador)));
        colunaConta.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getConta().getBanco()));
        
        listaTags.getItems().addAll(usuarioAtual.tagsSistema());
        listaTags.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    @FXML
    void onBuscar(){
        ArrayList<Transacao> resultado = new ArrayList<>();

        if (campoData.getValue() != null){
            resultado = this.usuarioAtual.buscarTransacao(campoData.getValue());
        }
        else if (!campoValor.getText().trim().isEmpty()){
            try {
                double valor = Double.parseDouble(campoValor.getText().trim());
                resultado = this.usuarioAtual.buscarTransacao(valor);
            }
            catch (NumberFormatException e){
                labelErro.setText("Digite um valor válido, por favor.");
            }
        }
        else if (comboCategoria.getValue() != null){
            resultado = this.usuarioAtual.buscarTransacao(comboCategoria.getValue());
        }
        else if (comboConta.getValue() != null){
            resultado = this.usuarioAtual.buscarTransacao(comboConta.getValue());
        }
        else if (listaTags.getSelectionModel().getSelectedItem() != null){
            resultado = this.usuarioAtual.buscarTransacao(listaTags.getSelectionModel().getSelectedItem());
        }
        else {
            labelErro.setText("Não foram encontradas transações correspondentes.");
            resultado = this.usuarioAtual.getHistorico();
        }

        tabelaTransacoes.getItems().clear();
        tabelaTransacoes.getItems().addAll(resultado);
        PersistenciaJSON.salvar(usuarioAtual);
    }

    @FXML
    void onVoltar(){
        try {
            App.trocarTela("transacoes", this.usuarioAtual);

        }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }
}
