package br.com.prismasys.service;

import br.com.prismasys.dto.PedidoForm;
import br.com.prismasys.model.Cliente;
import br.com.prismasys.model.Pedido;
import br.com.prismasys.model.PedidoItem;
import br.com.prismasys.model.Produto;
import br.com.prismasys.model.Transportadora;
import br.com.prismasys.repository.ClienteRepository;
import br.com.prismasys.repository.PedidoRepository;
import br.com.prismasys.repository.ProdutoRepository;
import br.com.prismasys.repository.TransportadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TransportadoraRepository transportadoraRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Pedido> listar() {
        return repository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Pedido salvar(PedidoForm form) {
        Cliente cliente = clienteRepository.findById(form.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Transportadora transportadora = transportadoraRepository.findById(form.getTransportadoraId())
                .orElseThrow(() -> new IllegalArgumentException("Transportadora não encontrada"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setTransportadora(transportadora);
        pedido.setDataPedido(new Date());
        pedido.setStatus("ABERTO");

        double total = 0.0;
        for (PedidoForm.ItemForm itemForm : form.getItens()) {
            if (itemForm.getProdutoId() == null || itemForm.getQuantidade() == null || itemForm.getQuantidade() <= 0) {
                continue;
            }
            Produto produto = produtoRepository.findById(itemForm.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemForm.getQuantidade());
            item.setPrecoUnitario(produto.getPrecoUnitario());

            pedido.getItens().add(item);
            total += item.getSubtotal();
        }

        if (pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("O pedido precisa ter ao menos um item.");
        }

        pedido.setValorTotal(total);
        return repository.save(pedido);
    }
}