package gerenciador.operacoes.movimentacoes;

import java.time.LocalDate;
import java.util.ArrayList;

import gerenciador.suporte.Categoria;
import gerenciador.suporte.Conta;
import gerenciador.suporte.Tag;

public abstract class Transacao {
    protected String nome;
    protected String id;
    protected double valor;
    protected ArrayList<Tag> tags;
    protected Categoria categoria;
    protected LocalDate data;
    protected Conta contaAtrelada;

    public Transacao(String nome, String id, double valor, ArrayList<Tag> tags, Categoria categoria, LocalDate data, Conta conta){
        this.nome = nome;
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

    public abstract void realizarTransacao();
}
