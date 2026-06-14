package gerenciador.sistema;

import gerenciador.base.GerenciadorUsuarios;
import gerenciador.base.Usuario;
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

    private GerenciadorUsuarios gerenciador = new GerenciadorUsuarios();

    //chamado para executar a ação de cadastro a partir de quando o usuário aperta o botão correspondente
    @FXML 
    void onCadastro(){
        try {
            Usuario u = gerenciador.cadastrarUsuario(campoNome.getText(), campoLogin.getText(), campoSenha.getText());
            App.trocarTela("login", u);
        }
        catch (LoginExistenteException e){
            labelErro.setText(e.getMessage());
        }
    }
}
