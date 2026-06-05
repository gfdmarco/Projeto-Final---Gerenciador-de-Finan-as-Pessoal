package gerenciador.exceptions;

public class SaldoInsuficienteException extends RuntimeException{
    public SaldoInsuficienteException(double saldo, double gasto){
        super("Gasto de " + gasto + " superior ao saldo! Seu saldo é de " + saldo + ".");
    }
}
