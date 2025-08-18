/**
 * Monitor de Logs - SEFAZ Transparency Consumer
 * Interface web para monitoramento de logs em tempo real
 */

class LogMonitor {
    constructor() {
        this.eventSource = null;
        this.isConnected = false;
        this.isPaused = false;
        this.autoScroll = true;
        this.filters = {};
        this.logData = {};
        this.totalLines = 0;
        
        this.logFiles = ['simple.log', 'application.log', 'errors.log', 'operations.md'];
        
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.loadInitialData();
        this.connectToStream();
        this.applyTheme();
    }

    setupEventListeners() {
        // Theme toggle
        document.getElementById('themeToggle').addEventListener('click', () => {
            this.toggleTheme();
        });

        // Pause/Resume
        document.getElementById('pauseBtn').addEventListener('click', () => {
            this.togglePause();
        });

        // Clear logs
        document.getElementById('clearBtn').addEventListener('click', () => {
            this.clearAllLogs();
        });

        // Refresh
        document.getElementById('refreshBtn').addEventListener('click', () => {
            this.refreshLogs();
        });

        // Auto-scroll toggle
        document.getElementById('autoScrollToggle').addEventListener('change', (e) => {
            this.autoScroll = e.target.checked;
        });

        // Window visibility change
        document.addEventListener('visibilitychange', () => {
            if (document.hidden) {
                this.disconnect();
            } else {
                this.connectToStream();
            }
        });

        // Before unload
        window.addEventListener('beforeunload', () => {
            this.disconnect();
        });
    }

    async loadInitialData() {
        try {
            // Load log info
            const infoResponse = await fetch('/logs/info');
            const info = await infoResponse.json();
            
            this.updateStats(info);

            // Load initial content for each log file
            for (const fileName of this.logFiles) {
                await this.loadLogContent(fileName);
            }

        } catch (error) {
            console.error('Erro ao carregar dados iniciais:', error);
            this.showToast('Erro ao carregar dados iniciais', 'error');
        }
    }

    async loadLogContent(fileName, lines = 100) {
        try {
            const response = await fetch(`/logs/${fileName}?lines=${lines}`);
            const data = await response.json();
            
            if (data.error) {
                this.showError(fileName, data.error);
                return;
            }

            this.logData[fileName] = data;
            this.displayLogContent(fileName, data.content);
            this.updateLineCount(fileName, data.lines);

        } catch (error) {
            console.error(`Erro ao carregar ${fileName}:`, error);
            this.showError(fileName, 'Erro ao carregar arquivo');
        }
    }

    connectToStream() {
        if (this.eventSource) {
            this.eventSource.close();
        }

        try {
            this.eventSource = new EventSource('/logs/stream');
            
            this.eventSource.onopen = () => {
                this.isConnected = true;
                this.updateConnectionStatus();
                console.log('Conectado ao stream de logs');
            };

            this.eventSource.onmessage = (event) => {
                this.handleStreamMessage(event);
            };

            this.eventSource.addEventListener('connected', (event) => {
                const data = JSON.parse(event.data);
                console.log('Stream conectado:', data);
                this.showToast('Conectado ao stream de logs', 'success');
            });

            this.eventSource.addEventListener('logUpdate', (event) => {
                if (!this.isPaused) {
                    this.handleLogUpdate(event);
                }
            });

            this.eventSource.onerror = (error) => {
                console.error('Erro no stream:', error);
                this.isConnected = false;
                this.updateConnectionStatus();
                
                // Reconectar após 5 segundos
                setTimeout(() => {
                    if (!this.isConnected) {
                        this.connectToStream();
                    }
                }, 5000);
            };

        } catch (error) {
            console.error('Erro ao conectar stream:', error);
            this.showToast('Erro ao conectar stream', 'error');
        }
    }

    handleLogUpdate(event) {
        try {
            const data = JSON.parse(event.data);
            const { fileName, lines, timestamp } = data;
            
            if (lines && lines.length > 0) {
                this.appendLogLines(fileName, lines);
                this.updateLastUpdate(timestamp);
                this.totalLines += lines.length;
                this.updateTotalLines();
            }

        } catch (error) {
            console.error('Erro ao processar atualização:', error);
        }
    }

    displayLogContent(fileName, content) {
        const logElement = document.getElementById(`log-${fileName}`);
        if (!logElement) return;

        if (!content || content.trim() === '') {
            logElement.innerHTML = '<div class="loading">Nenhum log disponível</div>';
            return;
        }

        // Check if this is the operations.md file for Markdown rendering
        if (fileName === 'operations.md') {
            this.displayMarkdownContent(logElement, content);
        } else {
            this.displayPlainTextContent(logElement, content);
        }

        if (this.autoScroll) {
            logElement.scrollTop = logElement.scrollHeight;
        }
    }

