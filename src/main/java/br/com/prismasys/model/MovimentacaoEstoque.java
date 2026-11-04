package br.com.prismasys.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao_estoque",
        indexes = @Index(name = "idx_mov_produto_data", columnList = "produto_id, data"))
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimentacao tipo;

    // Sempre positiva. Em AJUSTE, é o tamanho da diferença (o sentido sai de saldoAnterior x saldoApos).
    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidade;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal saldoAnterior;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal saldoApos;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(length = 255)
    private String motivo;

    @Column(length = 100)
    private String usuario;

    // Preenchido nas saídas geradas automaticamente por um pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    protected MovimentacaoEstoque() {
    }

    public MovimentacaoEstoque(Produto produto, TipoMovimentacao tipo, BigDecimal quantidade,
                               BigDecimal saldoAnterior, BigDecimal saldoApos,
                               String motivo, String usuario, Pedido pedido) {
        this.produto = produto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.saldoAnterior = saldoAnterior;
        this.saldoApos = saldoApos;
        this.motivo = motivo;
        this.usuario = usuario;
        this.pedido = pedido;
        this.data = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Produto getProduto() { return produto; }
    public TipoMovimentacao getTipo() { return tipo; }
    public BigDecimal getQuantidade() { return quantidade; }
    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public BigDecimal getSaldoApos() { return saldoApos; }
    public LocalDateTime getData() { return data; }
    public String getMotivo() { return motivo; }
    public String getUsuario() { return usuario; }
    public Pedido getPedido() { return pedido; }
}