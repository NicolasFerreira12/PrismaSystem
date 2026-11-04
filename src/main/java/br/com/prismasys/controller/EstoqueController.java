package br.com.prismasys.controller;

import br.com.prismasys.model.TipoMovimentacao;
import br.com.prismasys.repository.ProdutoRepository;
import br.com.prismasys.service.EstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService service;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("alertas", service.produtosAbaixoDoMinimo());
        return "estoque";
    }

    @GetMapping("/saldos")
    public String saldos(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll(Sort.by("nome")));
        return "estoque-saldos";
    }

    @PostMapping("/{id}/minimo")
    public String definirMinimo(@PathVariable("id") Long id,
                                @RequestParam("minimo") String minimo,
                                RedirectAttributes redirect) {
        try {
            service.definirMinimo(id, parse(minimo));
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/estoque/saldos";
    }

    // ---------------------------------------------------------- entrada / saída / ajuste

    @GetMapping("/entrada")
    public String telaEntrada(Model model) { return tela(model, TipoMovimentacao.ENTRADA); }

    @GetMapping("/saida")
    public String telaSaida(Model model) { return tela(model, TipoMovimentacao.SAIDA); }

    @GetMapping("/ajuste")
    public String telaAjuste(Model model) { return tela(model, TipoMovimentacao.AJUSTE); }

    @PostMapping("/entrada")
    public String entrada(@RequestParam("produtoId") Long produtoId,
                          @RequestParam("quantidade") String quantidade,
                          @RequestParam(value = "motivo", required = false) String motivo,
                          Authentication auth, Model model) {
        return executar(TipoMovimentacao.ENTRADA, produtoId, quantidade, motivo, auth, model);
    }

    @PostMapping("/saida")
    public String saida(@RequestParam("produtoId") Long produtoId,
                        @RequestParam("quantidade") String quantidade,
                        @RequestParam(value = "motivo", required = false) String motivo,
                        Authentication auth, Model model) {
        return executar(TipoMovimentacao.SAIDA, produtoId, quantidade, motivo, auth, model);
    }

    @PostMapping("/ajuste")
    public String ajuste(@RequestParam("produtoId") Long produtoId,
                         @RequestParam("quantidade") String quantidade,
                         @RequestParam(value = "motivo", required = false) String motivo,
                         Authentication auth, Model model) {
        return executar(TipoMovimentacao.AJUSTE, produtoId, quantidade, motivo, auth, model);
    }

    // ------------------------------------------------------------------------ histórico

    @GetMapping("/historico")
    public String historico(@RequestParam(value = "produtoId", required = false) Long produtoId,
                            Model model) {
        model.addAttribute("produtos", produtoRepository.findAll(Sort.by("nome")));
        if (produtoId != null) {
            model.addAttribute("produtoId", produtoId);
            model.addAttribute("movimentacoes", service.historico(produtoId));
        }
        return "estoque-historico";
    }

    // ------------------------------------------------------------------------- internos

    private String executar(TipoMovimentacao tipo, Long produtoId, String valor, String motivo,
                            Authentication auth, Model model) {
        try {
            BigDecimal numero = parse(valor);
            switch (tipo) {
                case ENTRADA -> service.registrarEntrada(produtoId, numero, motivo, auth.getName());
                case SAIDA -> service.registrarSaida(produtoId, numero, motivo, auth.getName());
                case AJUSTE -> service.ajustar(produtoId, numero, motivo, auth.getName());
            }
            return "redirect:/estoque/historico?produtoId=" + produtoId + "&ok=1";
        } catch (IllegalArgumentException e) { // inclui EstoqueInsuficienteException
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("produtoIdSel", produtoId);
            model.addAttribute("quantidadeSel", valor);
            model.addAttribute("motivoSel", motivo);
            return tela(model, tipo);
        }
    }

    private String tela(Model model, TipoMovimentacao tipo) {
        model.addAttribute("produtos", produtoRepository.findAll(Sort.by("nome")));
        model.addAttribute("acao", switch (tipo) {
            case ENTRADA -> "entrada";
            case SAIDA -> "saida";
            case AJUSTE -> "ajuste";
        });
        model.addAttribute("titulo", switch (tipo) {
            case ENTRADA -> "Registrar entrada";
            case SAIDA -> "Registrar saída";
            case AJUSTE -> "Ajuste de inventário";
        });
        model.addAttribute("rotulo", tipo == TipoMovimentacao.AJUSTE ? "Saldo contado" : "Quantidade");
        model.addAttribute("motivoObrigatorio", tipo == TipoMovimentacao.AJUSTE);
        return "estoque-movimento";
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