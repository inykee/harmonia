package com.example.harmonia.dtos;

import com.example.harmonia.models.Musica;

public record MusicaRequestDto(
        String titulo,
        String caminho
) {

    public Musica toMusica(Musica musica) {
        musica.setTitulo(this.titulo());
        musica.setCaminho(this.caminho());
        return musica;
    }

}