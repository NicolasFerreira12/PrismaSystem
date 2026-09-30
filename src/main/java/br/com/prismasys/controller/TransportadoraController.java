package br.com.prismasys.controller;

import br.com.prismasys.model.Transportadora;
import br.com.prismasys.service.TransportadoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/transportadoras")
public class TransportadoraController {

    @Autowired
    private TransportadoraService service;

    @GetMapping
    public String home() {
        return "transportadoras";
    }

    @GetMapping("/new")
    public String novo(Model model) {
        model.addAttribute("transportadora", new Transportadora());
        return "create-transportadora";
    }

    @PostMapping
    public String salvar(Transportadora transportadora, Model model) {
        if (transportadora.getCnpj() != null && service.buscarPorCnpj(transportadora.getCnpj()).isPresent()) {
            model.addAttribute("erro", "Já existe uma transportadora cadastrada com o CNPJ " + transportadora.getCnpj() + ".");
            return "create-transportadora";
        }
        service.salvar(transportadora);
        return "redirect:/transportadoras";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("transportadoras", service.listar());
        return "transportadoras-list";
    }

    @GetMapping("/busca")
    public String telaBusca() {
        return "buscar-transportadoras";
    }

    @GetMapping("/search")
    public String buscar(@RequestParam("cnpj") String cnpj, Model model) {
        Optional<Transportadora> transportadora = service.buscarPorCnpj(cnpj);
        if (transportadora.isPresent()) {
            model.addAttribute("transportadora", transportadora.get());
            return "alterar-transportadoras";
        }
        return "redirect:/transportadoras/busca";
    }

    @PostMapping("/update")
    public String atualizar(Transportadora transportadora) {
        service.salvar(transportadora);
        return "redirect:/transportadoras/list";
    }

    @GetMapping("/excluir")
    public String telaExcluir() {
        return "excluir-transportadoras";
    }

    @GetMapping("/delete")
    public String excluir(@RequestParam("id") Long id) {
        service.excluir(id);
        return "redirect:/transportadoras/list";
    }
}