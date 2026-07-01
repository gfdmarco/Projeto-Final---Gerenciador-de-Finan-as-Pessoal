package gerenciador.sistema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.enums.Frequencia;
import gerenciador.exceptions.OrcamentoExcedidoException;
import gerenciador.exceptions.SaldoInsuficienteException;
import gerenciador.interfaces.UsuarioNecessario;
import gerenciador.operacoes.movimentacoes.Despesa;
import gerenciador.operacoes.movimentacoes.DespesaRecorrente;
import gerenciador.operacoes.movimentacoes.Receita;
import gerenciador.operacoes.movimentacoes.ReceitaRecorrente;
import gerenciador.operacoes.movimentacoes.Transacao;
import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;
import gerenciador.suporte.Tag;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

public class AdicionarTransacaoController implements UsuarioNecessario {

    @FXML private TextField campoValor;
    @FXML private TextField campoNome;
    @FXML private DatePicker campoData;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private ComboBox<Conta> comboConta;
    @FXML private ListView<Tag> listaTags;
    @FXML private RadioButton radioDespesa;
    @FXML private RadioButton radioReceita;
    @FXML private RadioButton radioUnica;
    @FXML private RadioButton radioSemanal;
    @FXML private RadioButton radioQuinzenal;
    @FXML private RadioButton radioMensal;
    @FXML private RadioButton radioTrimestral;
    @FXML private RadioButton radioSemestral;
    @FXML private RadioButton radioAnual;
    @FXML private Label labelErro;

    private Usuario usuarioAtual;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioAtual = usuario;

        this.usuarioAtual.setCategorias();
        this.usuarioAtual.setTags();
        
        comboCategoria.getItems().addAll(usuarioAtual.categoriasSistema());
        comboConta.getItems().addAll(usuarioAtual.getContas());

        listaTags.getItems().addAll(usuarioAtual.tagsSistema());
        listaTags.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    void onConfirmar() {

        Transacao novaTransacao = null;

        try {
            double valor = Double.parseDouble(campoValor.getText());
            String nome = campoNome.getText();
            LocalDate data = campoData.getValue();
            Categoria categoria = comboCategoria.getValue();
            Conta conta = comboConta.getValue();
            String id = UUID.randomUUID().toString();

            // precisamos ler as tags escolhidas
            ArrayList<Tag> tagsEscolhidas = new ArrayList<>(listaTags.getSelectionModel().getSelectedItems());

            boolean recorrente = radioUnica.isSelected() ? false : true;
            Frequencia frequencia = getFrequenciaSelecionada();

            if (recorrente && frequencia == null) {
                labelErro.setText("Por favor, selecione uma frequência.");
                return;
            }

            if (categoria == null || conta == null || data == null || nome == null || nome.isBlank()) {
                labelErro.setText("Preencha nome, data, categoria e conta antes de salvar.");
                return;
            }

            if (radioDespesa.isSelected()) {
                if (recorrente) {
                    novaTransacao = new DespesaRecorrente(nome, id, valor, tagsEscolhidas, categoria, data, conta, frequencia, data);
                } else {
                    novaTransacao = new Despesa(nome, id, valor, tagsEscolhidas, categoria, data, conta);
                }
            } else if (radioReceita.isSelected()) {
                if (recorrente) {
                    novaTransacao = new ReceitaRecorrente(nome, id, valor, tagsEscolhidas, categoria, data, conta, frequencia, data);
                } else {
                    novaTransacao = new Receita(nome, id, valor, tagsEscolhidas, categoria, data, conta);
                }
            } else {
                labelErro.setText("Por favor, selecione Receita ou Despesa.");
                return; // sai do método sem fazer nada
            }

            usuarioAtual.adicionarTransacao(novaTransacao);
            PersistenciaJSON.salvar(usuarioAtual);
            try {
                App.trocarTela("transacoes", usuarioAtual);
            }
            catch (Exception e){
                labelErro.setText("Erro ao trocar de tela");
            }
        }
        catch (NumberFormatException e) {
            labelErro.setText("Valor inválido.");
        }
        catch (SaldoInsuficienteException e) {
            labelErro.setText(e.getMessage());
        }
        catch (OrcamentoExcedidoException e) {
            confirmarOrcamentoExcedido(e, (Despesa) novaTransacao);
        }
    }

    private void confirmarOrcamentoExcedido(OrcamentoExcedidoException e, Despesa despesa) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Orçamento excedido");
        alert.setHeaderText(e.getMessage());
        alert.setContentText("Mesmo assim deseja realizar a transação?");

        ButtonType botaoSim = new ButtonType("Sim");
        ButtonType botaoNao = new ButtonType("Não");
        alert.getButtonTypes().setAll(botaoSim, botaoNao);

        alert.showAndWait().ifPresent(resposta -> {
            if (resposta == botaoSim) {
                try {
                    despesa.realizarTransacao(true); // ignora orçamento, mas ainda verifica saldo
                    usuarioAtual.adicionarTransacao(despesa);
                    PersistenciaJSON.salvar(usuarioAtual);
                    try {
                        App.trocarTela("transacoes", usuarioAtual);
                    }
                    catch (Exception e1){
                        labelErro.setText("Erro ao trocar de tela");
                    }
                } catch (SaldoInsuficienteException e2) {
                    labelErro.setText(e2.getMessage());
                }
            } else {
                labelErro.setText("Transação cancelada.");
            }
        });
    }

    private Frequencia getFrequenciaSelecionada() {
        if (radioSemanal.isSelected()){
            return Frequencia.SEMANAL;
        }
        if (radioQuinzenal.isSelected()){
            return Frequencia.QUINZENAL;
        }
        if (radioMensal.isSelected()){
            return Frequencia.MENSAL;
        }
        if (radioTrimestral.isSelected()){
            return Frequencia.TRIMESTRAL;
        }
        if (radioSemestral.isSelected()){
            return Frequencia.SEMESTRAL;
        }
        if (radioAnual.isSelected()){
            return Frequencia.ANUAL;
        }
        //caso de frequência única ou nenhuma seleção:
        return null;
    }

    @FXML
    void onVoltar(){
        try {
            App.trocarTela("transacoes", this.usuarioAtual);

        }
        catch (Exception e){
            labelErro.setText("Erro ao trocar de tela");
        }
    }
}