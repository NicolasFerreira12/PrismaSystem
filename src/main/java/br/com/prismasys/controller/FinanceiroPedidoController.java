package br.com.prismasys.controller;

import br.com.prismasys.model.Pedido;
import br.com.prismasys.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * "Visualizar Pedido" do UC: acesso somente leitura para o Financeiro.
 * Reaproveita o PedidoService, mas com rotas e templates próprios,
 * sem os botões de criar/pagar (que são exclusivos do Vendedor em /pedidos/**).
 */
@Controller
@RequestMapping("/financeiro/pedidos")
public class FinanceiroPedidoController {

    @Autowired
    private PedidoService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", service.listar());
        return "financeiro-pedidos-list";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable("id") Long id, Model model) {
        Optional<Pedido> pedido = service.buscarPorId(id);
        if (pedido.isPresent()) {
            model.addAttribute("pedido", pedido.get());
            return "financeiro-pedido-detalhe";
        }
        return "redirect:/financeiro/pedidos";
    }
}