    displayPlainTextContent(logElement, content) {
        const lines = content.split('\n').filter(line => line.trim() !== '');
        const html = lines.map(line => {
            const decodedLine = this.decodeUtf8(line);
            return `<div class="log-line">${this.escapeHtml(decodedLine)}</div>`;
        }).join('');
        logElement.innerHTML = html;
    }

    displayMarkdownContent(logElement, content) {
        try {
            // Use marked.js to render Markdown if available
            if (typeof marked !== 'undefined') {
                const renderedHtml = marked.parse(content);
                logElement.innerHTML = `<div class="markdown-content">${renderedHtml}</div>`;
            } else {
                // Fallback to plain text if marked.js is not available
                this.displayPlainTextContent(logElement, content);
            }
        } catch (error) {
            console.error('Erro ao renderizar Markdown:', error);
            // Fallback to plain text on error
            this.displayPlainTextContent(logElement, content);
        }
    }

    appendLogLines(fileName, lines) {
        const logElement = document.getElementById(`log-${fileName}`);
        if (!logElement) return;

        // Remove loading message if present
        const loading = logElement.querySelector('.loading');
        if (loading) {
            loading.remove();
        }

        // For operations.md, we need to re-render the entire content as Markdown
        if (fileName === 'operations.md') {
            // Get existing content and append new lines
            const existingContent = this.getExistingPlainContent(logElement);
            const newContent = existingContent + '\n' + lines.join('\n');
            this.displayMarkdownContent(logElement, newContent);
        } else {
            // For other files, append as plain text lines
            const fragment = document.createDocumentFragment();
            lines.forEach(line => {
                if (line.trim() !== '') {
                    const div = document.createElement('div');
                    div.className = 'log-line';
                    const decodedLine = this.decodeUtf8(line);
                    div.textContent = decodedLine;
                    fragment.appendChild(div);
                }
            });

            logElement.appendChild(fragment);
        }

        // Update line count
        const currentLines = fileName === 'operations.md' ?
            this.countMarkdownLines(logElement) :
            logElement.querySelectorAll('.log-line').length;
        this.updateLineCount(fileName, currentLines);

        // Auto-scroll if enabled
        if (this.autoScroll) {
            logElement.scrollTop = logElement.scrollHeight;
        }

        // Apply current filter if any
        const filter = this.filters[fileName];
        if (filter) {
            this.applyFilter(fileName, filter);
        }
    }

    getExistingPlainContent(logElement) {
        // Extract plain text content from markdown or log lines
        const markdownContent = logElement.querySelector('.markdown-content');
        if (markdownContent) {
            // For markdown, we need to get the original text
            // This is a simplified approach - in a real app you'd store the original content
            return markdownContent.textContent || '';
        } else {
            // For plain text logs
            const lines = logElement.querySelectorAll('.log-line');
            return Array.from(lines).map(line => line.textContent).join('\n');
        }
    }

    countMarkdownLines(logElement) {
        const markdownContent = logElement.querySelector('.markdown-content');
        if (markdownContent) {
            // Count lines in markdown content (approximate)
            return (markdownContent.textContent || '').split('\n').length;
        }
        return 0;
    }

    updateConnectionStatus() {
        const indicator = document.getElementById('statusIndicator');
        const text = document.getElementById('statusText');
        
        if (this.isConnected) {
            indicator.className = 'status-indicator online';
            text.textContent = 'Conectado';
        } else {
            indicator.className = 'status-indicator offline';
            text.textContent = 'Desconectado';
        }
    }

    updateStats(info) {
        if (info.activeConnections !== undefined) {
            document.getElementById('activeConnections').textContent = info.activeConnections;
        }
    }

    updateLastUpdate(timestamp) {
        const date = new Date(timestamp);
        const formatted = date.toLocaleTimeString('pt-BR');
        document.getElementById('lastUpdate').textContent = formatted;
    }

    updateLineCount(fileName, count) {
        const element = document.getElementById(`${fileName.replace('.', '')}Count`);
        if (element) {
            element.textContent = `${count} linhas`;
        }
    }

    updateTotalLines() {
        document.getElementById('totalLines').textContent = this.totalLines;
    }

    toggleTheme() {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        
        const icon = document.querySelector('#themeToggle .icon');
        icon.textContent = newTheme === 'dark' ? '☀️' : '🌙';
    }

    applyTheme() {
        const savedTheme = localStorage.getItem('theme') || 'light';
        document.documentElement.setAttribute('data-theme', savedTheme);
        
        const icon = document.querySelector('#themeToggle .icon');
        icon.textContent = savedTheme === 'dark' ? '☀️' : '🌙';
    }

