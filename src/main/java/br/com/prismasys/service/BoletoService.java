package br.com.prismasys.service;

import br.com.prismasys.model.Boleto;
import br.com.prismasys.model.Pedido;
import br.com.prismasys.repository.BoletoRepository;
import br.com.prismasys.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoletoService {

    @Autowired
    private BoletoRepository repository;

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Boleto> listar() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Boleto::getId).reversed())
                .collect(Collectors.toList());
    }

    public Optional<Boleto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /** Pedidos que ainda não estão pagos e ainda não têm boleto. */
    public List<Pedido> pedidosElegiveis() {
        return pedidoRepository.findAll().stream()
                .filter(p -> !"PAGO".equals(p.getStatus()))
                .filter(p -> !repository.existsByPedidoId(p.getId()))
                .sorted(Comparator.comparing(Pedido::getId).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public Boleto gerar(Long pedidoId, Date vencimento) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("Selecione um pedido.");
        }
        if (vencimento == null) {
            throw new IllegalArgumentException("Informe a data de vencimento.");
        }
        LocalDate dataVencimento = new Date(vencimento.getTime()).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        if (dataVencimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de vencimento não pode ser anterior a hoje.");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
        if ("PAGO".equals(pedido.getStatus())) {
            throw new IllegalArgumentException("Este pedido já está pago.");
        }
        if (repository.existsByPedidoId(pedidoId)) {
            throw new IllegalArgumentException("Já existe um boleto para este pedido.");
        }

        Boleto boleto = new Boleto();
        boleto.setPedido(pedido);
        boleto.setValor(pedido.getValorTotal());
        boleto.setDataEmissao(new Date());
        boleto.setDataVencimento(vencimento);
        boleto = repository.save(boleto);

        boleto.setLinhaDigitavel(montarLinhaDigitavel(boleto));
        return repository.save(boleto);
    }

    /** Linha digitável SIMULADA (47 dígitos montados com os IDs) — não é um código bancário válido. */
    private String montarLinhaDigitavel(Boleto boleto) {
        long centavos = Math.round(boleto.getValor() * 100);
        String digitos = "0019"
                + String.format("%010d", boleto.getId())
                + String.format("%010d", boleto.getPedido().getId())
                + String.format("%010d", centavos);
        digitos = String.format("%-47s", digitos).replace(' ', '0');

        return digitos.substring(0, 5) + "." + digitos.substring(5, 10) + " "
                + digitos.substring(10, 15) + "." + digitos.substring(15, 21) + " "
                + digitos.substring(21, 26) + "." + digitos.substring(26, 32) + " "
                + digitos.substring(32, 33) + " "
                + digitos.substring(33, 47);
    }
}