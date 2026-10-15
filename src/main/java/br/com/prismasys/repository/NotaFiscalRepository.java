package br.com.prismasys.repository;

import br.com.prismasys.model.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {

    boolean existsByPedidoId(Long pedidoId);
}