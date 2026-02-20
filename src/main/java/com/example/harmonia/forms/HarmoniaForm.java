package com.example.harmonia.forms;

import com.example.harmonia.models.Musica;
import com.example.harmonia.repositories.MusicaRepository;
import com.example.harmonia.services.MusicaService;
import com.example.harmonia.services.TocarService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;

public class HarmoniaForm extends JDialog {

    // Painéis
    private JPanel pnlPrincipal;
    private JPanel pnlCampos;
    private JPanel pnlBotoes;
    private JPanel pnlInformacoes;

    // Componentes de tabela
    private JScrollPane pnlTabelas;
    private JTable tblMusicas;

    // Componentes de texto
    private JTextPane txtInformativo;
    private JTextField txtTitulo;
    private JLabel lblTitulo;

    // Botões
    private JButton btnBaixar;
    private JButton btnTocar;
    private JButton btnParar;
    private JButton btnDeletar;

    private final MusicaRepository musicaRepository;
    private final MusicaService musicaService;

    public HarmoniaForm(MusicaRepository musicaRepository, MusicaService musicaService) {
        this.musicaRepository = musicaRepository;
        this.musicaService = musicaService;

        setContentPane(pnlPrincipal);
        setModal(true);
        setTitle("Harmonia - Gerenciador de Músicas");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        carregarTabela();
        configurarEventos();
        pack();
        setLocationRelativeTo(null);
    }

    private void configurarEventos() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                TocarService.parar();
                dispose();
            }
        });

        btnBaixar.addActionListener(e -> {
            String titulo = txtTitulo.getText().trim();
            if (!titulo.isEmpty()) {
                musicaService.baixarMusica(titulo);
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(null, "Digite um título válido.");
            }
        });

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

        btnTocar.addActionListener(e -> {
            int linha = tblMusicas.getSelectedRow();
            if (linha != -1) {
                String caminho = (String) tblMusicas.getValueAt(linha, 2);
                TocarService.tocar(caminho);
            } else {
                JOptionPane.showMessageDialog(null, "Selecione uma música.");
            }
        });

        btnParar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TocarService.parar();
            }
        });
    }

    private void carregarTabela() {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addColumn("Código");
        modelo.addColumn("Título");
        modelo.addColumn("Caminho");

        List<Musica> musicas = musicaRepository.findAll();

        for (Musica musica : musicas) {
            modelo.addRow(new Object[]{musica.getIdMusica(), musica.getTitulo(), musica.getCaminho()});
        }

        tblMusicas.setModel(modelo);
        tblMusicas.getTableHeader().setReorderingAllowed(false);
    }

}
