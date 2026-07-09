package gerenciador.sistema;

import java.util.List;

import gerenciador.base.GerenciadorUsuarios;
import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.exceptions.LoginInvalidoException;
import gerenciador.operacoes.reservas.Fundo;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    //parte para o arquivo FXML injetar os ids nos atributos
    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private Label labelErro;


    //chamado para executar a ação de login a partir de quando o usuário aperta o botão correspondente
    @FXML
    void onLogin(){
        try {
            labelErro.setText("");
            Usuario u = GerenciadorUsuarios.autenticar(campoLogin.getText(), campoSenha.getText());
            for (Fundo f : u.getFundos()){
                f.jurosCompostos();
            }
            List<String> avisos = u.processarRecorrencias();
            u.setCategorias();
            u.setTags();
            PersistenciaJSON.salvar(u);
            if (!avisos.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Recorrências com pendências");
                alert.setContentText(String.join("\n", avisos));
                alert.showAndWait();
            }
            try {
                App.trocarTela("dashboard", u);
            }
            catch (Exception e){
                labelErro.setText("Erro ao trocar de tela");
            }
        }
        catch (LoginInvalidoException e){
            labelErro.setText(e.getMessage());
        }
    }

    //possibilidade de troca de tela com uso de botao para a tela de cadastro
    @FXML
    void onCadastrar() {
        try {
            App.trocarTela("cadastro");
            }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }
}