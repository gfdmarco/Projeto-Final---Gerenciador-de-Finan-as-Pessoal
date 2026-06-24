package gerenciador.sistema;

import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.interfaces.UsuarioNecessario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController implements UsuarioNecessario {

    private Usuario usuarioAtual;
    @FXML private Label labelSaldoGeral;
    @FXML private Label labelNome;
    @FXML private Label labelErro;

    @Override
    public void setUsuario(Usuario usuario){
        this.usuarioAtual = usuario;
        labelNome.setText("Olá, " + usuarioAtual.getNome() + "! Seja bem-vindo ao menu do seu Gerenciador de Finanças Pessoais!");
        labelSaldoGeral.setText("Seu saldo geral: R$ " + String.format("%.2f", usuario.getSaldoGeral()));
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
    void onMetas(){
        try {
            App.trocarTela("metas", this.usuarioAtual);
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
    void onSair(){
        PersistenciaJSON.salvar(this.usuarioAtual);
        Platform.exit();
    }
}
