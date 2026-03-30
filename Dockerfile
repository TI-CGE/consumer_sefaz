FROM oven/bun:1 AS base
WORKDIR /app

FROM base AS install
COPY package.json bun.lock* ./
RUN bun install --frozen-lockfile --production

FROM base AS release
COPY --from=install /app/node_modules node_modules
COPY src ./src
COPY package.json tsconfig.json ./

RUN mkdir -p /app/logs && chmod 777 /app/logs

ENV NODE_ENV=docker
ENV LOG_PATH=/app/logs
ENV PORT=8080

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=30s \
  CMD curl -f http://localhost:8080/health || exit 1

CMD ["bun", "run", "src/index.ts"]
