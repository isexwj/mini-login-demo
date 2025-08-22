package com.example.project.service;

import com.example.project.entity.Note;
import java.util.List;

public interface NoteService {
    List<Note> getNoteList();
    Note getNote(Long id);
    Note createNote(Note note);
    Note updateNote(Long id, Note note);
    void deleteNote(Long id);
}