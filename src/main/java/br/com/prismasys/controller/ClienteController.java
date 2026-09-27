package br.com.prismasys.controller;

import br.com.prismasys.model.Cliente;
import br.com.prismasys.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @GetMapping
    public String home() {
        return "clientes";
    }

    @GetMapping("/new")
    public String novo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "create-cliente";
    }

    @PostMapping
    public String salvar(Cliente cliente, Model model) {
        if (cliente.getCpfCnpj() != null && service.buscarPorCpfCnpj(cliente.getCpfCnpj()).isPresent()) {
            model.addAttribute("erro", "Já existe um cliente cadastrado com o CPF/CNPJ " + cliente.getCpfCnpj() + ".");
            return "create-cliente";
        }
        service.salvar(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("clientes", service.listar());
        return "clientes-list";
    }

    @GetMapping("/busca")
    public String telaBusca() {
        return "buscar-clientes";
    }

    @GetMapping("/search")
    public String buscar(@RequestParam("cpfCnpj") String cpfCnpj, Model model) {
        Optional<Cliente> cliente = service.buscarPorCpfCnpj(cpfCnpj);
        if (cliente.isPresent()) {
            model.addAttribute("cliente", cliente.get());
            return "alterar-clientes";
        }
        return "redirect:/clientes/busca";
    }

    @PostMapping("/update")
    public String atualizar(Cliente cliente) {
        service.salvar(cliente);
        return "redirect:/clientes/list";
    }

    @GetMapping("/excluir")
    public String telaExcluir() {
        return "excluir-clientes";
    }

    @GetMapping("/delete")
    public String excluir(@RequestParam("id") Long id) {
        service.excluir(id);
        return "redirect:/clientes/list";
    }
}