<template>
    <div class="note-page">
        <h1>个人笔记</h1>
        <!-- 这里将来可以添加笔记相关的内容 -->
        <div class="note-app">
            <el-container style="height: 100vh;">
                <!-- 左侧笔记列表 -->
                <el-aside width="260px" class="note-list-aside">
                    <div class="aside-header">
                        <span>我的笔记</span>
                        <el-button type="primary" icon="el-icon-plus" size="mini"
                            @click="showAddDialog = true">新建</el-button>
                    </div>
                    <el-menu :default-active="String(selectedNoteId)" class="note-list-menu" @select="handleSelectNote">
                        <el-menu-item v-for="note in notes" :key="note.id" :index="String(note.id)">
                            <div class="note-item">
                                <span class="note-title">{{ note.title }}</span>
                                <div class="note-actions">
                                    <el-button type="text" icon="el-icon-delete" size="mini" 
                                        @click.stop="handleDeleteNote(note.id)">删除</el-button>
                                </div>
                            </div>
                        </el-menu-item>
                    </el-menu>
                </el-aside>

                <!-- 中间内容区 -->
                <el-main class="note-main">
                    <div v-if="selectedNote">
                        <div class="note-title-bar">
                            <el-input v-model="selectedNote.title" placeholder="请输入标题" class="note-title-input"
                                size="medium" />
                            <el-button type="primary" size="mini" @click="saveNote">保存</el-button>
                        </div>
                        <mavon-editor v-model="selectedNote.content" style="height: 60vh;" />
                    </div>
                    <div v-else class="empty-note">
                        <el-empty description="请选择或新建一条笔记" />
                    </div>
                </el-main>
            </el-container>

            <!-- 新建笔记弹窗 -->
            <el-dialog title="新建笔记" :visible.sync="showAddDialog" width="400px">
                <el-form :model="newNote" label-width="60px">
                    <el-form-item label="标题">
                        <el-input v-model="newNote.title" placeholder="请输入标题" />
                    </el-form-item>
                    <el-form-item label="内容">
                        <mavon-editor v-model="newNote.content" style="min-height: 200px;" />
                    </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="showAddDialog = false">取 消</el-button>
                    <el-button type="primary" @click="addNote">确 定</el-button>
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script>
import { http } from '@/services/httpClient';

export default {
    name: 'Note',
    data() {
        return {
            notes: [],
            loading: false,
            selectedNoteId: null,
            showAddDialog: false,
            newNote: {
                title: '',
                content: '',
            },
        };
    },
    computed: {
        selectedNote() {
            return this.notes.find(n => n.id === Number(this.selectedNoteId));
        },
    },
    methods: {
        // 获取笔记列表
        async loadNotes() {
            try {
                this.loading = true;
                const result = await http.get('/notes');
                if (result.code === 200) {
                    this.notes = result.data;
                    // 如果有笔记且没有选中笔记，默认选中第一个
                    if (this.notes.length && !this.selectedNoteId) {
                        this.selectedNoteId = this.notes[0].id;
                    }
                } else {
                    this.$message.error(result.msg || '获取笔记列表失败');
                }
            } catch (error) {
                console.error('获取笔记列表错误:', error);
                this.$message.error('获取笔记列表失败');
            } finally {
                this.loading = false;
            }
        },

        // 选中笔记
        async handleSelectNote(id) {
            try {
                const result = await http.get(`/notes/${id}`);
                if (result.code === 200) {
                    this.selectedNoteId = id;
                    // 更新笔记内容
                    const index = this.notes.findIndex(n => n.id === id);
                    if (index !== -1) {
                        this.notes[index] = result.data;
                    }
                } else {
                    this.$message.error(result.msg || '获取笔记详情失败');
                }
            } catch (error) {
                console.error('获取笔记详情错误:', error);
                this.$message.error('获取笔记详情失败');
            }
        },

        // 新建笔记
        async addNote() {
            if (!this.newNote.title) {
                this.$message.warning('请输入标题');
                return;
            }

            try {
                const result = await http.post('/notes', this.newNote);
                if (result.code === 200) {
                    const newNote = result.data;
                    this.notes.push(newNote);
                    this.selectedNoteId = newNote.id;
                    this.showAddDialog = false;
                    this.newNote.title = '';
                    this.newNote.content = '';
                    this.$message.success('新建笔记成功');
                } else {
                    this.$message.error(result.msg || '新建笔记失败');
                }
            } catch (error) {
                console.error('新建笔记错误:', error);
                this.$message.error('新建笔记失败');
            }
        },

        // 保存笔记
        // 保存笔记更新
        async saveNote() {
            if (!this.selectedNote) return;

            try {
                const result = await http.put(`/notes/${this.selectedNote.id}`, this.selectedNote);
                if (result.code === 200) {
                    // 更新列表中的笔记标题
                    const index = this.notes.findIndex(n => n.id === this.selectedNote.id);
                    if (index !== -1) {
                        this.notes[index].title = this.selectedNote.title;
                        this.notes[index].content = this.selectedNote.content;
                    }
                    this.$message.success('保存成功');
                } else {
                    this.$message.error(result.msg || '保存失败');
                }
            } catch (error) {
                console.error('保存笔记错误:', error);
                this.$message.error('保存失败');
            }
        },

        // 删除笔记
        async handleDeleteNote(id) {
            try {
                await this.$confirm('确认删除这条笔记吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });

                const result = await http.delete(`/notes/${id}`);
                if (result.code === 200) {
                    // 从列表中移除
                    this.notes = this.notes.filter(note => note.id !== id);
                    // 如果删除的是当前选中的笔记，清空选中状态
                    if (this.selectedNoteId === id) {
                        this.selectedNoteId = this.notes.length ? this.notes[0].id : null;
                    }
                    this.$message.success('删除成功');
                } else {
                    this.$message.error(result.msg || '删除失败');
                }
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('删除笔记错误:', error);
                    this.$message.error('删除失败');
                }
            }
        },
    },

    // 组件创建后加载笔记列表
    created() {
        this.loadNotes();
    },
};
</script>

<style scoped>
    .note-page {
        padding: 40px;
        text-align: center;
    }
    
    .note-app {
        height: calc(100vh - 80px);
        margin: -20px;
        background: #f5f7fa;
    }

    .note-list-aside {
        background: #fff;
        border-right: 1px solid #ebeef5;
        display: flex;
        flex-direction: column;
        padding: 0;
    }

    .aside-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 18px 16px 8px 16px;
        font-weight: bold;
        font-size: 16px;
        border-bottom: 1px solid #ebeef5;
    }

    .note-list-menu {
        border: none;
        flex: 1;
        overflow-y: auto;
    }

    .note-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        width: 100%;
        padding-right: 10px;
    }

    .note-title {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .note-actions {
        display: none;
    }

    .el-menu-item:hover .note-actions {
        display: block;
    }

    .note-main {
        padding: 32px 24px;
        background: #fff;
        min-height: calc(100vh - 80px);
    }

    .note-title-bar {
        display: flex;
        align-items: center;
        margin-bottom: 18px;
        gap: 16px;
    }

    .note-title-input {
        flex: 1;
    }

    .empty-note {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 60vh;
    }

    /* mavon-editor 样式覆盖 */
    .v-note-wrapper {
        min-height: calc(100vh - 250px);
        z-index: 1;
    }

    .el-container {
        height: 100% !important;
    }

    h1 {
        margin-bottom: 20px;
    }
</style>