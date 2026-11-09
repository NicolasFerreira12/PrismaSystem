package br.com.prismasys.repository;

import br.com.prismasys.model.OrdemProducao;
import br.com.prismasys.model.StatusProducao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdemProducaoRepository extends JpaRepository<OrdemProducao, Long> {

    List<OrdemProducao> findAllByOrderByIdDesc();

    List<OrdemProducao> findByStatusOrderByIdDesc(StatusProducao status);

    // Trava a ordem durante a mudança de status (evita concluir duas vezes com clique duplo)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrdemProducao o where o.id = :id")
    Optional<OrdemProducao> findByIdForUpdate(@Param("id") Long id);
}