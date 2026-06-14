package gerenciador.sistema;

import java.util.ArrayList;

import gerenciador.base.Usuario;
import gerenciador.interfaces.UsuarioNecessario;
import gerenciador.suporte.Conta;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController implements UsuarioNecessario{
    private Usuario usuarioAtual;
    @FXML Label labelSaldoGeral;
    @FXML TableView<Conta> tabelaContas;
    @FXML TableView<Conta, String> colunaNome;
    @FXML TableView<Conta, Double> colunaSaldo;

    @Override
    public void setUsuario(Usuario usuario){
        this.usuarioAtual = usuario;
        
    }

    @FXML
    public void exibir(){
        // SALDO GERAL
        labelSaldoGeral.setText(String.valueOf(usuarioAtual.getSaldoGeral()));

        // SALDO DAS CONTAS
        ArrayList<Conta> contas = this.usuarioAtual.getContas();

        colunaNome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        colunaSaldo.setCellValueFactory(new PropertyValueFactory<>("Saldo"));

        tabelaContas.setItems(contas);
    }

    @FXML
    public void onTransacoes(){
        App.trocarTela("transacoes", this.usuarioAtual);
    }

    @FXML
    public void onMetas(){
        App.trocarTela("metas", this.usuarioAtual);
    }

    @FXML
    public void onCategorias(){
        App.trocarTela("categorias", this.usuarioAtual);
    }

    @FXML
    public void onRelatorios(){
        App.trocarTela("relatorios", this.usuarioAtual);
    }

    @FXML
    public void onFundos(){
        App.trocarTela("fundos", this.usuarioAtual);
    }
}
