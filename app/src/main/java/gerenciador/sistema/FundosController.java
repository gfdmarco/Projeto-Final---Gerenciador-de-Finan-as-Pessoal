package gerenciador.sistema;

import java.time.LocalDate;
import java.util.List;

import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.enums.TipoFundo;
import gerenciador.interfaces.UsuarioNecessario;
import gerenciador.operacoes.reservas.Fundo;
import gerenciador.operacoes.reservas.FundoEmergencia;
import gerenciador.operacoes.reservas.FundoInvestimento;
import gerenciador.suporte.Conta;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FundosController implements UsuarioNecessario {

    @FXML private Label erroTroca;
    @FXML private TableView<Fundo> tabelaFundos;
    @FXML private TableColumn<Fundo, String> colunaNome;
    @FXML private TableColumn<Fundo, String> colunaTipo;
    @FXML private TableColumn<Fundo, Double> colunaSaldo;
    @FXML private TableColumn<Fundo, Double> colunaObjetivo;

    @FXML private TextField campoNome;
    @FXML private ComboBox<TipoFundo> comboTipo;
    @FXML private TextField campoObjetivo;
    @FXML private TextField campoTaxa;
    @FXML private ComboBox<Conta> comboConta;
    @FXML private Button botaoCriar;
    @FXML private Button botaoVoltar;

    private Usuario usuarioAtual;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioAtual = usuario;

        colunaNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));
        colunaTipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipo().name()));
        colunaSaldo.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getSaldo()).asObject());
        colunaObjetivo.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getObjetivo()).asObject());

        comboTipo.setItems(FXCollections.observableArrayList(TipoFundo.values()));
        comboConta.setItems(FXCollections.observableArrayList(usuarioAtual.getContas()));

        tabelaFundos.getItems().clear();
        tabelaFundos.getItems().setAll(usuarioAtual.getFundos());
    }

    @FXML
    void onCriarFundo() {
        String nome = campoNome.getText();
        String objetivoTexto = campoObjetivo.getText();
        String taxaTexto = campoTaxa.getText();
        TipoFundo tipo = comboTipo.getValue();
        Conta conta = comboConta.getValue();

        if (nome == null || nome.trim().isEmpty()) {
            erroTroca.setText("Informe um nome para o fundo.");
            return;
        }
        if (tipo == null) {
            erroTroca.setText("Selecione o tipo de fundo.");
            return;
        }
        if (conta == null) {
            erroTroca.setText("Selecione uma conta para associar o fundo.");
            return;
        }
        double objetivo;
        double taxa;
        try {
            objetivo = Double.parseDouble(objetivoTexto.trim());
            taxa = Double.parseDouble(taxaTexto.trim());
        } catch (NumberFormatException e) {
            erroTroca.setText("Informe valores numéricos para objetivo e taxa.");
            return;
        }

        usuarioAtual.criarFundo(nome.trim(), tipo, objetivo, taxa, LocalDate.now(), conta);
        PersistenciaJSON.salvar(usuarioAtual);

        tabelaFundos.getItems().setAll(usuarioAtual.getFundos());
        campoNome.clear();
        campoObjetivo.clear();
        campoTaxa.clear();
        erroTroca.setText("Fundo criado com sucesso.");
    }

    @FXML
    void onVoltar() {
        try {
            App.trocarTela("dashboard", usuarioAtual);
        } catch (Exception e) {
            erroTroca.setText("Erro ao trocar de tela");
        }
    }
}
