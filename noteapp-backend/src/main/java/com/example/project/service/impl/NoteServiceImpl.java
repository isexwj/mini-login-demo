package com.example.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.project.common.ex.BusinessException;
import com.example.project.entity.Note;
import com.example.project.mapper.NoteMapper;
import com.example.project.service.NoteService;
import com.example.project.service.UserService;
import com.example.project.utils.AISummaryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteMapper noteMapper;
    private final AISummaryUtil summaryUtil;

    @Override
    public List<Note> getNoteList() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Long userId = (Long) attributes.getRequest().getAttribute("X-User-Id");
        
        return noteMapper.selectList(new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, userId)
                .orderByDesc(Note::getUpdateTime));
    }

    @Override
    public Note getNote(Long id) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Long userId = (Long) attributes.getRequest().getAttribute("X-User-Id");

        Note note = noteMapper.selectOne(new LambdaQueryWrapper<Note>()
                .eq(Note::getId, id)
                .eq(Note::getUserId, userId));
        if (note == null) {
            throw new BusinessException("笔记不存在");
        }
        return note;
    }

    @Override
    public Note createNote(Note note) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Long userId = (Long) attributes.getRequest().getAttribute("X-User-Id");
        
        note.setUserId(userId);
        noteMapper.insert(note);
        return note;
    }

    @Override
    public Note updateNote(Long id, Note updateNote) {
        // 获取当前用户ID并检查权限
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Long userId = (Long) attributes.getRequest().getAttribute("X-User-Id");
        
        // 检查笔记是否存在且属于当前用户
        Note existingNote = noteMapper.selectOne(new LambdaQueryWrapper<Note>()
                .eq(Note::getId, id)
                .eq(Note::getUserId, userId));
        if (existingNote == null) {
            throw new BusinessException("笔记不存在");
        }

        // 更新笔记内容
        existingNote.setTitle(updateNote.getTitle());
        existingNote.setContent(updateNote.getContent());
        noteMapper.updateById(existingNote);
        return existingNote;
    }

    @Override
    public void deleteNote(Long id) {
        // 获取当前用户ID并检查权限
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Long userId = (Long) attributes.getRequest().getAttribute("X-User-Id");
        
        // 检查笔记是否存在且属于当前用户
        Note existingNote = noteMapper.selectOne(new LambdaQueryWrapper<Note>()
                .eq(Note::getId, id)
                .eq(Note::getUserId, userId));
        if (existingNote == null) {
            throw new BusinessException("笔记不存在");
        }

        noteMapper.deleteById(id);
    }

    @Override
    public String summarizeNote(Long id) {
        // 1. 获取当前用户 ID
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Long userId = (Long) attributes.getRequest().getAttribute("X-User-Id");

        // 2. 查笔记，校验权限
        Note existingNote = noteMapper.selectOne(new LambdaQueryWrapper<Note>()
                .eq(Note::getId, id)
                .eq(Note::getUserId, userId));
        if (existingNote == null) {
            throw new BusinessException("笔记不存在");
        }

        // 3. 调用总结逻辑
        String summary = summaryUtil.summarize(existingNote.getContent());

        // 4. 更新数据库
        existingNote.setSummary(summary);
        noteMapper.updateById(existingNote);

        // 5. 返回总结
        return summary;
    }
}