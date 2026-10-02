package br.com.prismasys.dto;

import java.util.ArrayList;
import java.util.List;

public class PedidoForm {

    private Long clienteId;
    private Long transportadoraId;
    private List<ItemForm> itens = new ArrayList<>();

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getTransportadoraId() { return transportadoraId; }
    public void setTransportadoraId(Long transportadoraId) { this.transportadoraId = transportadoraId; }

    public List<ItemForm> getItens() { return itens; }
    public void setItens(List<ItemForm> itens) { this.itens = itens; }

    public static class ItemForm {
        private Long produtoId;
        private Integer quantidade;

        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    }
}