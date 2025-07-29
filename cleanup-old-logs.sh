#!/bin/bash

# Script para limpar estrutura de logs antiga e preparar para o sistema simplificado

echo "🧹 Limpando estrutura de logs antiga..."

# Backup dos logs atuais (opcional)
if [ -d "logs" ]; then
    echo "📦 Criando backup dos logs atuais..."
    timestamp=$(date +"%Y%m%d_%H%M%S")
    mkdir -p "logs_backup_$timestamp"
    cp -r logs/* "logs_backup_$timestamp/" 2>/dev/null || true
    echo "✅ Backup criado em: logs_backup_$timestamp/"
fi

# Remover diretórios de logs especializados
echo "🗑️ Removendo diretórios de logs especializados..."
rm -rf logs/api
rm -rf logs/application
rm -rf logs/contracts
rm -rf logs/database
rm -rf logs/performance
rm -rf logs/security

# Manter apenas os arquivos de log principais
echo "📁 Reorganizando estrutura de logs..."
if [ -f "logs/master.log" ]; then
    mv logs/master.log logs/application.log 2>/dev/null || true
fi

# Criar estrutura simplificada
mkdir -p logs

# Verificar se os arquivos principais existem, se não, criar vazios
touch logs/application.log
touch logs/errors.log

echo "✅ Estrutura de logs simplificada criada:"
echo "   📄 logs/application.log - Log principal"
echo "   📄 logs/errors.log - Log de erros"
echo ""
echo "🎯 Sistema de logging simplificado pronto!"
echo ""
echo "📋 Próximos passos:"
echo "   1. Reiniciar a aplicação"
echo "   2. Verificar os novos logs em logs/application.log"
echo "   3. Remover classes de logging antigas (opcional)"
echo ""
echo "💡 Para mais informações, consulte: SIMPLIFIED_LOGGING_SYSTEM.md"
