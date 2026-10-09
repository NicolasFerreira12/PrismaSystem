package br.com.prismasys.controller;

import br.com.prismasys.dto.SaidaProdutoLinha;
import br.com.prismasys.model.Pagamento;
import br.com.prismasys.model.Pedido;
import br.com.prismasys.repository.ClienteRepository;
import br.com.prismasys.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/financeiro/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService service;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public String home() {
        return "relatorios";
    }

    @GetMapping("/vendas")
    public String vendas(@RequestParam(value = "mes", required = false) String mes, Model model) {
        YearMonth periodo = service.parseMes(mes);
        List<Pedido> pedidos = service.historicoVendas(periodo);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("total", service.somaValor(pedidos));
        model.addAttribute("mes", periodo == null ? "" : periodo.toString());
        return "relatorio-vendas";
    }

    @GetMapping("/cliente")
    public String cliente(@RequestParam(value = "clienteId", required = false) Long clienteId,
                          @RequestParam(value = "mes", required = false) String mes, Model model) {
        YearMonth periodo = service.parseMes(mes);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("mes", periodo == null ? "" : periodo.toString());
        if (clienteId != null) {
            List<Pedido> pedidos = service.historicoCliente(clienteId, periodo);
            model.addAttribute("pedidos", pedidos);
            model.addAttribute("total", service.somaValor(pedidos));
            model.addAttribute("totalPago", service.somaValorPagos(pedidos));
        }
        return "relatorio-cliente";
    }

    @GetMapping("/faturamento")
    public String faturamento(@RequestParam(value = "mes", required = false) String mes, Model model) {
        YearMonth periodo = service.parseMes(mes);
        List<Pagamento> pagamentos = service.faturamento(periodo);
        model.addAttribute("pagamentos", pagamentos);
        model.addAttribute("total", service.somaPagamentos(pagamentos));
        model.addAttribute("porForma", service.faturamentoPorForma(pagamentos));
        model.addAttribute("mes", periodo == null ? "" : periodo.toString());
        return "relatorio-faturamento";
    }

    @GetMapping("/saida-produto")
    public String saidaProduto(@RequestParam(value = "mes", required = false) String mes, Model model) {
        YearMonth periodo = service.parseMes(mes);
        List<SaidaProdutoLinha> linhas = service.saidaProduto(periodo);
        model.addAttribute("linhas", linhas);
        model.addAttribute("mes", periodo == null ? "" : periodo.toString());
        return "relatorio-saida-produto";
    }
}