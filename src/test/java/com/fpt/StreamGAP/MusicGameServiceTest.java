package com.fpt.StreamGAP;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fpt.StreamGAP.entity.MusicGame;
import com.fpt.StreamGAP.repository.MusicGameRepository;
import com.fpt.StreamGAP.service.MusicGameService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;
import java.util.List;

@SpringBootTest
public class MusicGameServiceTest {

    @InjectMocks
    private MusicGameService musicGameService;

    @Mock
    private MusicGameRepository musicGameRepository;  // Mock MusicGameRepository để không thực sự lưu vào DB

    @Test
    public void testImportAndCreateMusicGamesFromExcel() throws Exception {
        // Giả lập file Excel để test
        File excelFile = new File("D:\\project-svtt08\\StreamingGAP\\src\\main\\resources\\Book1.xlsx");

        // Gọi hàm import từ service
        List<MusicGame> musicGames = musicGameService.createMusicGame(excelFile);

        // Kiểm tra kết quả
        assertNotNull(musicGames);
        assertFalse(musicGames.isEmpty());
        assertEquals("Sample Question", musicGames.get(0).getQuestion_text());

        // Kiểm tra việc lưu vào repository có được gọi hay không
        verify(musicGameRepository, times(musicGames.size())).save(any(MusicGame.class));
    }
}
