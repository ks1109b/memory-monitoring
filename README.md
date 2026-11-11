# Java Memory Monitoring

Учебный проект на **Java 17 + Spring Boot 3**, демонстрирующий мониторинг использования памяти JVM и HTTP-запросов с помощью Prometheus и Grafana.

Проект демонстрирует:
- создание REST API с различными HTTP-статусами;
- искусственное увеличение потребления памяти JVM;
- сбор пользовательских и системных метрик;
- мониторинг приложения с помощью Prometheus;
- визуализацию метрик в Grafana;
- настройку автоматических правил оповещения.

Основные метрики:

- `jvm_memory_used_bytes` — использование памяти JVM (heap и non-heap).
- `demo_process_resident_memory_bytes` — RSS, объём физической памяти процесса.
- `demo_controller_calls_total` — общее количество вызовов контроллера.
- `http_server_requests_seconds_count` — количество HTTP-запросов.
- `http_server_requests_seconds_sum` — суммарное время обработки HTTP-запросов.
- `jvm_threads_live_threads` — количество активных потоков JVM.

RSS вычисляется по данным `/proc/self/status` внутри контейнера Linux.

## Стек технологий

- Java 17
- Spring Boot 3
- Spring Boot Actuator
- Micrometer + Prometheus
- Grafana
- Docker и Docker Compose
- Maven

## Запуск проекта

Для запуска необходимы Docker и Docker Compose.

Клонировать репозиторий:

```bash
git clone https://github.com/YOUR_USERNAME/java-memory-monitoring.git
cd java-memory-monitoring
```

Запустить все сервисы одной командой:

```bash
docker compose up --build -d
```

Остановка:

```bash
docker compose down
```

## Доступные сервисы

| Сервис | Адрес |
|---|---|
| Spring Boot API | http://localhost:8080 |
| Actuator Prometheus | http://localhost:8080/actuator/prometheus |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |

Dashboard загружается автоматически из файлов конфигурации проекта.

## API endpoints

| Метод | Endpoint | HTTP-код | Описание |
|---|---|---:|---|
| GET | `/api/ok` | 200 | Успешный запрос |
| GET | `/api/missing` | 404 | Возвращает Not Found |
| GET | `/api/error` | 500 | Возвращает Internal Server Error |
| POST | `/api/memory/allocate?mb=256` | 200 | Выделяет память |
| GET | `/api/memory` | 200 | Показывает информацию о выделенной памяти |
| DELETE | `/api/memory/clear` | 200 | Очищает выделенную память |

Пример выделения памяти:

```bash
curl -X POST "http://localhost:8080/api/memory/allocate?mb=256"
```
