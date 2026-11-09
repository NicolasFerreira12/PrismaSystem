package br.com.prismasys.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordem_producao")
public class OrdemProducao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidadePlanejada;

    // Preenchida só na conclusão (pode ser diferente da planejada)
    @Column(precision = 15, scale = 3)
    private BigDecimal quantidadeProduzida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusProducao status;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDate dataPrevista;
    private LocalDateTime dataInicio;
    private LocalDateTime dataEncerramento; // conclusão ou cancelamento

    @Column(length = 255)
    private String observacao;

    @Column(length = 100)
    private String criadaPor;

    @Column(length = 100)
    private String encerradaPor;

    protected OrdemProducao() {
    }

    public OrdemProducao(Produto produto, BigDecimal quantidadePlanejada, LocalDate dataPrevista,
                         String observacao, String criadaPor) {
        this.produto = produto;
        this.quantidadePlanejada = quantidadePlanejada;
        this.dataPrevista = dataPrevista;
        this.observacao = observacao;
        this.criadaPor = criadaPor;
        this.status = StatusProducao.PLANEJADA;
        this.dataCriacao = LocalDateTime.now();
    }

    // ------------------------------------------------------------ transições de status

    public void iniciar() {
        if (status != StatusProducao.PLANEJADA) {
            throw new IllegalStateException("Só é possível iniciar uma ordem planejada (situação atual: "
                    + status.getDescricao() + ").");
        }
        this.status = StatusProducao.EM_PRODUCAO;
        this.dataInicio = LocalDateTime.now();
    }

    public void concluir(BigDecimal produzida, String usuario) {
        if (status != StatusProducao.EM_PRODUCAO) {
            throw new IllegalStateException("Só é possível concluir uma ordem em produção (situação atual: "
                    + status.getDescricao() + ").");
        }
        this.quantidadeProduzida = produzida;
        this.status = StatusProducao.CONCLUIDA;
        this.dataEncerramento = LocalDateTime.now();
        this.encerradaPor = usuario;
    }

    public void cancelar(String usuario) {
        if (status == StatusProducao.CONCLUIDA || status == StatusProducao.CANCELADA) {
            throw new IllegalStateException("Não é possível cancelar uma ordem "
                    + status.getDescricao().toLowerCase() + ".");
        }
        this.status = StatusProducao.CANCELADA;
        this.dataEncerramento = LocalDateTime.now();
        this.encerradaPor = usuario;
    }

    public Long getId() { return id; }
    public Produto getProduto() { return produto; }
    public BigDecimal getQuantidadePlanejada() { return quantidadePlanejada; }
    public BigDecimal getQuantidadeProduzida() { return quantidadeProduzida; }
    public StatusProducao getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDate getDataPrevista() { return dataPrevista; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataEncerramento() { return dataEncerramento; }
    public String getObservacao() { return observacao; }
    public String getCriadaPor() { return criadaPor; }
    public String getEncerradaPor() { return encerradaPor; }
}