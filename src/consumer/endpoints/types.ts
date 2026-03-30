export interface EndpointDefinition {
  name: string;
  tableName: string;
  url: string;
  nomeDataInicialPadraoFiltro: string | null;
  nomeDataFinalPadraoFiltro: string | null;
  dtAnoPadrao: string | null;
  parametrosRequeridos: boolean;
  requerIteracaoCdGestao: boolean;
  requerIteracaoEmpenhos: boolean;
  requerMultiplasRequisicoes?: boolean;
  multiplasRequisicoesCount?: number;
  multiplasRequisicoesPauseMs?: number;

  responseMapping: Record<string, string | ((item: any) => any)>;

  uniqueConstraintColumns: string[] | null;

  getParametrosTodosAnos: (ugCd: string | null, ano: number, filters?: Record<string, any>) => Record<string, any>;
  getParametrosAtual: (ugCd: string | null, anoAtual: number, mesAtual: number, filters?: Record<string, any>) => Record<string, any>;
}
