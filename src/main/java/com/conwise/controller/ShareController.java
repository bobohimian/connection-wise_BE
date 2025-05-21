package com.conwise.controller;

import com.conwise.model.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<Canvas>>> getCanvasShareByUserId(@PathVariable("userId") int userId) {
        return ResponseEntity.ok(canvasShareService.getCanvasShareByUserId(userId));
    }

    @GetMapping("/{canvasId}")
    public ResponseEntity<ApiResponse<List<User>>> getSharedUsersByCanvasId(@PathVariable("canvasId") int canvasId) {
        return ResponseEntity.ok(canvasShareService.getSharedUsersByCanvasId(canvasId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> shareCanvas(@RequestBody CanvasShare canvasShare) {
        return ResponseEntity.ok(canvasShareService.shareCanvas(canvasShare));
    }
    @DeleteMapping("/{shareId}")
    public ResponseEntity<ApiResponse<Void>> deleteShare(@PathVariable("shareId") int shareId) {
        return ResponseEntity.ok(canvasShareService.deleteShare(shareId));
    }
    @PutMapping
    public ResponseEntity<ApiResponse<Void>> updateShare(@RequestBody CanvasShare canvasShare) {
        return ResponseEntity.ok(canvasShareService.updateShare(canvasShare));
    }
}
