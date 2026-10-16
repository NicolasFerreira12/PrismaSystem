package br.com.prismasys.controller;

import br.com.prismasys.model.Boleto;
import br.com.prismasys.service.BoletoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/financeiro/boletos")
public class BoletoController {

    @Autowired
    private BoletoService service;

    @GetMapping
    public String home(Model model) {
        carregar(model);
        return "boletos";
    }

    @PostMapping
    public String gerar(@RequestParam(value = "pedidoId", required = false) Long pedidoId,
                        @RequestParam(value = "vencimento", required = false)
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date vencimento,
                        Model model) {
        try {
            Boleto boleto = service.gerar(pedidoId, vencimento);
            return "redirect:/financeiro/boletos/" + boleto.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            carregar(model);
            return "boletos";
        }
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable("id") Long id, Model model) {
        Optional<Boleto> boleto = service.buscarPorId(id);
        if (boleto.isPresent()) {
            model.addAttribute("boleto", boleto.get());
            return "boleto-detalhe";
        }
        return "redirect:/financeiro/boletos";
    }

    @PostMapping("/{id}/confirmar-pagamento")
    public String confirmarPagamento(@PathVariable("id") Long id, Model model) {
        try {
            service.confirmarPagamento(id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return detalhe(id, model);
        }
        return "redirect:/financeiro/boletos/" + id;
    }

    private void carregar(Model model) {
        model.addAttribute("boletos", service.listar());
        model.addAttribute("pedidos", service.pedidosElegiveis());
    }
}