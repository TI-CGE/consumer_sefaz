/**
 * Opções de log de console para consumo via cron e rotas manuais (/scheduler).
 * Cabeçalho, barra de progresso e GET no console; fim compacto (linha única).
 */
export const SCHEDULER_CONSUME_CONSOLE = {
  quietBanners: false,
  quietEndBanner: true,
  verboseRequestLog: true,
} as const;
