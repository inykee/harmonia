package com.example.harmonia.forms;

import com.example.harmonia.models.Musica;
import com.example.harmonia.repositories.MusicaRepository;
import com.example.harmonia.services.MusicaService;
import com.example.harmonia.services.TocarService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;

/**
 * Tela principal do gerenciador de músicas.
 * Permite baixar, tocar, parar e deletar músicas.
 */
public class HarmoniaForm extends JDialog {

    private JPanel pnlPrincipal, pnlCampos, pnlBotoes, pnlInformacoes;
    private JScrollPane pnlTabelas;
    private JTextPane txtInformativo;

    private JTextField txtTitulo;
    private JTable tblMusicas;
    private JButton btnBaixar, btnTocar, btnParar, btnDeletar;
    private JLabel lblTitulo;

    private final MusicaRepository musicaRepository;
    private final MusicaService musicaService;

    public HarmoniaForm(MusicaRepository musicaRepository, MusicaService musicaService) {
        this.musicaRepository = musicaRepository;
        this.musicaService = musicaService;

        setContentPane(pnlPrincipal);
        setModal(true);
        setTitle("Harmonia - Gerenciador de Músicas");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        carregarTabela();    // Preenche a tabela com músicas
        configurarEventos(); // Configura botões e atalhos
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Configura os eventos da GUI.
     * Inclui botões e fechamento da janela.
     */
    private void configurarEventos() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // Fechar com ESC
        pnlPrincipal.registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Baixar música
        btnBaixar.addActionListener(e -> {
            String titulo = txtTitulo.getText().trim();
            if (!titulo.isEmpty()) {
                musicaService.baixarMusica(titulo);
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(null, "Digite um título válido.");
            }
        });

        // Deletar música selecionada
        btnDeletar.addActionListener(e -> {
            int linha = tblMusicas.getSelectedRow();
            if (linha != -1) {
                DefaultTableModel modelo = (DefaultTableModel) tblMusicas.getModel();
                Integer idMusica = (Integer) modelo.getValueAt(linha, 0);
                Musica musica = musicaRepository.findById(idMusica).orElse(null);
                if (musica != null && musicaService.deletarMusica(musica)) {
                    modelo.removeRow(linha);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selecione uma música.");
            }
        });

        // Tocar música selecionada
        btnTocar.addActionListener(e -> {
            int linha = tblMusicas.getSelectedRow();
            if (linha != -1) {
                String caminho = (String) tblMusicas.getValueAt(linha, 2);
                TocarService.tocar(caminho);
            } else {
                JOptionPane.showMessageDialog(null, "Selecione uma música.");
            }
        });

        // Parar reprodução
        btnParar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TocarService.parar();
            }
        });
    }

    // Atualiza a tabela com as músicas do banco
    private void carregarTabela() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Código", "Título", "Caminho"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Musica> musicas = musicaRepository.findAll();
        for (Musica musica : musicas) {
            modelo.addRow(new Object[]{
                    musica.getIdMusica(),
                    musica.getTitulo(),
                    musica.getCaminho()
            });
        }

        tblMusicas.setModel(modelo);
    }

}