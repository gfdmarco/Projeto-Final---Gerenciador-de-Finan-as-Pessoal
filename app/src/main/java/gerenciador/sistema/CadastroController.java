package gerenciador.sistema;

import gerenciador.base.GerenciadorUsuarios;
import gerenciador.exceptions.LoginExistenteException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CadastroController {
    //parte para o arquivo FXML injetar os ids nos atributos
    @FXML private TextField campoNome;
    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private Label labelErro;

    //chamado para executar a ação de cadastro a partir de quando o usuário aperta o botão correspondente
    @FXML
    void onCadastro(){
        try {
            GerenciadorUsuarios.cadastrarUsuario(campoNome.getText(), campoLogin.getText(), campoSenha.getText());
            try {
                App.trocarTela("login");
            }
            catch (Exception e){
                labelErro.setText("Erro ao trocar de tela");
            }
        }
        catch (LoginExistenteException e){
            labelErro.setText(e.getMessage());
        }
    }

    @FXML
    void onVoltar() {
        try {
            App.trocarTela("login");
        }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }
}
