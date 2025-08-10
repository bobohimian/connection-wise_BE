package com.conwise;

import com.conwise.mapper.CanvasMapper;
import com.conwise.service.CanvasService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConnectWiseApplicationTests {

    @Mock
    private CanvasMapper canvasMapper;
    @InjectMocks
    private CanvasService canvasService;


}
