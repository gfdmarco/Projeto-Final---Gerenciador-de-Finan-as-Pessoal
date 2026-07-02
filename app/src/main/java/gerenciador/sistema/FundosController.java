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
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ProgressBarTableCell;

public class FundosController implements UsuarioNecessario {

    @FXML private Label erroTroca;
    @FXML private TableView<Fundo> tabelaFundos;
    @FXML private TableColumn<Fundo, String> colunaNome;
    @FXML private TableColumn<Fundo, String> colunaTipo;
    @FXML private TableColumn<Fundo, Double> colunaSaldo;
    @FXML private TableColumn<Fundo, Double> colunaObjetivo;
    @FXML private TableColumn<Fundo, Double> colunaProgresso;

    @FXML private TextField campoNome;
    @FXML private ComboBox<TipoFundo> comboTipo;
    @FXML private TextField campoObjetivo;
    @FXML private TextField campoTaxa;
    @FXML private ComboBox<Conta> comboConta;

    @FXML private TextField campoValorOperacao;
    @FXML private RadioButton radioDeposito;
    @FXML private RadioButton radioSaque;

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
        colunaProgresso.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue() != null ? cell.getValue().getProgresso() : 0.0).asObject());
        colunaProgresso.setCellFactory(ProgressBarTableCell.forTableColumn());

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
    void onRealizarOperacao(){
        double valor = Double.parseDouble(campoValorOperacao.getText());
        Fundo fundoSelecionado = tabelaFundos.getSelectionModel().getSelectedItem();
        if (fundoSelecionado == null){
            erroTroca.setText("Selecione um fundo na tabela primeiramente, por favor.");
            return;
        }
        try {
            if (valor <= 0){
                erroTroca.setText("Digite um valor válido, por favor.");
                return;
            }
            if (radioDeposito.isSelected()){
                fundoSelecionado.depositar(valor);
                erroTroca.setText("Depósito realizado com sucesso!");
            }
            else if (radioSaque.isSelected()){
                fundoSelecionado.sacar(valor);
                erroTroca.setText("Saque realizado com sucesso!");
            }
            else {
                erroTroca.setText("Selecione uma operação, por favor.");
            }
            PersistenciaJSON.salvar(usuarioAtual);
            campoValorOperacao.clear();
            //apenas atualiza os campos (dados de cada fundo). não precisa reconstruir a tabela que nem nas outras telas
            tabelaFundos.refresh();
        }
        catch (NumberFormatException e){
            erroTroca.setText("Digite um valor válido, por favor (Exemplo: 100.00).");
        }
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
