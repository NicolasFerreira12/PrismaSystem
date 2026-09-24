package br.com.prismasys.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Funcionario implements Persistable<Integer> {

	@Id
	private int re;

	private String nome;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dataAdmissao;

	private Double salario;

	private String login;

	private String senha;

	@Enumerated(EnumType.STRING)
	private Perfil perfil;

	public Funcionario() {
	}

	public Funcionario(int re, String nome, Date dataAdmissao, Double salario) {
		this.re = re;
		this.nome = nome;
		this.dataAdmissao = dataAdmissao;
		this.salario = salario;
	}

	public int getRe() {
		return re;
	}

	public void setRe(int re) {
		this.re = re;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataAdmissao() {
		return dataAdmissao;
	}

	public void setDataAdmissao(Date dataAdmissao) {
		this.dataAdmissao = dataAdmissao;
	}

	public Double getSalario() {
		return salario;
	}

	public void setSalario(Double salario) {
		this.salario = salario;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	@Override
	@Transient
	public Integer getId() {
		return re;
	}

	@Override
	@Transient
	public boolean isNew() {
		return false;
	}
}