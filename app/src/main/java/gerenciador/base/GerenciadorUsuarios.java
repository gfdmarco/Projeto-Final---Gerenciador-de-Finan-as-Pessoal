package gerenciador.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import gerenciador.interfaces.Autenticavel;
import gerenciador.suporte.HistoricoTransacoes;
import gerenciador.exceptions.*;

public class GerenciadorUsuarios implements Autenticavel {

    public static Usuario cadastrarUsuario(String nome, String login, String senha){

        if (PersistenciaJSON.usuarioExistente(login)){
            throw new LoginExistenteException(login);
        }
        String senhaHasheada = HashSenhas.gerarHash(senha);
        Usuario u = new Usuario(nome, login, senhaHasheada);

        PersistenciaJSON.salvar(u);
        
        return u;
    }

    @Override
    public Usuario autenticar(String login, String senha){

        if (!PersistenciaJSON.usuarioExistente(login)){
            throw new LoginInvalidoException(login, senha);
        }

        Usuario u = PersistenciaJSON.carregar(login);
        String senhaAtualHasheada = HashSenhas.gerarHash(senha);

        boolean valido = false;
        if (u.getSenhaHasheada().equals(senhaAtualHasheada)){
            valido = true;
        }
        if (!valido){
            throw new LoginInvalidoException(login, senha);
        }
        return u;
    }
}
