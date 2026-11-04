package br.com.prismasys.service;

import br.com.prismasys.model.Produto;
import br.com.prismasys.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    public List<Produto> listar() {
        return repository.findAll();
    }

    @Transactional
    public Produto salvar(Produto produto) {
        if (produto.getId() != null) {
            // o formulário de produto não envia os campos de estoque: preserva os valores atuais
            repository.findByIdForUpdate(produto.getId()).ifPresent(atual -> {
                produto.setEstoqueAtual(atual.getEstoqueAtual());
                produto.setEstoqueMinimo(atual.getEstoqueMinimo());
            });
        }
        return repository.save(produto);
    }

    public Optional<Produto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}