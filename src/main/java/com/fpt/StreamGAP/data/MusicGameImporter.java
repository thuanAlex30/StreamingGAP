package com.fpt.StreamGAP.data;

import com.fpt.StreamGAP.dto.MusicGameDTO;
import com.fpt.StreamGAP.entity.MusicGame;
import com.fpt.StreamGAP.service.MusicGameService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Component
public class MusicGameImporter {


    public List<MusicGameDTO> importMusicGamesFromExcel(File excelFile) {
        List<MusicGameDTO> musicGameDTOList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                MusicGameDTO musicGameDTO = new MusicGameDTO();
                musicGameDTO.setQuestion_text(row.getCell(0).getStringCellValue());
                musicGameDTO.setAnswer_1(row.getCell(1).getStringCellValue());
                musicGameDTO.setAnswer_2(row.getCell(2).getStringCellValue());
                musicGameDTO.setAnswer_3(row.getCell(3).getStringCellValue());
                musicGameDTO.setAnswer_4(row.getCell(4).getStringCellValue());
                musicGameDTO.setCorrect_answer((int) row.getCell(5).getNumericCellValue());

                if (row.getCell(6).getCellType() == CellType.NUMERIC) {
                    musicGameDTO.setUser_answer((int) row.getCell(6).getNumericCellValue());
                }

                musicGameDTOList.add(musicGameDTO);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return musicGameDTOList;
    }

}