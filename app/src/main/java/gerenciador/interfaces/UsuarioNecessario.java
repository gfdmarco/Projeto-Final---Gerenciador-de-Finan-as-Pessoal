package gerenciador.interfaces;

import gerenciador.base.Usuario;

public interface UsuarioNecessario {
    //no front, as classes controller que precisarem do estado do usuario precisarão implementar a interface aqui descrita
    void setUsuario(Usuario usuario);
}
