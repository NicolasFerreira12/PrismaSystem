package br.com.prismasys.service;

import br.com.prismasys.model.OrdemProducao;
import br.com.prismasys.model.Produto;
import br.com.prismasys.model.StatusProducao;
import br.com.prismasys.repository.OrdemProducaoRepository;
import br.com.prismasys.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProducaoService {

    private static final int ESCALA = 3;

    private final OrdemProducaoRepository ordemRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;

    public ProducaoService(OrdemProducaoRepository ordemRepository,
                           ProdutoRepository produtoRepository,
                           EstoqueService estoqueService) {
        this.ordemRepository = ordemRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueService = estoqueService;
    }

    @Transactional
    public OrdemProducao criar(Long produtoId, BigDecimal quantidade, LocalDate dataPrevista,
                               String observacao, String usuario) {
        if (produtoId == null) {
            throw new IllegalArgumentException("Selecione o produto.");
        }
        if (quantidade == null || quantidade.signum() <= 0) {
            throw new IllegalArgumentException("A quantidade planejada deve ser maior que zero.");
        }
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));
        return ordemRepository.save(new OrdemProducao(produto,
                quantidade.setScale(ESCALA, RoundingMode.HALF_UP), dataPrevista, observacao, usuario));
    }

    @Transactional
    public void iniciar(Long id) {
        ordemComLock(id).iniciar();
    }

    /**
     * Conclui a ordem e dá entrada do produto acabado no estoque, na mesma transação:
     * se a entrada falhar, a ordem continua "Em produção".
     */
    @Transactional
    public void concluir(Long id, BigDecimal quantidadeProduzida, String usuario) {
        OrdemProducao ordem = ordemComLock(id);
        if (ordem.getStatus() != StatusProducao.EM_PRODUCAO) {
            throw new IllegalStateException("Só é possível concluir uma ordem em produção (situação atual: "
                    + ordem.getStatus().getDescricao() + ").");
        }
        if (quantidadeProduzida == null || quantidadeProduzida.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Informe a quantidade produzida (maior que zero). Para descartar a ordem, use Cancelar.");
        }
        BigDecimal produzida = quantidadeProduzida.setScale(ESCALA, RoundingMode.HALF_UP);
        estoqueService.registrarEntrada(ordem.getProduto().getId(), produzida,
                "Produção #" + ordem.getId(), usuario);
        ordem.concluir(produzida, usuario);
    }

    @Transactional
    public void cancelar(Long id, String usuario) {
        ordemComLock(id).cancelar(usuario);
    }

    @Transactional(readOnly = true)
    public OrdemProducao buscar(Long id) {
        return ordemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ordem de produção não encontrada: #" + id));
    }

    @Transactional(readOnly = true)
    public List<OrdemProducao> listar(StatusProducao status) {
        return status == null
                ? ordemRepository.findAllByOrderByIdDesc()
                : ordemRepository.findByStatusOrderByIdDesc(status);
    }

    private OrdemProducao ordemComLock(Long id) {
        return ordemRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NoSuchElementException("Ordem de produção não encontrada: #" + id));
    }
}