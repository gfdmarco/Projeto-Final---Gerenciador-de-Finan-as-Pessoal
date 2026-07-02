package gerenciador.exceptions;

public class LoginInvalidoException extends RuntimeException{
    public LoginInvalidoException(String login, String senha){
        super("O login " + login + " não existe ou a senha digitada é inválida. Cadastre-se ou tente outra senha");
    }
}
