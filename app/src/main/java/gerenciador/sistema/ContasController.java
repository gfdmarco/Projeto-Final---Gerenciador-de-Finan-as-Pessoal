//preciso adicionar um bgl pra fazer a transferencia entre contas dps da implementacao no backpackage gerenciador.sistema;
package gerenciador.sistema;

import java.util.UUID;

import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.interfaces.UsuarioNecessario;
import gerenciador.suporte.Conta;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ContasController implements UsuarioNecessario{

    @FXML private Label erroTroca;
    @FXML private TableView<Conta> tabelaContas;
    @FXML private TableColumn<Conta, String> colunaNome;
    @FXML private TableColumn<Conta, Double> colunaMontante;
    @FXML private TableColumn<Conta, String> colunaID;
    @FXML private TableColumn<Conta, Integer> colunaTransacoes;

    @FXML private TextField campoNomeCriar;
    @FXML private TextField campoSaldoInicial;

    @FXML private TextField campoValor;
    @FXML private TextField campoNomeConta1;
    @FXML private TextField campoNomeConta2;

    private Usuario usuarioAtual;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioAtual = usuario;

        this.usuarioAtual.setCategorias();
        this.usuarioAtual.setTags();

        colunaNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getBanco()));
        colunaMontante.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getMontante()));
        colunaID.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getID()));
        colunaTransacoes.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTransacoesAssociadas().size()));

        tabelaContas.getItems().addAll(usuario.getContas());
    }

    @FXML
    void onAdicionar(){
        String nome = campoNomeCriar.getText();
        double saldo = Double.parseDouble(campoSaldoInicial.getText());
        String id = UUID.randomUUID().toString();

        Conta novaConta = new Conta(nome, id, saldo);

        usuarioAtual.getContas().add(novaConta);
        PersistenciaJSON.salvar(usuarioAtual);
    }

    @FXML
    void onTransferir(){
        //precisa adicionar um bgl pra fazer a transferencia entre contas dps da implementacao no back;
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
