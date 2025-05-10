package com.conwise.controller;

import com.conwise.model.Canvas;
import com.conwise.model.CanvasShare;
import com.conwise.model.User;
import com.conwise.service.CanvasService;
import com.conwise.service.CanvasShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Canvas> getCanvasById(@PathVariable int id) {
        Canvas canvas = canvasService.getCanvasById(id);
        if (canvas != null) {
            return ResponseEntity.ok(canvas);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Canvas>> getCanvasesByUserId(@PathVariable int userId) {
        return ResponseEntity.ok(canvasService.getCanvasesByUserId(userId));
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<Canvas> createCanvas(@PathVariable int userId) {
        boolean created = canvasService.createCanvas(userId);
        if (created) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateCanvas(@RequestBody Canvas canvas) {
        boolean updated = canvasService.updateCanvas(canvas);
        if (updated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{canvasId}")
    public ResponseEntity<Void> deleteCanvas(@PathVariable("canvasId") int canvasId) {
        boolean deleted = canvasService.deleteCanvas(canvasId);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}