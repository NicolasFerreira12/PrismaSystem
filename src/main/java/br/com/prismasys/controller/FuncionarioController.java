package br.com.prismasys.controller;

import br.com.prismasys.model.Funcionario;
import br.com.prismasys.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

	@Autowired
	private FuncionarioService service;

	@GetMapping
	public String home() {
		return "funcionarios";
	}

	@GetMapping("/new")
	public String novo(Model model) {
		model.addAttribute("funcionario", new Funcionario());
		return "create-funcionario";
	}

	@PostMapping
	public String salvar(Funcionario funcionario, Model model) {
		if (service.buscarPorRe(funcionario.getRe()).isPresent()) {
			model.addAttribute("erro", "Já existe um funcionário cadastrado com o RE " + funcionario.getRe() + ".");
			return "create-funcionario";
		}
		if (service.buscarPorLogin(funcionario.getLogin()).isPresent()) {
			model.addAttribute("erro", "O login \"" + funcionario.getLogin() + "\" já está em uso.");
			return "create-funcionario";
		}
		service.salvar(funcionario);
		return "redirect:/funcionarios";
	}

	@GetMapping("/list")
	public String listar(Model model) {
		model.addAttribute("funcionarios", service.listar());
		return "funcionarios-list";
	}

	@GetMapping("/busca")
	public String telaBusca() {
		return "buscar-funcionarios";
	}

	@GetMapping("/search")
	public String buscar(@RequestParam("re") int re, Model model) {
		Optional<Funcionario> funcionario = service.buscarPorRe(re);
		if (funcionario.isPresent()) {
			model.addAttribute("funcionario", funcionario.get());
			return "alterar-funcionarios";
		}
		return "redirect:/funcionarios/busca";
	}

	@PostMapping("/update")
	public String atualizar(Funcionario funcionario) {
		service.salvar(funcionario);
		return "redirect:/funcionarios/list";
	}

	@GetMapping("/excluir")
	public String telaExcluir() {
		return "excluir-funcionarios";
	}

	@GetMapping("/delete")
	public String excluir(@RequestParam("re") int re) {
		service.excluir(re);
		return "redirect:/funcionarios/list";
	}
}