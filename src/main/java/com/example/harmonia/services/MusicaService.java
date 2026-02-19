package com.example.harmonia.services;

import com.example.harmonia.dtos.MusicaRequestDto;
import com.example.harmonia.models.Musica;
import com.example.harmonia.repositories.MusicaRepository;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class MusicaService {

    private final MusicaRepository musicaRepository;

    private static final String YTDLP_PATH = "tools/yt-dlp.exe";
    private static final String FFMPEG_PATH = "tools/ffmpeg.exe";
    private static final String DOWNLOADS_PATH = "downloads/musicas";

    public MusicaService(MusicaRepository musicaRepository) {
        this.musicaRepository = musicaRepository;
    }

    public boolean baixarMusica(String titulo) {
        String nomeArquivo = limparNome(titulo);
        String caminho = DOWNLOADS_PATH + "/" + nomeArquivo + ".wav";

        List<String> comando = montarComando(titulo, caminho);
        boolean sucesso = executarComando(comando);

        if (sucesso) {
            salvarMusica(nomeArquivo, caminho);
            JOptionPane.showMessageDialog(
                    null,
                    "Música '" + titulo + "' baixada com sucesso!\nCaminho: " + caminho,
                    "Download Concluído",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Falha ao baixar a música '" + titulo + "'.",
                    "Erro no Download",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return sucesso;
    }

    public boolean deletarMusica(Musica musica) {
        File arquivo = new File(musica.getCaminho());
        boolean arquivoDeletado = !arquivo.exists() || arquivo.delete();

        if (arquivoDeletado) {
            musicaRepository.delete(musica);
            JOptionPane.showMessageDialog(
                    null,
                    "Música deletada com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return true;
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Não foi possível deletar o arquivo.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    private List<String> montarComando(String titulo, String caminho) {
        List<String> comando = new ArrayList<>();
        comando.add(YTDLP_PATH);
        comando.add("-x");
        comando.add("--audio-format");
        comando.add("wav");
        comando.add("--ffmpeg-location");
        comando.add(FFMPEG_PATH);
        comando.add("-o");
        comando.add(caminho);
        comando.add("ytsearch:" + titulo);
        return comando;
    }

    private boolean executarComando(List<String> comando) {
        try {
            ProcessBuilder pb = new ProcessBuilder(comando);
            pb.redirectErrorStream(true);
            Process processo = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(processo.getInputStream()))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    System.out.println(linha);
                }
            }

            return processo.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void salvarMusica(String titulo, String caminho) {
        MusicaRequestDto dto = new MusicaRequestDto(titulo, caminho);
        Musica musica = dto.toMusica(new Musica());
        musicaRepository.save(musica);
    }

    private String limparNome(String nome) {
        return nome.replaceAll("[<>:\"/\\\\|?*]", "-")
                .replace(" ", "_");
    }
    
}
