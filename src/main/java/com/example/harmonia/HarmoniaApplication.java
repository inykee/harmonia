package com.example.harmonia;

import com.example.harmonia.forms.HarmoniaForm;
import com.example.harmonia.repositories.MusicaRepository;
import com.example.harmonia.services.MusicaService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class HarmoniaApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HarmoniaApplication.class);
		app.setHeadless(false);
		var context = app.run(args);

		MusicaRepository musicaRepository = context.getBean(MusicaRepository.class);
		MusicaService musicaService = context.getBean(MusicaService.class);

		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			HarmoniaForm dialog = new HarmoniaForm(musicaRepository, musicaService);
			dialog.pack();
			dialog.setVisible(true);
		});
	}

}