package gerenciador.sistema;

import gerenciador.base.Usuario;
import gerenciador.interfaces.UsuarioNecessario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TransacoesController implements UsuarioNecessario{
    
    private Usuario usuarioAtual;
    @FXML private Label erroTroca;

    @Override
    public void setUsuario(Usuario usuario){
        this.usuarioAtual = usuario;
    }

    @FXML
    void onAdicionar() {
        //corrigir os demais pra ficar igual esse em TODOS os arquivos
        try {
            App.trocarTela("adicionarTransacao", this.usuarioAtual);

        }
        catch (Exception e){
            erroTroca.setText("Erro ao trocar de tela");
        }
    }

    @FXML
    void onRemover(){
        try {
            App.trocarTela("removerTransacao", this.usuarioAtual);

        }
        catch (Exception e){
            erroTroca.setText("Erro ao trocar de tela");
        }
    }

    @FXML
    void onBuscar(){
        try {
            App.trocarTela("buscarMenu", this.usuarioAtual);

        }
        catch (Exception e){
            erroTroca.setText("Erro ao trocar de tela");
        }
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
