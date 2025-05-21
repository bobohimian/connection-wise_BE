package com.conwise.controller;

import com.conwise.model.ApiResponse;
import com.conwise.model.Canvas;
import com.conwise.model.CanvasShare;
import com.conwise.model.User;
import com.conwise.service.CanvasService;
import com.conwise.service.CanvasShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/canvas")
public class CanvasController {

    private final CanvasService canvasService;

    @Autowired
    public CanvasController(CanvasService canvasService) {
        this.canvasService = canvasService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Canvas>> getCanvasById(@PathVariable int id) {
        ApiResponse<Canvas> apiResponse = canvasService.getCanvasById(id);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Canvas>>> getCanvasesByUserId(@PathVariable int userId) {
        return ResponseEntity.ok(canvasService.getCanvasesByUserId(userId));
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<ApiResponse<Void>> createCanvas(@PathVariable int userId) {
        return ResponseEntity.ok(canvasService.createCanvas(userId));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Void>> updateCanvas(@RequestBody Canvas canvas) {
        return ResponseEntity.ok(canvasService.updateCanvas(canvas));
    }

    @DeleteMapping("/{canvasId}")
    public ResponseEntity<ApiResponse<Void>> deleteCanvas(@PathVariable("canvasId") int canvasId) {

        return ResponseEntity.ok(canvasService.deleteCanvas(canvasId));
    }

    @PostMapping("/uploadThumbnail")
    public ResponseEntity<ApiResponse<Void>> uploadThumbnail(@RequestParam("canvasId") int canvasId, @RequestParam("thumbnail") MultipartFile thumbnail) {
        return ResponseEntity.ok(canvasService.saveThumbnail(canvasId, thumbnail));
    }
}