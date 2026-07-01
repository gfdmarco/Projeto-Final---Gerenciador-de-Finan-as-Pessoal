package gerenciador.sistema;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import gerenciador.base.Usuario;
import gerenciador.interfaces.UsuarioNecessario;
import gerenciador.operacoes.relatorios.RelatorioCategoria;
import gerenciador.operacoes.relatorios.RelatorioPeriodo;
import gerenciador.suporte.Categoria;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RelatoriosController implements UsuarioNecessario {

    @FXML private Label erroTroca;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private Button botaoGerarCategoria;
    @FXML private DatePicker campoInicio;
    @FXML private DatePicker campoFim;
    @FXML private Button botaoGerarPeriodo;
    @FXML private TextArea areaRelatorio;
    @FXML private Button botaoVoltar;

    private Usuario usuarioAtual;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioAtual = usuario;
        this.usuarioAtual.setCategorias();
        comboCategoria.setItems(FXCollections.observableArrayList(usuarioAtual.categoriasSistema()));
    }

    @FXML
    void onGerarCategoria() {
        Categoria categoria = comboCategoria.getValue();
        if (categoria == null) {
            erroTroca.setText("Selecione uma categoria.");
            return;
        }
        String resultado = new RelatorioCategoria(categoria).gerar(usuarioAtual.getHistorico(), usuarioAtual);
        areaRelatorio.setText(resultado);
        erroTroca.setText("");
    }

    @FXML
    void onGerarPeriodo() {
        LocalDate inicio = campoInicio.getValue();
        LocalDate fim = campoFim.getValue();
        if (inicio == null || fim == null) {
            erroTroca.setText("Selecione datas de início e fim.");
            return;
        }
        if (fim.isBefore(inicio)) {
            erroTroca.setText("A data de fim deve ser igual ou posterior à data de início.");
            return;
        }
        String resultado = new RelatorioPeriodo(inicio, fim).gerar(usuarioAtual.getHistorico(), usuarioAtual);
        areaRelatorio.setText(resultado);
        erroTroca.setText("");
    }

    @FXML
    void onVoltar() {
        try {
            App.trocarTela("dashboard", usuarioAtual);
        } catch (Exception e) {
            erroTroca.setText("Erro ao trocar de tela");
        }
    }
}
