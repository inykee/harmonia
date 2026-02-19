package com.example.harmonia.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "musicas")
public class Musica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_musicas")
    private Integer idMusica;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "caminho", nullable = false, length = 255, unique = true)
    private String caminho;

}