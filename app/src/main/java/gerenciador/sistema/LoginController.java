package gerenciador.sistema;

import gerenciador.base.GerenciadorUsuarios;
import gerenciador.base.Usuario;
import gerenciador.exceptions.LoginInvalidoException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    //parte para o arquivo FXML injetar os ids nos atributos
    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private Label labelErro;

    private GerenciadorUsuarios gerenciador = new GerenciadorUsuarios();

    //chamado para executar a ação de login a partir de quando o usuário aperta o botão correspondente
    @FXML 
    void onLogin(){
        try {
            labelErro.setText("");
            Usuario u = gerenciador.autenticar(campoLogin.getText(), campoSenha.getText());
            App.trocarTela("dashboard", u);
        }
        catch (LoginInvalidoException e){
            labelErro.setText(e.getMessage());
        }
    }

    //possibilidade de troca de tela com uso de botao para a tela de cadastro
    @FXML
    void onTelaCadastro() {
        App.trocarTela("cadastro"); //talvez precise passar o usuário
    }
}