    togglePause() {
        this.isPaused = !this.isPaused;
        const btn = document.getElementById('pauseBtn');
        const icon = btn.querySelector('.icon');
        
        if (this.isPaused) {
            icon.textContent = '▶️';
            btn.title = 'Retomar atualizações';
            this.showToast('Atualizações pausadas', 'warning');
        } else {
            icon.textContent = '⏸️';
            btn.title = 'Pausar atualizações';
            this.showToast('Atualizações retomadas', 'success');
        }
    }

    clearAllLogs() {
        if (confirm('Tem certeza que deseja limpar todos os logs da visualização?')) {
            this.logFiles.forEach(fileName => {
                const logElement = document.getElementById(`log-${fileName}`);
                if (logElement) {
                    logElement.innerHTML = '<div class="loading">Logs limpos</div>';
                }
                this.updateLineCount(fileName, 0);
            });
            
            this.totalLines = 0;
            this.updateTotalLines();
            this.showToast('Logs limpos da visualização', 'success');
        }
    }

    async refreshLogs() {
        const btn = document.getElementById('refreshBtn');
        const icon = btn.querySelector('.icon');
        
        // Animate refresh button
        icon.style.animation = 'spin 1s linear infinite';
        
        try {
            await this.loadInitialData();
            this.showToast('Logs atualizados', 'success');
        } catch (error) {
            this.showToast('Erro ao atualizar logs', 'error');
        } finally {
            icon.style.animation = '';
        }
    }

    showError(fileName, message) {
        const logElement = document.getElementById(`log-${fileName}`);
        if (logElement) {
            logElement.innerHTML = `<div class="loading" style="color: var(--error-color);">❌ ${message}</div>`;
        }
    }

