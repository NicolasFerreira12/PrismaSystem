package br.com.prismasys.controller;

import br.com.prismasys.model.Fornecedor;
import br.com.prismasys.service.FornecedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService service;

    @GetMapping
    public String home() {
        return "fornecedores";
    }

    @GetMapping("/new")
    public String novo(Model model) {
        model.addAttribute("fornecedor", new Fornecedor());
        return "create-fornecedor";
    }

    @PostMapping
    public String salvar(Fornecedor fornecedor, Model model) {
        if (fornecedor.getCnpj() != null && service.buscarPorCnpj(fornecedor.getCnpj()).isPresent()) {
            model.addAttribute("erro", "Já existe um fornecedor cadastrado com o CNPJ " + fornecedor.getCnpj() + ".");
            return "create-fornecedor";
        }
        service.salvar(fornecedor);
        return "redirect:/fornecedores";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("fornecedores", service.listar());
        return "fornecedores-list";
    }

    @GetMapping("/busca")
    public String telaBusca() {
        return "buscar-fornecedores";
    }

    @GetMapping("/search")
    public String buscar(@RequestParam("cnpj") String cnpj, Model model) {
        Optional<Fornecedor> fornecedor = service.buscarPorCnpj(cnpj);
        if (fornecedor.isPresent()) {
            model.addAttribute("fornecedor", fornecedor.get());
            return "alterar-fornecedores";
        }
        return "redirect:/fornecedores/busca";
    }

    @PostMapping("/update")
    public String atualizar(Fornecedor fornecedor) {
        service.salvar(fornecedor);
        return "redirect:/fornecedores/list";
    }

    @GetMapping("/excluir")
    public String telaExcluir() {
        return "excluir-fornecedores";
    }

    @GetMapping("/delete")
    public String excluir(@RequestParam("id") Long id) {
        service.excluir(id);
        return "redirect:/fornecedores/list";
    }
}