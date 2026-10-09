package br.com.prismasys.service;

import br.com.prismasys.dto.SaidaProdutoLinha;
import br.com.prismasys.model.FormaPagamento;
import br.com.prismasys.model.Pagamento;
import br.com.prismasys.model.Pedido;
import br.com.prismasys.model.PedidoItem;
import br.com.prismasys.model.Produto;
import br.com.prismasys.model.StatusPagamento;
import br.com.prismasys.repository.PagamentoRepository;
import br.com.prismasys.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RelatorioService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    /** Converte "2026-10" (input type=month) em YearMonth; vazio/inválido = sem filtro. */
    public YearMonth parseMes(String mes) {
        if (mes == null || mes.isBlank()) {
            return null;
        }
        try {
            return YearMonth.parse(mes);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private boolean noPeriodo(Date data, YearMonth mes) {
        if (mes == null) {
            return true;
        }
        if (data == null) {
            return false;
        }
        LocalDate dia = new Date(data.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return YearMonth.from(dia).equals(mes);
    }

    // ---------- Histórico de Vendas ----------
    public List<Pedido> historicoVendas(YearMonth mes) {
        return pedidoRepository.findAll().stream()
                .filter(p -> noPeriodo(p.getDataPedido(), mes))
                .sorted(Comparator.comparing(Pedido::getId).reversed())
                .collect(Collectors.toList());
    }

    // ---------- Histórico de Cliente ----------
    public List<Pedido> historicoCliente(Long clienteId, YearMonth mes) {
        return historicoVendas(mes).stream()
                .filter(p -> p.getCliente() != null && clienteId.equals(p.getCliente().getId()))
                .collect(Collectors.toList());
    }

    public double somaValor(List<Pedido> pedidos) {
        return pedidos.stream()
                .mapToDouble(p -> p.getValorTotal() == null ? 0.0 : p.getValorTotal())
                .sum();
    }

    public double somaValorPagos(List<Pedido> pedidos) {
        return pedidos.stream()
                .filter(p -> "PAGO".equals(p.getStatus()))
                .mapToDouble(p -> p.getValorTotal() == null ? 0.0 : p.getValorTotal())
                .sum();
    }

    // ---------- Histórico de Faturamento ----------
    public List<Pagamento> faturamento(YearMonth mes) {
        return pagamentoRepository.findAll().stream()
                .filter(p -> p.getStatus() == StatusPagamento.APROVADO)
                .filter(p -> noPeriodo(p.getDataPagamento(), mes))
                .sorted(Comparator.comparing(Pagamento::getId).reversed())
                .collect(Collectors.toList());
    }

    public double somaPagamentos(List<Pagamento> pagamentos) {
        return pagamentos.stream()
                .mapToDouble(p -> p.getValor() == null ? 0.0 : p.getValor())
                .sum();
    }

    public Map<FormaPagamento, Double> faturamentoPorForma(List<Pagamento> pagamentos) {
        Map<FormaPagamento, Double> mapa = new EnumMap<>(FormaPagamento.class);
        for (Pagamento p : pagamentos) {
            mapa.merge(p.getFormaPagamento(), p.getValor() == null ? 0.0 : p.getValor(), Double::sum);
        }
        return mapa;
    }

    // ---------- Saída de Produto ----------
    public List<SaidaProdutoLinha> saidaProduto(YearMonth mes) {
        Map<Long, SaidaProdutoLinha> linhas = new LinkedHashMap<>();
        for (Pedido pedido : historicoVendas(mes)) {
            if (!"PAGO".equals(pedido.getStatus())) {
                continue;
            }
            for (PedidoItem item : pedido.getItens()) {
                Produto produto = item.getProduto();
                SaidaProdutoLinha linha = linhas.computeIfAbsent(produto.getId(),
                        id -> new SaidaProdutoLinha(produto.getNome(), produto.getUnidadeMedida()));
                linha.adicionar(item.getQuantidade() == null ? 0 : item.getQuantidade(), item.getSubtotal());
            }
        }
        List<SaidaProdutoLinha> resultado = new ArrayList<>(linhas.values());
        resultado.sort(Comparator.comparingInt(SaidaProdutoLinha::getQuantidade).reversed());
        return resultado;
    }
}