package gerenciador.operacoes.movimentacoes;

import java.time.LocalDate;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;
import gerenciador.suporte.Tag;

//PARTE NECESSARIA PARA IDENTIFICAR QUAL TIPO DE TRANSACAO ESTA SENDO SALVA E CONSEGUIR CARREGAR/SALVAR CORRETAMENTE
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipoTransacao")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Receita.class, name = "receita"),
    @JsonSubTypes.Type(value = Despesa.class, name = "despesa"),
    @JsonSubTypes.Type(value = ReceitaRecorrente.class, name = "receitaRecorrente"),
    @JsonSubTypes.Type(value = DespesaRecorrente.class, name = "despesaRecorrente")
})
public abstract class Transacao {
    private String nome;
    private String id;
    private double valor;
    private ArrayList<Tag> tags;
    private Categoria categoria;
    private LocalDate data;
    private Conta contaAtrelada;

    //JSON precisa de um construtor padrão para conseguir construir os objetos quando carregar
    public Transacao(){
        this.tags = new ArrayList<>();
    }

    public Transacao(String nome, String id, double valor, ArrayList<Tag> tags, Categoria categoria, LocalDate data, Conta conta){
        this.nome = nome;
        this.id = id;
        this.valor = valor;
        this.tags = tags;
        this.categoria = categoria;
        this.data = data;
        this.contaAtrelada = conta;
    }

    public String getNome(){
        return this.nome;
    }
    public String getID(){
        return this.id;
    }

    public ArrayList<Tag> getTags(){
        return this.tags;
    }

    public Categoria getCategoria(){
        return this.categoria;
    }

    public LocalDate getData(){
        return this.data;
    }

    public Conta getConta(){
        return this.contaAtrelada;
    }

    public double getValor(){
        return this.valor;
    }

    public void setValor(double valor){
        if (valor <= 0){
            throw new IllegalArgumentException("O valor da transferência deve ser positivo.");
        }
        this.valor = valor;
    }

    public abstract void realizarTransacao();
}
