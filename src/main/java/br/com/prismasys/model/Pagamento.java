package br.com.prismasys.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    private Double valor;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPagamento;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    public Pagamento() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public Date getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(Date dataPagamento) { this.dataPagamento = dataPagamento; }

    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
}