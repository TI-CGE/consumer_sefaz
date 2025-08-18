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

        const lines = content.split('\n').filter(line => line.trim() !== '');
        const html = lines.map(line => `<div class="log-line">${this.escapeHtml(line)}</div>`).join('');
        
        logElement.innerHTML = html;
        
        if (this.autoScroll) {
            logElement.scrollTop = logElement.scrollHeight;
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

        const fragment = document.createDocumentFragment();
        lines.forEach(line => {
            if (line.trim() !== '') {
                const div = document.createElement('div');
                div.className = 'log-line';
                div.textContent = line;
                fragment.appendChild(div);
            }
        });

        logElement.appendChild(fragment);

        // Update line count
        const currentLines = logElement.querySelectorAll('.log-line').length;
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
