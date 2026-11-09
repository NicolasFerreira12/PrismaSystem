package br.com.prismasys.controller;

import br.com.prismasys.model.OrdemProducao;
import br.com.prismasys.model.StatusProducao;
import br.com.prismasys.repository.ProdutoRepository;
import br.com.prismasys.service.ProducaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/producao")
public class ProducaoController {

    @Autowired
    private ProducaoService service;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public String listar(@RequestParam(value = "status", required = false) StatusProducao status,
                         Model model) {
        model.addAttribute("ordens", service.listar(status));
        model.addAttribute("status", status);
        model.addAttribute("statusOpcoes", StatusProducao.values());
        return "producao";
    }

    // ------------------------------------------------------------------------ nova ordem

    @GetMapping("/nova")
    public String nova(Model model) {
        return formNova(model);
    }

    @PostMapping("/nova")
    public String criar(@RequestParam("produtoId") Long produtoId,
                        @RequestParam("quantidade") String quantidade,
                        @RequestParam(value = "dataPrevista", required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPrevista,
                        @RequestParam(value = "observacao", required = false) String observacao,
                        Authentication auth, Model model, RedirectAttributes redirect) {
        try {
            OrdemProducao ordem = service.criar(produtoId, parse(quantidade), dataPrevista,
                    observacao, auth.getName());
            redirect.addFlashAttribute("ok", "Ordem de produção #" + ordem.getId() + " criada.");
            return "redirect:/producao";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("produtoIdSel", produtoId);
            model.addAttribute("quantidadeSel", quantidade);
            model.addAttribute("dataPrevistaSel", dataPrevista);
            model.addAttribute("observacaoSel", observacao);
            return formNova(model);
        }
    }

    // ------------------------------------------------------------- iniciar / cancelar

    @PostMapping("/{id}/iniciar")
    public String iniciar(@PathVariable("id") Long id, RedirectAttributes redirect) {
        try {
            service.iniciar(id);
            redirect.addFlashAttribute("ok", "Ordem #" + id + " iniciada.");
        } catch (IllegalStateException | NoSuchElementException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/producao";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable("id") Long id, Authentication auth, RedirectAttributes redirect) {
        try {
            service.cancelar(id, auth.getName());
            redirect.addFlashAttribute("ok", "Ordem #" + id + " cancelada.");
        } catch (IllegalStateException | NoSuchElementException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/producao";
    }

    // ---------------------------------------------------------------------------- concluir

    @GetMapping("/{id}/concluir")
    public String telaConcluir(@PathVariable("id") Long id, Model model, RedirectAttributes redirect) {
        try {
            OrdemProducao ordem = service.buscar(id);
            if (ordem.getStatus() != StatusProducao.EM_PRODUCAO) {
                redirect.addFlashAttribute("erro", "Só é possível concluir uma ordem em produção.");
                return "redirect:/producao";
            }
            model.addAttribute("ordem", ordem);
            return "producao-concluir";
        } catch (NoSuchElementException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/producao";
        }
    }

    @PostMapping("/{id}/concluir")
    public String concluir(@PathVariable("id") Long id,
                           @RequestParam("quantidadeProduzida") String quantidade,
                           Authentication auth, Model model, RedirectAttributes redirect) {
        try {
            service.concluir(id, parse(quantidade), auth.getName());
            redirect.addFlashAttribute("ok", "Ordem #" + id + " concluída: o produto entrou no estoque.");
            return "redirect:/producao";
        } catch (IllegalStateException | NoSuchElementException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/producao";
        } catch (IllegalArgumentException e) { // quantidade inválida
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("ordem", service.buscar(id));
            model.addAttribute("quantidadeSel", quantidade);
            return "producao-concluir";
        }
    }

    // ------------------------------------------------------------------------- internos

    private String formNova(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll(Sort.by("nome")));
        return "producao-nova";
    }

    /** Aceita "1,5", "1.5" e "1.000,5". */
    private BigDecimal parse(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Informe um valor numérico.");
        }
        String v = valor.trim();
        if (v.contains(",")) {
            v = v.replace(".", "").replace(",", ".");
        }
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor numérico inválido: " + valor);
        }
    }
}