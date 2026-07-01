package gerenciador.sistema;

import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.suporte.Conta;
import gerenciador.interfaces.UsuarioNecessario;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class DashboardController implements UsuarioNecessario {

    private Usuario usuarioAtual;
    @FXML private Label labelSaldoGeral;
    @FXML private Label labelNome;
    @FXML private Label labelErro;

    //parte da exibição das contas
    @FXML private TableView<Conta> tabelaContas;
    @FXML private TableColumn<Conta, String> colunaNome;
    @FXML private TableColumn<Conta, Double> colunaMontante;
    @FXML private TableColumn<Conta, String> colunaID;
    @FXML private TableColumn<Conta, Integer> colunaTransacoes;

    @Override
    public void setUsuario(Usuario usuario){
        this.usuarioAtual = usuario;
        labelNome.setText("Olá, " + usuarioAtual.getNome() + "! Seja bem-vindo ao menu do seu Gerenciador de Finanças Pessoais!");
        labelSaldoGeral.setText("R$ " + String.format("%.2f", usuario.getSaldoGeral()));

        colunaNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue() != null ? cell.getValue().getBanco() : ""));
        colunaMontante.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue() != null ? cell.getValue().getMontante() : 0.0));
        colunaID.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue() != null ? cell.getValue().getID() : ""));
        colunaTransacoes.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue() != null ? cell.getValue().getTransacoesAssociadas().size() : 0));

        tabelaContas.getItems().clear();
        tabelaContas.getItems().addAll(usuario.getContas());
    }

    @FXML
    void onDadosPessoais(){
        try {
            App.trocarTela("dadosPessoais", this.usuarioAtual);
        }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }

    @FXML
    void onTransacoes(){
        try {
            App.trocarTela("transacoes", this.usuarioAtual);
        }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }

    @FXML
    void onCategorias(){
        try {
            App.trocarTela("categorias", this.usuarioAtual);
        }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }

    @FXML
    void onRelatorios(){
        try {
            App.trocarTela("relatorios", this.usuarioAtual);
        }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }

    @FXML
    void onFundos(){
        try {
            App.trocarTela("fundos", this.usuarioAtual);
        }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }

    @FXML
    void onContas(){
        try {
            App.trocarTela("contas", this.usuarioAtual);
        }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }

    @FXML
    void onTags(){
        try {
            App.trocarTela("tags", this.usuarioAtual);
        }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }

    @FXML
    void onSair(){
        PersistenciaJSON.salvar(this.usuarioAtual);
        Platform.exit();
    }
}
