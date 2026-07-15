package gerenciador.sistema;

import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.interfaces.UsuarioNecessario;
import gerenciador.suporte.Tag;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TagsController implements UsuarioNecessario {

    @FXML private Label erroTroca;
    @FXML private Label sucessoAcao;

    @FXML private TableView<Tag> tabelaTags;
    @FXML private TableColumn<Tag, String> colunaNome;
    @FXML private TableColumn<Tag, Integer> colunaTransacoes;

    @FXML private TextField campoNomeCriar;

    private Usuario usuarioAtual;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioAtual = usuario;

        this.usuarioAtual.setCategorias();
        this.usuarioAtual.setTags();

        colunaNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        colunaTransacoes.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTransacoesAssociadas().size()));

        tabelaTags.getItems().clear();
        tabelaTags.getItems().setAll(usuario.tagsSistema());
    }

    @FXML
    void onCriar(){
        String nome = campoNomeCriar.getText();

        if (nome == null || nome.trim().isEmpty()) {
            erroTroca.setText("Informe um nome de tag válido.");
            return;
        }

        Tag novaTag = new Tag(nome.trim());
        usuarioAtual.tagsSistema().add(novaTag);
        PersistenciaJSON.salvar(usuarioAtual);

        tabelaTags.getItems().setAll(usuarioAtual.tagsSistema());
        campoNomeCriar.clear();
        sucessoAcao.setText("Tag criada com sucesso.");
    }

    @FXML
    void onVoltar(){
        try {
            App.trocarTela("dashboard", this.usuarioAtual);

        }
        catch (Exception e){
            erroTroca.setText("Erro ao trocar de tela");
        }
    }
}
