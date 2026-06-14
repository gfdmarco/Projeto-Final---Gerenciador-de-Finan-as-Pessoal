package gerenciador.sistema;

import gerenciador.base.Usuario;
import gerenciador.interfaces.UsuarioNecessario;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class TransacoesController implements UsuarioNecessario{
    
    private Usuario usuarioAtual;
    @FXML private TableView tabelaTransacoes;

    @Override
    public void setUsuario(Usuario usuario){
        this.usuarioAtual = usuario;
        
    }
}
