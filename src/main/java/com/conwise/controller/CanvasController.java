package com.conwise.controller;

import com.conwise.model.Canvas;
import com.conwise.service.CanvasService;
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


    @GetMapping(("user/{useId}"))
    public ResponseEntity<List<Canvas>> getAllCanvases(@PathVariable("useId") int id) {
        return ResponseEntity.ok(canvasService.getAllCanvases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Canvas> getCanvasById(@PathVariable Long id) {
        Canvas canvas = canvasService.getCanvasById(id);
        if (canvas != null) {
            return ResponseEntity.ok(canvas);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Canvas>> getCanvasesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(canvasService.getCanvasesByUserId(userId));
    }
    
    @PostMapping
    public ResponseEntity<Canvas> createCanvas(@RequestBody Canvas canvas) {
        boolean created = canvasService.createCanvas(canvas);
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED).body(canvas);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCanvas(@PathVariable Long id, @RequestBody Canvas canvas) {
        canvas.setId(id);
        boolean updated = canvasService.updateCanvas(canvas);
        if (updated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCanvas(@PathVariable Long id) {
        boolean deleted = canvasService.deleteCanvas(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}