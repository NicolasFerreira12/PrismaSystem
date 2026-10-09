package br.com.prismasys.dto;

public class SaidaProdutoLinha {

    private final String produto;
    private final String unidade;
    private int quantidade;
    private double valorTotal;

    public SaidaProdutoLinha(String produto, String unidade) {
        this.produto = produto;
        this.unidade = unidade;
    }

    public void adicionar(int quantidade, double valor) {
        this.quantidade += quantidade;
        this.valorTotal += valor;
    }

    public String getProduto() { return produto; }
    public String getUnidade() { return unidade; }
    public int getQuantidade() { return quantidade; }
    public double getValorTotal() { return valorTotal; }
}