    showToast(message, type = 'info') {
        const container = document.getElementById('toastContainer');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>${message}</span>
                <button onclick="this.parentElement.parentElement.remove()" style="background: none; border: none; cursor: pointer; color: var(--text-muted);">✕</button>
            </div>
        `;
        
        container.appendChild(toast);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (toast.parentElement) {
                toast.remove();
            }
        }, 5000);
    }

    disconnect() {
        if (this.eventSource) {
            this.eventSource.close();
            this.eventSource = null;
        }
        this.isConnected = false;
        this.updateConnectionStatus();
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    /**
     * Decodifica caracteres UTF-8 mal codificados
     */
    decodeUtf8(text) {
        try {
            // Tenta decodificar caracteres UTF-8 mal codificados
            // Converte sequências como â\x9c\x85 de volta para ✅
            return text
                .replace(/â\x9c\x85/g, '✅')  // ✅ check mark
                .replace(/ð\x9f\x9a\x80/g, '🚀')  // 🚀 rocket
                .replace(/â\x9d\x8c/g, '❌')  // ❌ cross mark
                .replace(/ð\x9f\x93\x8b/g, '📋')  // 📋 clipboard
                .replace(/ð\x9f\x93\x9d/g, '📝')  // 📝 memo
                .replace(/â\x9a\x99ï¸\x8f/g, '⚙️')  // ⚙️ gear
                .replace(/ð\x9f\x93\x8a/g, '📊')  // 📊 bar chart
                .replace(/ð\x9f\x94\x8d/g, '🔍')  // 🔍 magnifying glass
                .replace(/ð\x9f\x92\x be/g, '💾')  // 💾 floppy disk
                .replace(/ð\x9f\x97\x91ï¸\x8f/g, '🗑️')  // 🗑️ wastebasket
                .replace(/ð\x9f\x94\x84/g, '🔄')  // 🔄 counterclockwise arrows
                .replace(/ð\x9f\x8c\x99/g, '🌙')  // 🌙 crescent moon
                .replace(/â\x8f\xb8ï¸\x8f/g, '⏸️')  // ⏸️ pause button
                .replace(/â\x96\xb6ï¸\x8f/g, '▶️')  // ▶️ play button
                .replace(/ð\x9f\x94\x92/g, '🔒')  // 🔒 locked
                .replace(/ð\x9f\x94\x93/g, '🔓')  // 🔓 unlocked
                .replace(/â\x9a\xa0/g, '⚠️')  // ⚠️ warning sign
                .replace(/ð\x9f\x9a\xa8/g, '🚨')  // 🚨 police car light
                .replace(/ð\x9f\x94\xa5/g, '🔥')  // 🔥 fire
                .replace(/â\x9c\x8d/g, '✍️')  // ✍️ writing hand
                .replace(/ð\x9f\x93\x88/g, '📈')  // 📈 chart increasing
                .replace(/ð\x9f\x93\x89/g, '📉')  // 📉 chart decreasing
                .replace(/ð\x9f\x92\xa1/g, '💡')  // 💡 light bulb
                .replace(/ð\x9f\x8e\xaf/g, '🎯')  // 🎯 direct hit
                .replace(/ð\x9f\x8e\x89/g, '🎉')  // 🎉 party popper
                .replace(/ð\x9f\x91\x8d/g, '👍')  // 👍 thumbs up
                .replace(/ð\x9f\x91\x8e/g, '👎')  // 👎 thumbs down
                .replace(/ð\x9f\x92\xaf/g, '💯')  // 💯 hundred points
                .replace(/ð\x9f\x86\x97/g, '🆗')  // 🆗 OK button
                .replace(/ð\x9f\x86\x95/g, '🆕')  // 🆕 NEW button
                .replace(/ð\x9f\x86\x99/g, '🆙')  // 🆙 UP! button
                // Adiciona mais mapeamentos conforme necessário
                ;
        } catch (error) {
            console.warn('Erro ao decodificar UTF-8:', error);
            return text;
        }
    }
}

// Global functions for HTML event handlers
function toggleFilter(fileName) {
    const filterBar = document.getElementById(`filter-${fileName}`);
    const isVisible = filterBar.style.display !== 'none';
    filterBar.style.display = isVisible ? 'none' : 'block';
    
    if (!isVisible) {
        const input = filterBar.querySelector('input');
        input.focus();
    }
}

function filterLogs(fileName, filterText) {
    window.logMonitor.filters[fileName] = filterText.toLowerCase();
    window.logMonitor.applyFilter(fileName, filterText.toLowerCase());
}

function clearFilter(fileName) {
    const filterBar = document.getElementById(`filter-${fileName}`);
    const input = filterBar.querySelector('input');
    input.value = '';
    delete window.logMonitor.filters[fileName];
    window.logMonitor.applyFilter(fileName, '');
    filterBar.style.display = 'none';
}

function downloadLog(fileName) {
    const logElement = document.getElementById(`log-${fileName}`);
    const lines = Array.from(logElement.querySelectorAll('.log-line')).map(el => el.textContent);
    const content = lines.join('\n');
    
    const blob = new Blob([content], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    
    window.logMonitor.showToast(`Download de ${fileName} iniciado`, 'success');
}

// Add filter functionality to LogMonitor prototype
LogMonitor.prototype.applyFilter = function(fileName, filterText) {
    const logElement = document.getElementById(`log-${fileName}`);

    if (fileName === 'operations.md') {
        // For Markdown content, filter the entire content
        const markdownContent = logElement.querySelector('.markdown-content');
        if (markdownContent) {
            const text = markdownContent.textContent.toLowerCase();
            const matches = !filterText || text.includes(filterText);

            if (matches && filterText) {
                // Highlight matching text in markdown
                this.highlightTextInMarkdown(markdownContent, filterText);
            } else if (!filterText) {
                // Remove highlights when filter is cleared
                this.removeHighlightsFromMarkdown(markdownContent);
            }

            markdownContent.style.display = matches ? 'block' : 'none';
        }
    } else {
        // For plain text logs
        const lines = logElement.querySelectorAll('.log-line');

        lines.forEach(line => {
            const text = line.textContent.toLowerCase();
            const matches = !filterText || text.includes(filterText);
            line.style.display = matches ? 'block' : 'none';

            if (matches && filterText) {
                line.classList.add('highlight');
            } else {
                line.classList.remove('highlight');
            }
        });
    }
};

LogMonitor.prototype.highlightTextInMarkdown = function(element, searchText) {
    if (!searchText) return;

    // Simple text highlighting for markdown content
    const walker = document.createTreeWalker(
        element,
        NodeFilter.SHOW_TEXT,
        null,
        false
    );

    const textNodes = [];
    let node;
    while (node = walker.nextNode()) {
        textNodes.push(node);
    }

    textNodes.forEach(textNode => {
        const text = textNode.textContent;
        const regex = new RegExp(`(${searchText})`, 'gi');
        if (regex.test(text)) {
            const highlightedText = text.replace(regex, '<mark>$1</mark>');
            const span = document.createElement('span');
            span.innerHTML = highlightedText;
            textNode.parentNode.replaceChild(span, textNode);
        }
    });
};

LogMonitor.prototype.removeHighlightsFromMarkdown = function(element) {
    const marks = element.querySelectorAll('mark');
    marks.forEach(mark => {
        mark.outerHTML = mark.innerHTML;
    });
};

// Add CSS for spin animation
const style = document.createElement('style');
style.textContent = `
    @keyframes spin {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }
`;
document.head.appendChild(style);

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    window.logMonitor = new LogMonitor();
});
