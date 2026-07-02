package gerenciador.exceptions;

public class LoginExistenteException extends RuntimeException{
    public LoginExistenteException(String login){
        super("O login " + login + "já existe! Tente um novo nome de usuário.");
    }
}
