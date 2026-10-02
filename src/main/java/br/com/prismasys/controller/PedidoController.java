package br.com.prismasys.controller;

import br.com.prismasys.dto.PedidoForm;
import br.com.prismasys.model.Pedido;
import br.com.prismasys.repository.ClienteRepository;
import br.com.prismasys.repository.ProdutoRepository;
import br.com.prismasys.repository.TransportadoraRepository;
import br.com.prismasys.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService service;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TransportadoraRepository transportadoraRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public String home() {
        return "pedidos";
    }

    @GetMapping("/new")
    public String novo(Model model) {
        model.addAttribute("pedidoForm", new PedidoForm());
        carregarListasAuxiliares(model);
        return "create-pedido";
    }

    @PostMapping
    public String salvar(PedidoForm pedidoForm, Model model) {
        try {
            service.salvar(pedidoForm);
            return "redirect:/pedidos/list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("pedidoForm", pedidoForm);
            carregarListasAuxiliares(model);
            return "create-pedido";
        }
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("pedidos", service.listar());
        return "pedidos-list";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable("id") Long id, Model model) {
        Optional<Pedido> pedido = service.buscarPorId(id);
        if (pedido.isPresent()) {
            model.addAttribute("pedido", pedido.get());
            return "pedido-detalhe";
        }
        return "redirect:/pedidos/list";
    }

    private void carregarListasAuxiliares(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("transportadoras", transportadoraRepository.findAll());
        model.addAttribute("produtos", produtoRepository.findAll());
    }
}