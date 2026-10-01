package br.com.prismasys.controller;

import br.com.prismasys.model.MateriaPrima;
import br.com.prismasys.service.MateriaPrimaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/materias-primas")
public class MateriaPrimaController {

    @Autowired
    private MateriaPrimaService service;

    @GetMapping
    public String home() {
        return "materias-primas";
    }

    @GetMapping("/new")
    public String novo(Model model) {
        model.addAttribute("materiaPrima", new MateriaPrima());
        return "create-materia-prima";
    }

    @PostMapping
    public String salvar(MateriaPrima materiaPrima) {
        service.salvar(materiaPrima);
        return "redirect:/materias-primas";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        model.addAttribute("materiasPrimas", service.listar());
        return "materias-primas-list";
    }

    @GetMapping("/busca")
    public String telaBusca() {
        return "buscar-materias-primas";
    }

    @GetMapping("/search")
    public String buscar(@RequestParam("id") Long id, Model model) {
        Optional<MateriaPrima> materiaPrima = service.buscarPorId(id);
        if (materiaPrima.isPresent()) {
            model.addAttribute("materiaPrima", materiaPrima.get());
            return "alterar-materias-primas";
        }
        return "redirect:/materias-primas/busca";
    }

    @PostMapping("/update")
    public String atualizar(MateriaPrima materiaPrima) {
        service.salvar(materiaPrima);
        return "redirect:/materias-primas/list";
    }

    @GetMapping("/excluir")
    public String telaExcluir() {
        return "excluir-materias-primas";
    }

    @GetMapping("/delete")
    public String excluir(@RequestParam("id") Long id) {
        service.excluir(id);
        return "redirect:/materias-primas/list";
    }
}