package br.com.prismasys.service;

import br.com.prismasys.model.MovimentacaoEstoque;
import br.com.prismasys.model.Pedido;
import br.com.prismasys.model.PedidoItem;
import br.com.prismasys.model.Produto;
import br.com.prismasys.model.TipoMovimentacao;
import br.com.prismasys.repository.MovimentacaoEstoqueRepository;
import br.com.prismasys.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class EstoqueService {

    private static final int ESCALA = 3;

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public EstoqueService(ProdutoRepository produtoRepository,
                          MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    // ---------------------------------------------------------------- operações

    @Transactional
    public MovimentacaoEstoque registrarEntrada(Long produtoId, BigDecimal quantidade,
                                                String motivo, String usuario) {
        BigDecimal qtd = validarQuantidade(quantidade);
        Produto produto = produtoComLock(produtoId);
        BigDecimal anterior = produto.getEstoqueAtual();
        return aplicar(produto, TipoMovimentacao.ENTRADA, qtd, anterior,
                anterior.add(qtd), motivo, usuario, null);
    }

    @Transactional
    public MovimentacaoEstoque registrarSaida(Long produtoId, BigDecimal quantidade,
                                              String motivo, String usuario) {
        BigDecimal qtd = validarQuantidade(quantidade);
        Produto produto = produtoComLock(produtoId);
        BigDecimal anterior = produto.getEstoqueAtual();
        verificarSaldo(produto, anterior, qtd);
        return aplicar(produto, TipoMovimentacao.SAIDA, qtd, anterior,
                anterior.subtract(qtd), motivo, usuario, null);
    }

    /** Ajuste de inventário: o usuário informa o saldo contado e o sistema calcula a diferença. */
    @Transactional
    public MovimentacaoEstoque ajustar(Long produtoId, BigDecimal saldoContado,
                                       String motivo, String usuario) {
        if (saldoContado == null || saldoContado.signum() < 0) {
            throw new IllegalArgumentException("Informe um saldo contado maior ou igual a zero.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Informe o motivo do ajuste.");
        }
        Produto produto = produtoComLock(produtoId);
        BigDecimal anterior = produto.getEstoqueAtual();
        BigDecimal novo = saldoContado.setScale(ESCALA, RoundingMode.HALF_UP);
        BigDecimal diferenca = novo.subtract(anterior).abs();
        if (diferenca.signum() == 0) {
            throw new IllegalArgumentException("O saldo informado é igual ao saldo atual.");
        }
        return aplicar(produto, TipoMovimentacao.AJUSTE, diferenca, anterior, novo, motivo, usuario, null);
    }

    @Transactional
    public Produto definirMinimo(Long produtoId, BigDecimal minimo) {
        if (minimo == null || minimo.signum() < 0) {
            throw new IllegalArgumentException("O estoque mínimo deve ser maior ou igual a zero.");
        }
        Produto produto = produtoComLock(produtoId);
        produto.setEstoqueMinimo(minimo.setScale(ESCALA, RoundingMode.HALF_UP));
        return produtoRepository.save(produto);
    }

    /**
     * Baixa automática ao emitir o pedido. Tudo ou nada: se algum item não tiver saldo,
     * lança EstoqueInsuficienteException e a transação inteira é desfeita.
     * Idempotente: chamar duas vezes para o mesmo pedido não baixa duas vezes.
     */
    @Transactional
    public void baixarPedido(Pedido pedido, String usuario) {
        if (movimentacaoRepository.existsByPedidoIdAndTipo(pedido.getId(), TipoMovimentacao.SAIDA)) {
            return;
        }

        // Soma por produto e trava em ordem de id (evita deadlock entre pedidos simultâneos)
        Map<Long, BigDecimal> totais = new TreeMap<>();
        for (PedidoItem item : pedido.getItens()) {
            if (item.getProduto() == null || item.getQuantidade() == null) {
                throw new IllegalStateException(
                        "O pedido #" + pedido.getId() + " tem um item sem produto ou sem quantidade.");
            }
            totais.merge(item.getProduto().getId(),
                    BigDecimal.valueOf(item.getQuantidade()),
                    BigDecimal::add);
        }

        for (Map.Entry<Long, BigDecimal> e : totais.entrySet()) {
            BigDecimal qtd = validarQuantidade(e.getValue());
            Produto produto = produtoComLock(e.getKey());
            BigDecimal anterior = produto.getEstoqueAtual();
            verificarSaldo(produto, anterior, qtd);
            aplicar(produto, TipoMovimentacao.SAIDA, qtd, anterior, anterior.subtract(qtd),
                    "Saída do pedido #" + pedido.getId(), usuario, pedido);
        }
    }

    // ----------------------------------------------------------------- consultas

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoque> historico(Long produtoId) {
        return movimentacaoRepository.findByProdutoIdOrderByDataDesc(produtoId);
    }

    /** Consultar saída de Produto: produtoId pode ser null (todos os produtos). */
    @Transactional(readOnly = true)
    public List<MovimentacaoEstoque> consultarSaidas(Long produtoId, LocalDate inicio, LocalDate fim) {
        return movimentacaoRepository.buscar(TipoMovimentacao.SAIDA, produtoId,
                inicio.atStartOfDay(), fim.plusDays(1).atStartOfDay());
    }

    @Transactional(readOnly = true)
    public List<Produto> produtosAbaixoDoMinimo() {
        return produtoRepository.findAbaixoDoMinimo();
    }

    // ------------------------------------------------------------------- internos

    private MovimentacaoEstoque aplicar(Produto produto, TipoMovimentacao tipo, BigDecimal quantidade,
                                        BigDecimal anterior, BigDecimal novo,
                                        String motivo, String usuario, Pedido pedido) {
        produto.setEstoqueAtual(novo);
        produtoRepository.save(produto);
        return movimentacaoRepository.save(new MovimentacaoEstoque(
                produto, tipo, quantidade, anterior, novo, motivo, usuario, pedido));
    }

    private Produto produtoComLock(Long id) {
        return produtoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }

    private BigDecimal validarQuantidade(BigDecimal quantidade) {
        if (quantidade == null || quantidade.signum() <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        return quantidade.setScale(ESCALA, RoundingMode.HALF_UP);
    }

    private void verificarSaldo(Produto produto, BigDecimal disponivel, BigDecimal solicitado) {
        if (disponivel.compareTo(solicitado) < 0) {
            throw new EstoqueInsuficienteException(produto.getNome(), disponivel, solicitado);
        }
    }
}