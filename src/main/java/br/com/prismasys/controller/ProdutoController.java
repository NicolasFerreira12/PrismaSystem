package br.com.prismasys.controller;

import br.com.prismasys.model.Produto;
import br.com.prismasys.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @GetMapping
    public String home() {
        return "produtos";
    }

    @GetMapping("/new")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        return "create-produto";
    }

    @PostMapping
    public String salvar(Produto produto) {
        service.salvar(produto);
        return "redirect:/produtos";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("produtos", service.listar());
        return "produtos-list";
    }

    @GetMapping("/busca")
    public String telaBusca() {
        return "buscar-produtos";
    }

    @GetMapping("/search")
    public String buscar(@RequestParam("id") Long id, Model model) {
        Optional<Produto> produto = service.buscarPorId(id);
        if (produto.isPresent()) {
            model.addAttribute("produto", produto.get());
            return "alterar-produtos";
        }
        return "redirect:/produtos/busca";
    }

    @PostMapping("/update")
    public String atualizar(Produto produto) {
        service.salvar(produto);
        return "redirect:/produtos/list";
    }

    @GetMapping("/excluir")
    public String telaExcluir() {
        return "excluir-produtos";
    }

    @GetMapping("/delete")
    public String excluir(@RequestParam("id") Long id) {
        service.excluir(id);
        return "redirect:/produtos/list";
    }
}