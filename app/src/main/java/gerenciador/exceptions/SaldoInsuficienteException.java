package gerenciador.exceptions;

public class SaldoInsuficienteException extends RuntimeException{
    public SaldoInsuficienteException(double saldo, double gasto){
        super("Saldo insuficiente para retirar R$ " + gasto + "! Seu saldo é de R$ " + saldo + ".");
    }
}
