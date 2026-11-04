package br.com.prismasys.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String unidadeMedida;
    private Double precoUnitario;

    // --- Estoque (produto acabado) ---
    @Column(precision = 15, scale = 3)
    private BigDecimal estoqueAtual = BigDecimal.ZERO;

    // 0 = alerta desativado para este produto
    @Column(precision = 15, scale = 3)
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;

    public Produto() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public Double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(Double precoUnitario) { this.precoUnitario = precoUnitario; }

    public BigDecimal getEstoqueAtual() {
        return estoqueAtual == null ? BigDecimal.ZERO : estoqueAtual;
    }
    public void setEstoqueAtual(BigDecimal estoqueAtual) { this.estoqueAtual = estoqueAtual; }

    public BigDecimal getEstoqueMinimo() {
        return estoqueMinimo == null ? BigDecimal.ZERO : estoqueMinimo;
    }
    public void setEstoqueMinimo(BigDecimal estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    // Usado nas telas (Thymeleaf: produto.abaixoDoMinimo).
    public boolean isAbaixoDoMinimo() {
        return getEstoqueMinimo().signum() > 0
                && getEstoqueAtual().compareTo(getEstoqueMinimo()) <= 0;
    }
}