package gerenciador.exceptions;

import gerenciador.suporte.Categoria;

public class OrcamentoExcedidoException extends RuntimeException{
    public OrcamentoExcedidoException(Categoria categoria, double valor){
        super("Este gasto supera o orçamento para a categoria " + categoria.getNome() + "! O orçamento definido foi de " + 
        categoria.getOrcamento() + ".");
    }
}
