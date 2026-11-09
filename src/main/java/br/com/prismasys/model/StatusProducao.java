package br.com.prismasys.model;

public enum StatusProducao {
    PLANEJADA("Planejada"),
    EM_PRODUCAO("Em produção"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusProducao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}