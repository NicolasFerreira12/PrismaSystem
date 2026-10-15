package br.com.prismasys.controller;

import br.com.prismasys.model.NotaFiscal;
import br.com.prismasys.service.NotaFiscalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/financeiro/notas")
public class NotaFiscalController {

    @Autowired
    private NotaFiscalService service;

    @GetMapping
    public String home(Model model) {
        carregar(model);
        return "notas";
    }

    @PostMapping
    public String emitir(@RequestParam(value = "pedidoId", required = false) Long pedidoId, Model model) {
        try {
            NotaFiscal nota = service.emitir(pedidoId);
            return "redirect:/financeiro/notas/" + nota.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            carregar(model);
            return "notas";
        }
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable("id") Long id, Model model) {
        Optional<NotaFiscal> nota = service.buscarPorId(id);
        if (nota.isPresent()) {
            model.addAttribute("nota", nota.get());
            return "nota-detalhe";
        }
        return "redirect:/financeiro/notas";
    }

    private void carregar(Model model) {
        model.addAttribute("notas", service.listar());
        model.addAttribute("pedidos", service.pedidosElegiveis());
    }
}