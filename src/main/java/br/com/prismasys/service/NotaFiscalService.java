package br.com.prismasys.service;

import br.com.prismasys.model.NotaFiscal;
import br.com.prismasys.model.Pedido;
import br.com.prismasys.repository.NotaFiscalRepository;
import br.com.prismasys.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotaFiscalService {

    @Autowired
    private NotaFiscalRepository repository;

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<NotaFiscal> listar() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(NotaFiscal::getId).reversed())
                .collect(Collectors.toList());
    }

    public Optional<NotaFiscal> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /** Pedidos PAGOS que ainda não têm nota fiscal. */
    public List<Pedido> pedidosElegiveis() {
        return pedidoRepository.findAll().stream()
                .filter(p -> "PAGO".equals(p.getStatus()))
                .filter(p -> !repository.existsByPedidoId(p.getId()))
                .sorted(Comparator.comparing(Pedido::getId).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public NotaFiscal emitir(Long pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("Selecione um pedido.");
        }
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
        if (!"PAGO".equals(pedido.getStatus())) {
            throw new IllegalArgumentException("Só é possível emitir nota fiscal de pedido PAGO.");
        }
        if (repository.existsByPedidoId(pedidoId)) {
            throw new IllegalArgumentException("Já existe uma nota fiscal para este pedido.");
        }

        NotaFiscal nota = new NotaFiscal();
        nota.setPedido(pedido);
        nota.setValorTotal(pedido.getValorTotal());
        nota.setDataEmissao(new Date());
        nota = repository.save(nota);

        nota.setChaveAcesso(montarChaveAcesso(nota));
        return repository.save(nota);
    }

    /** Chave de acesso SIMULADA (44 dígitos montados com os IDs) — não é uma chave fiscal válida. */
    private String montarChaveAcesso(NotaFiscal nota) {
        String base = "26"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMM"))
                + String.format("%014d", nota.getPedido().getId())
                + String.format("%09d", nota.getId());
        return String.format("%-44s", base).replace(' ', '0');
    }
}