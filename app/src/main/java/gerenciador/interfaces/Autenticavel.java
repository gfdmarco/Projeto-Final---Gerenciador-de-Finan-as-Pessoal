package gerenciador.interfaces;

import gerenciador.base.Usuario;

public interface Autenticavel {
    Usuario autenticar(String login, String senha);
}
