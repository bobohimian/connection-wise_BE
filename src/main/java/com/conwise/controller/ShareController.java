package com.conwise.controller;

import com.conwise.model.Canvas;
import com.conwise.model.CanvasShare;
import com.conwise.model.User;
import com.conwise.service.CanvasShareService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/share")

@RestController
public class ShareController {
    private final CanvasShareService canvasShareService;

    public ShareController(CanvasShareService canvasShareService) {
        this.canvasShareService = canvasShareService;
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Canvas>> getCanvasShareByUserId(@PathVariable("userId") int userId) {
        List<Canvas> canvasList = canvasShareService.getCanvasShareByUserId(userId);
        return ResponseEntity.ok(canvasList);
    }

    @GetMapping("/{canvasId}")
    public ResponseEntity<List<User>> getSharedUsersByCanvasId(@PathVariable("canvasId") int canvasId) {
        List<User> canvasList = canvasShareService.getSharedUsersByCanvasId(canvasId);
        return ResponseEntity.ok(canvasList);
    }

    @PostMapping
    public ResponseEntity<Void> shareCanvas(@RequestBody CanvasShare canvasShare) {
        boolean success = canvasShareService.shareCanvas(canvasShare);
        if (!success)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{shareId}")
    public ResponseEntity<Void> deleteShare(@PathVariable("shareId") int shareId) {
        boolean success = canvasShareService.deleteShare(shareId);
        if(!success)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.ok().build();
    }
    @PutMapping
    public ResponseEntity<Void> updateShare(@RequestBody CanvasShare canvasShare) {
        boolean success = canvasShareService.updateShare(canvasShare);
        if (!success)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.ok().build();
    }
}
