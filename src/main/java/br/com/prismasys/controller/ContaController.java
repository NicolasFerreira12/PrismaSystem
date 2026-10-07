package br.com.prismasys.controller;

import br.com.prismasys.model.ContaPagarReceber;
import br.com.prismasys.service.ContaPagarReceberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/financeiro/contas")
public class ContaController {

    @Autowired
    private ContaPagarReceberService service;

    @GetMapping
    public String home() {
        return "contas";
    }

    @GetMapping("/new")
    public String novo(Model model) {
        model.addAttribute("conta", new ContaPagarReceber());
        return "create-conta";
    }

    @PostMapping
    public String salvar(ContaPagarReceber conta) {
        service.salvar(conta);
        return "redirect:/financeiro/contas";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("contas", service.listar());
        return "contas-list";
    }

    @GetMapping("/busca")
    public String telaBusca() {
        return "buscar-contas";
    }

    @GetMapping("/search")
    public String buscar(@RequestParam("id") Long id, Model model) {
        Optional<ContaPagarReceber> conta = service.buscarPorId(id);
        if (conta.isPresent()) {
            model.addAttribute("conta", conta.get());
            return "alterar-contas";
        }
        return "redirect:/financeiro/contas/busca";
    }

    @PostMapping("/update")
    public String atualizar(ContaPagarReceber conta) {
        service.salvar(conta);
        return "redirect:/financeiro/contas/list";
    }

    @GetMapping("/baixa")
    public String darBaixa(@RequestParam("id") Long id) {
        service.darBaixa(id);
        return "redirect:/financeiro/contas/list";
    }

    @GetMapping("/excluir")
    public String telaExcluir() {
        return "excluir-contas";
    }

    @GetMapping("/delete")
    public String excluir(@RequestParam("id") Long id) {
        service.excluir(id);
        return "redirect:/financeiro/contas/list";
    }
}