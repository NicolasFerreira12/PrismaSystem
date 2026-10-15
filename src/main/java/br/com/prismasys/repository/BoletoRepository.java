package br.com.prismasys.repository;

import br.com.prismasys.model.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoletoRepository extends JpaRepository<Boleto, Long> {

    boolean existsByPedidoId(Long pedidoId);
}