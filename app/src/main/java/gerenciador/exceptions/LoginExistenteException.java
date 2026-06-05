package gerenciador.exceptions;

public class LoginExistenteException extends RuntimeException{
    public LoginExistenteException(String mensagem){
        super(mensagem);
    }
}
