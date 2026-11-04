package br.com.prismasys.repository;

import br.com.prismasys.model.Produto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Produto p where p.id = :id")
    Optional<Produto> findByIdForUpdate(@Param("id") Long id);

    @Query("select p from Produto p where p.estoqueMinimo > 0 and p.estoqueAtual <= p.estoqueMinimo order by p.nome")
    List<Produto> findAbaixoDoMinimo();
}