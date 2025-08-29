<template>
    <div class="note-page">
        <h1>个人笔记</h1>
        <div class="note-app">
            <el-container style="height: 100vh;">
                <!-- 左侧笔记列表 -->
                <el-aside width="260px" class="note-list-aside">
                    <div class="aside-header">
                        <span>我的笔记</span>
                        <el-button type="primary" icon="el-icon-plus" size="mini" @click="createNewNote">新建</el-button>
                    </div>
                    <el-menu :default-active="String(selectedNoteId)" class="note-list-menu" @select="handleSelectNote">
                        <el-menu-item v-for="note in notes" :key="note.id || 'temp'" :index="String(note.id)">
                            <div class="note-item">
                                <span class="note-title">{{ note.title || '未命名笔记' }}</span>
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
                            <el-button type="success" size="mini" @click="summarizeNote">AI总结</el-button>
                        </div>
                        <mavon-editor v-model="selectedNote.content" style="height: 60vh;" />
                        <!-- 总结结果展示区域 -->
                        <el-card v-if="selectedNote.summary" class="note-summary" style="margin-top: 15px;">
                            <h3>总结</h3>
                            <p>{{ selectedNote.summary }}</p>
                        </el-card>
                    </div>
                    <div v-else class="empty-note">
                        <el-empty description="请选择或新建一条笔记" />
                    </div>
                </el-main>
            </el-container>
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
            };
        },
        computed: {
            selectedNote() {
                return this.notes.find(n => String(n.id) === String(this.selectedNoteId));
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

            // AI总结笔记
            async summarizeNote() {
                if (!this.selectedNote || !this.selectedNote.id) {
                    this.$message.warning('请先保存笔记，再生成总结');
                    return;
                }

                try {
                    const result = await http.post(`/notes/${this.selectedNote.id}/summary`);
                    if (result.code === 200) {
                        this.selectedNote.summary = result.data.summary;
                        this.$message.success('总结成功');
                    } else {
                        this.$message.error(result.msg || '总结失败');
                    }
                } catch (error) {
                    console.error('总结笔记错误:', error);
                    this.$message.error('总结失败');
                }
            },

            // 选中笔记
            async handleSelectNote(id) {
                // 如果当前是新建未保存的空笔记，切换时删除
                const tempNoteIndex = this.notes.findIndex(n => !n.id);
                if (tempNoteIndex !== -1) {
                    this.notes.splice(tempNoteIndex, 1);
                }

                try {
                    const result = await http.get(`/notes/${id}`);
                    if (result.code === 200) {
                        this.selectedNoteId = id;
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

            // 新建笔记（临时）
            createNewNote() {
                if (this.notes.some(n => !n.id)) {
                    this.selectedNoteId = null;
                    return;
                }
                const tempNote = {
                    id: null, // 后端保存后才有
                    title: '',
                    content: ''
                };
                this.notes.push(tempNote);
                this.selectedNoteId = null;
            },

            // 保存笔记
            async saveNote() {
                if (!this.selectedNote) return;

                try {
                    if (!this.selectedNote.id) {
                        // 新建
                        const result = await http.post('/notes', {
                            title: this.selectedNote.title,
                            content: this.selectedNote.content,
                        });
                        if (result.code === 200) {
                            const index = this.notes.findIndex(n => n === this.selectedNote);
                            if (index !== -1) {
                                this.notes[index] = result.data;
                            }
                            this.selectedNoteId = result.data.id;
                            this.$message.success('新建笔记成功');
                        }
                    } else {
                        // 更新
                        const result = await http.put(`/notes/${this.selectedNote.id}`, this.selectedNote);
                        if (result.code === 200) {
                            const index = this.notes.findIndex(n => n.id === this.selectedNote.id);
                            if (index !== -1) {
                                this.notes[index].title = this.selectedNote.title;
                                this.notes[index].content = this.selectedNote.content;
                            }
                            this.$message.success('保存成功');
                        }
                    }
                } catch (error) {
                    console.error('保存笔记错误:', error);
                    this.$message.error('保存失败');
                }
            },

            // 删除笔记
            async handleDeleteNote(id) {
                // 删除临时笔记（未保存）
                if (!id) {
                    this.notes = this.notes.filter(note => note.id);
                    this.selectedNoteId = this.notes.length ? this.notes[0].id : null;
                    return;
                }

                try {
                    await this.$confirm('确认删除这条笔记吗？', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    });

                    const result = await http.delete(`/notes/${id}`);
                    if (result.code === 200) {
                        this.notes = this.notes.filter(note => note.id !== id);
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