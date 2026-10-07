package br.com.prismasys.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class ContaPagarReceber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoConta tipo;

    private Double valor;

    @Temporal(TemporalType.DATE)
    private Date dataVencimento;

    @Temporal(TemporalType.DATE)
    private Date dataPagamento;

    @Enumerated(EnumType.STRING)
    private StatusConta status;

    public ContaPagarReceber() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public TipoConta getTipo() { return tipo; }
    public void setTipo(TipoConta tipo) { this.tipo = tipo; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public Date getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(Date dataVencimento) { this.dataVencimento = dataVencimento; }

    public Date getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(Date dataPagamento) { this.dataPagamento = dataPagamento; }

    public StatusConta getStatus() { return status; }
    public void setStatus(StatusConta status) { this.status = status; }
}