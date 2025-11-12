/**
 * Context Path Manager
 * Detecta automaticamente o contexto da aplicação e fornece funções para construir URLs corretas
 * Funciona tanto em JAR standalone (/) quanto em WAR no Tomcat (/nome-do-war)
 */

(function() {
    'use strict';

    /**
     * Detecta o context path da aplicação
     * @returns {string} O context path (ex: "" para raiz, "/sefaz-transparency-consumer-0.0.1-SNAPSHOT" para WAR)
     */
    function detectContextPath() {
        // Tenta obter do meta tag (se configurado)
        const metaTag = document.querySelector('meta[name="context-path"]');
        if (metaTag && metaTag.content) {
            return metaTag.content;
        }

        // Detecta automaticamente pelo pathname atual
        const pathname = window.location.pathname;
        
        // Se estamos na raiz ou em um arquivo estático direto, não há context path
        if (pathname === '/' || pathname === '' || pathname.match(/^\/[^\/]+\.(html|css|js)$/)) {
            return '';
        }

        // Extrai o primeiro segmento do path como context path
        // Ex: /sefaz-transparency-consumer-0.0.1-SNAPSHOT/index.html -> /sefaz-transparency-consumer-0.0.1-SNAPSHOT
        const match = pathname.match(/^(\/[^\/]+)/);
        if (match) {
            const potentialContext = match[1];
            
            // Verifica se não é um arquivo estático na raiz
            if (!potentialContext.match(/\.(html|css|js|png|jpg|svg)$/)) {
                return potentialContext;
            }
        }

        return '';
    }

    /**
     * Constrói uma URL completa com o context path
     * @param {string} path - O caminho relativo (ex: "/api/logs", "/swagger-ui.html")
     * @returns {string} URL completa com context path
     */
    function buildUrl(path) {
        // Remove barra inicial se existir
        const cleanPath = path.startsWith('/') ? path.substring(1) : path;
        
        // Se não há context path, retorna com barra inicial
        if (!window.APP_CONTEXT_PATH || window.APP_CONTEXT_PATH === '') {
            return '/' + cleanPath;
        }
        
        // Retorna com context path
        return window.APP_CONTEXT_PATH + '/' + cleanPath;
    }

    /**
     * Constrói uma URL de API
     * @param {string} endpoint - O endpoint da API (ex: "logs/stream", "/api/data")
     * @returns {string} URL completa da API
     */
    function buildApiUrl(endpoint) {
        return buildUrl(endpoint);
    }

    /**
     * Constrói uma URL de recurso estático
     * @param {string} resource - O caminho do recurso (ex: "css/styles.css", "/images/logo.png")
     * @returns {string} URL completa do recurso
     */
    function buildResourceUrl(resource) {
        return buildUrl(resource);
    }

    /**
     * Atualiza todos os links e recursos na página para usar o context path correto
     */
    function updatePageUrls() {
        // Atualiza links <a> que começam com /
        document.querySelectorAll('a[href^="/"]').forEach(link => {
            const href = link.getAttribute('href');
            if (href && href !== '/' && !href.startsWith(window.APP_CONTEXT_PATH)) {
                link.setAttribute('href', buildUrl(href));
            }
        });

        // Atualiza imagens que começam com /
        document.querySelectorAll('img[src^="/"]').forEach(img => {
            const src = img.getAttribute('src');
            if (src && !src.startsWith(window.APP_CONTEXT_PATH)) {
                img.setAttribute('src', buildUrl(src));
            }
        });

        // Atualiza scripts que começam com /
        document.querySelectorAll('script[src^="/"]').forEach(script => {
            const src = script.getAttribute('src');
            if (src && !src.startsWith(window.APP_CONTEXT_PATH)) {
                script.setAttribute('src', buildUrl(src));
            }
        });

        // Atualiza CSS que começam com /
        document.querySelectorAll('link[href^="/"]').forEach(link => {
            const href = link.getAttribute('href');
            if (href && !href.startsWith(window.APP_CONTEXT_PATH)) {
                link.setAttribute('href', buildUrl(href));
            }
        });
    }

    // Detecta e armazena o context path globalmente
    window.APP_CONTEXT_PATH = detectContextPath();
    
    // Expõe as funções globalmente
    window.ContextPath = {
        get: function() {
            return window.APP_CONTEXT_PATH;
        },
        buildUrl: buildUrl,
        buildApiUrl: buildApiUrl,
        buildResourceUrl: buildResourceUrl,
        updatePageUrls: updatePageUrls
    };

    // Log para debug (pode ser removido em produção)
    console.log('[ContextPath] Detected context path:', window.APP_CONTEXT_PATH || '(root)');

    // Atualiza URLs quando o DOM estiver pronto
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', updatePageUrls);
    } else {
        updatePageUrls();
    }

})();

