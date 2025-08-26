package com.example.project.controller;

import com.example.project.common.Result;
import com.example.project.entity.Note;
import com.example.project.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "笔记管理", description = "笔记相关接口")
@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @Operation(summary = "获取笔记列表")
    @GetMapping
    public Result<List<Note>> getNoteList() {
        return Result.ok(noteService.getNoteList());
    }

    @Operation(summary = "获取笔记详情")
    @GetMapping("/{id}")
    public Result<Note> getNote(@PathVariable Long id) {
        return Result.ok(noteService.getNote(id));
    }

    @Operation(summary = "创建笔记")
    @PostMapping
    public Result<Note> createNote(@RequestBody Note note) {
        return Result.ok(noteService.createNote(note));
    }

    @Operation(summary = "更新笔记")
    @PutMapping("/{id}")
    public Result<Note> updateNote(@PathVariable Long id, @RequestBody Note note) {
        return Result.ok(noteService.updateNote(id, note));
    }

    @Operation(summary = "删除笔记")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return Result.ok();
    }
}