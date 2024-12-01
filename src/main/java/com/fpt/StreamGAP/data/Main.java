package com.fpt.StreamGAP.data;

import com.fpt.StreamGAP.dto.MusicGameDTO;
import com.fpt.StreamGAP.entity.MusicGame;
import com.fpt.StreamGAP.service.MusicGameService;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("Current working directory: " + new java.io.File(".").getAbsolutePath());

        MusicGameImporter importer = new MusicGameImporter();
        File file = new File("D:\\project-svtt08\\StreamingGAP\\src\\main\\resources\\Book1.xlsx");
        List<MusicGameDTO> musicGames = importer.importMusicGamesFromExcel(file);
        MusicGameService musicGameService = new MusicGameService();
        for (MusicGameDTO game : musicGames) {
            musicGameService.createMusicGame(game);
            System.out.println(game);
            if (game != null) {
                System.out.println("ok");
            } else {
                System.out.printf("koz");
            }
        }
    }
}