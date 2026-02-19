package com.example.harmonia.services;

import jplay.Sound;

public class TocarService {

    private static Sound musica;

    public static void tocar(String caminho) {
        parar();
        musica = new Sound(caminho);
        musica.play();
    }

    public static void parar() {
        if (musica != null) {
            musica.stop();
        }
    }

}