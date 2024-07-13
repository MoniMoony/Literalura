package br.com.andre.literalura.entity;

public enum Idioma {
    ES("Español"),
    EN("Inglés"),
    FR("Francés"),
    PT("Portugués"),
    IT("Italiano");

    private String nome;

    public String getNome() {
        return nome;
    }

    Idioma(String nome) {
        this.nome = nome;
    }
}
