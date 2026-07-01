package gerenciador.sistema;

import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.interfaces.UsuarioNecessario;
import gerenciador.suporte.Categoria;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ProgressBarTableCell;

public class CategoriasController implements UsuarioNecessario{

    @FXML private Label erroTroca;
    @FXML private TableView<Categoria> tabelaCategorias;
    @FXML private TableColumn<Categoria, String> colunaNome;
    @FXML private TableColumn<Categoria, Double> colunaOrcamento;
    @FXML private TableColumn<Categoria, Double> colunaRelacao;
    @FXML private TableColumn<Categoria, Integer> colunaTransacoes;

    @FXML private TextField campoNomeCriar;
    @FXML private TextField campoOrcamentoCriar;

    @FXML private TextField campoNomeProcurar;
    @FXML private TextField campoOrcamentoEditar;

    private Usuario usuarioAtual;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioAtual = usuario;

        this.usuarioAtual.setCategorias();
        this.usuarioAtual.setTags();

        colunaNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue() != null ? cell.getValue().getNome() : ""));
        colunaOrcamento.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue() != null ? cell.getValue().getOrcamento() : 0.0));
        colunaRelacao.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue() != null ? cell.getValue().getPercentualUso() : 0.0).asObject());
        colunaRelacao.setCellFactory(ProgressBarTableCell.forTableColumn());
        colunaTransacoes.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue() != null ? cell.getValue().getTransacoesAssociadas().size() : 0));

        tabelaCategorias.getItems().clear();
        tabelaCategorias.getItems().setAll(usuario.categoriasSistema());
    }

    @FXML
    void onCriar(){
        String nome = campoNomeCriar.getText();
        String orcamentoTexto = campoOrcamentoCriar.getText();

        if (nome == null || nome.trim().isEmpty()) {
            erroTroca.setText("Informe um nome de categoria válido.");
            return;
        }
        double orcamento;
        try {
            orcamento = Double.parseDouble(orcamentoTexto.trim());
        }
        catch (NumberFormatException e) {
            erroTroca.setText("Informe um valor de orçamento válido.");
            return;
        }

        Categoria novaCategoria = new Categoria(nome.trim(), orcamento);
        usuarioAtual.categoriasSistema().add(novaCategoria);
        PersistenciaJSON.salvar(usuarioAtual);

        tabelaCategorias.getItems().setAll(usuarioAtual.categoriasSistema());
        campoNomeCriar.clear();
        campoOrcamentoCriar.clear();
        erroTroca.setText("Categoria criada com sucesso.");
    }

    @FXML
    void onEditar(){
        String nome = campoNomeProcurar.getText();
        double novoOrcamento;
        try {
            novoOrcamento = Double.parseDouble(campoOrcamentoEditar.getText().trim());
        }
        catch (NumberFormatException e) {
            erroTroca.setText("Informe um valor de orçamento válido.");
            return;
        }

        Categoria c = this.usuarioAtual.buscarCategoria(nome);

        if (c != null){
            c.editarOrcamento(novoOrcamento);
            tabelaCategorias.getItems().setAll(usuarioAtual.categoriasSistema());
            campoNomeProcurar.clear();
            campoOrcamentoEditar.clear();
            erroTroca.setText("Orçamento atualizado com sucesso.");
        }
        else {
            erroTroca.setText("Categoria não encontrada. Digite um nome válido.");
        }
        PersistenciaJSON.salvar(usuarioAtual);
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
