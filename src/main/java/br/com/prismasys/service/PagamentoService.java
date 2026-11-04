package br.com.prismasys.service;

import br.com.prismasys.model.*;
import br.com.prismasys.repository.PagamentoRepository;
import br.com.prismasys.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private EstoqueService estoqueService;

    public Optional<Pagamento> buscarPorPedido(Long pedidoId) {
        return pagamentoRepository.findByPedidoId(pedidoId);
    }

    @Transactional
    public Pagamento processar(Long pedidoId, FormaPagamento forma) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        // reaproveita o registro de uma tentativa recusada; pedido já pago não pode pagar de novo
        Pagamento pagamento = pagamentoRepository.findByPedidoId(pedidoId).orElseGet(Pagamento::new);
        if (pagamento.getStatus() == StatusPagamento.APROVADO) {
            throw new IllegalArgumentException("Este pedido já está pago.");
        }

        StatusPagamento status = StatusPagamento.APROVADO;

        if (forma == FormaPagamento.CREDITO) {
            Double limite = pedido.getCliente().getLimiteCredito();
            if (limite == null || limite < pedido.getValorTotal()) {
                status = StatusPagamento.RECUSADO;
            }
        }

        pagamento.setPedido(pedido);
        pagamento.setFormaPagamento(forma);
        pagamento.setValor(pedido.getValorTotal());
        pagamento.setDataPagamento(new Date());
        pagamento.setStatus(status);
        pagamentoRepository.save(pagamento);

        pedido.setStatus(status == StatusPagamento.APROVADO ? "PAGO" : "PAGAMENTO_RECUSADO");
        pedidoRepository.save(pedido);

        if (status == StatusPagamento.APROVADO) {
            estoqueService.baixarPedido(pedido, usuarioLogado());
        }

        return pagamento;
    }

    private String usuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "sistema";
    }
}