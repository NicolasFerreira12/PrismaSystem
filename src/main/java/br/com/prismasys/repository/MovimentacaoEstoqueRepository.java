package br.com.prismasys.repository;

import br.com.prismasys.model.MovimentacaoEstoque;
import br.com.prismasys.model.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByProdutoIdOrderByDataDesc(Long produtoId);

    // Evita baixar o estoque duas vezes para o mesmo pedido
    boolean existsByPedidoIdAndTipo(Long pedidoId, TipoMovimentacao tipo);

    // Base do "Consultar saída de Produto" (produtoId opcional)
    @Query("""
            select m from MovimentacaoEstoque m
            where m.tipo = :tipo
              and (:produtoId is null or m.produto.id = :produtoId)
              and m.data >= :inicio and m.data < :fim
            order by m.data desc
            """)
    List<MovimentacaoEstoque> buscar(@Param("tipo") TipoMovimentacao tipo,
                                     @Param("produtoId") Long produtoId,
                                     @Param("inicio") LocalDateTime inicio,
                                     @Param("fim") LocalDateTime fim);
}