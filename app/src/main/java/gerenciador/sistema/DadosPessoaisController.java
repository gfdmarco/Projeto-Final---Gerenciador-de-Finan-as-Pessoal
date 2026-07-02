package gerenciador.sistema;

import java.time.LocalDate;
import java.util.UUID;

import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.interfaces.UsuarioNecessario;
import gerenciador.suporte.Conta;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class DadosPessoaisController implements UsuarioNecessario{
    
    @FXML private Label labelNome;
    @FXML private Label labelLogin;
    @FXML private Label labelSalario;

    @FXML private TextField setaSalario;
    @FXML private TextField contaSalario;
    @FXML private DatePicker dataSalario;

    @FXML private TextField editaSalario;

    @FXML private ListView<Conta> listaContas;
    @FXML private Label labelErro;

    private Usuario usuarioAtual;

    @Override
    public void setUsuario(Usuario usuario){
        this.usuarioAtual = usuario;
        labelNome.setText(usuario.getNome());
        labelLogin.setText(usuario.getLogin());
        labelSalario.setText(usuario.getSalario() > 0.0 ? "R$ " + usuario.getSalario() : "Não registrado");
        listaContas.getItems().clear();
        listaContas.getItems().addAll(usuario.getContas());
    }

    @FXML
    void onRegistrarSalario(){
        String textoSalario = setaSalario.getText();
        LocalDate data = dataSalario.getValue();
        if (data == null){
            labelErro.setText("Por favor, insira uma data.");
            return;
        }
        try {
            double salario = Double.parseDouble(textoSalario);
            Conta contaDoSalario = null;
            for (Conta c : usuarioAtual.getContas()){
                if (c.getBanco().equals(contaSalario.getText())){
                    contaDoSalario = c;
                    break;
                }
            }
            if (contaDoSalario == null){
                String id = UUID.randomUUID().toString();
                //abrimos a conta com montante inicial nulo para evitar duplicar salario (ja sera creditado em registrarSalario)
                contaDoSalario = this.usuarioAtual.abrirConta(contaSalario.getText(), id, 0);
                listaContas.getItems().add(contaDoSalario);
            }
            usuarioAtual.registrarSalario(contaDoSalario, salario, data);
            PersistenciaJSON.salvar(usuarioAtual);
            labelSalario.setText("R$ " + String.format("%.2f", salario));

        }
        catch (NumberFormatException e){
            labelErro.setText("Digite um valor válido, por favor.");
        }
    }

    @FXML
    void onEditarSalario(){
        String textoNovoSalario = editaSalario.getText();
        try {
            double salario = Double.parseDouble(textoNovoSalario);
            this.usuarioAtual.editarSalario(salario);
            PersistenciaJSON.salvar(usuarioAtual);
            listaContas.refresh();
            labelSalario.setText("R$ " + String.format("%.2f", salario));
        }
        catch (NumberFormatException e){
            labelErro.setText("Digite um valor válido, por favor.");
        }
    }

    @FXML
    void onRemoverSalario(){
        try {
            this.usuarioAtual.removerSalario();
            PersistenciaJSON.salvar(usuarioAtual);
            listaContas.refresh();
            labelSalario.setText("Não registrado");
        }
        catch (Exception e){
            labelErro.setText("Não foi possível remover o salário.");
        }
    }

    @FXML
    void onVoltar(){
        try {
            App.trocarTela("dashboard", usuarioAtual);
        }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }
}
