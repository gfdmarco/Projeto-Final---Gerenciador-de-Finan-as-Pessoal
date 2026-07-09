package gerenciador.fluxo;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import gerenciador.base.GerenciadorUsuarios;
import gerenciador.base.PersistenciaJSON;
import gerenciador.base.Usuario;
import gerenciador.exceptions.LoginExistenteException;
import gerenciador.exceptions.LoginInvalidoException;
import gerenciador.operacoes.reservas.Fundo;

// Cobre o cenario: Cadastrar usuario -> login -> dashboard
class FluxoCadastroLoginTest {

    private String login;

    @AfterEach
    void limparArquivoDeTeste() {
        if (login != null) {
            new File("dados", login + ".json").delete();
        }
    }

    @Test
    void cadastroLoginEDadosDoDashboardFuncionamSemCrash() {
        login = "teste_cadastro_" + UUID.randomUUID();
        Usuario cadastrado = GerenciadorUsuarios.cadastrarUsuario("Fulano de Tal", login, "senha123");
        assertNotNull(cadastrado);

        Usuario logado = GerenciadorUsuarios.autenticar(login, "senha123");
        assertEquals(login, logado.getLogin());

        // reproduz exatamente o que LoginController.onLogin() faz antes de trocar para o dashboard
        for (Fundo f : logado.getFundos()) {
            f.jurosCompostos();
        }
        List<String> avisos = logado.processarRecorrencias();
        logado.setCategorias();
        logado.setTags();
        PersistenciaJSON.salvar(logado);

        assertTrue(avisos.isEmpty(), "usuario recem-criado nao deve ter recorrencias pendentes");
        assertNotNull(logado.getContas());
        assertNotNull(logado.getFundos());
        assertEquals(0.0, logado.getSaldoGeral(), 0.0001);
    }

    @Test
    void loginComSenhaErradaDaErroTratadoNaoCrash() {
        login = "teste_senhaerrada_" + UUID.randomUUID();
        GerenciadorUsuarios.cadastrarUsuario("Fulano de Tal", login, "senhaCorreta");

        assertThrows(LoginInvalidoException.class, () -> GerenciadorUsuarios.autenticar(login, "senhaErrada"));
    }

    @Test
    void loginComLoginInexistenteDaErroTratadoNaoCrash() {
        assertThrows(LoginInvalidoException.class,
            () -> GerenciadorUsuarios.autenticar("login_que_nao_existe_" + UUID.randomUUID(), "qualquer"));
    }

    @Test
    void cadastroDuplicadoDaErroTratadoNaoCrash() {
        login = "teste_duplicado_" + UUID.randomUUID();
        GerenciadorUsuarios.cadastrarUsuario("Fulano", login, "senha123");

        assertThrows(LoginExistenteException.class,
            () -> GerenciadorUsuarios.cadastrarUsuario("Outro Nome", login, "outraSenha"));
    }
}
