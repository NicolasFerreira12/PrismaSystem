package br.com.prismasys.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    private Double valorTotal;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEmissao;

    private String chaveAcesso;

    public NotaFiscal() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /** O número da nota é o próprio ID sequencial. */
    public Long getNumero() { return id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public Date getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(Date dataEmissao) { this.dataEmissao = dataEmissao; }

    public String getChaveAcesso() { return chaveAcesso; }
    public void setChaveAcesso(String chaveAcesso) { this.chaveAcesso = chaveAcesso; }
}