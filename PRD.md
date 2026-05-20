# Product Requirements Document (PRD): 실시간 대용량 주문/체결 및 시세 전파 시스템

## 1. 프로젝트 개요

본 문서는 대형 증권사(예: 한국투자증권)의 백엔드 개발 직무 KPI인 '금융 데이터의 무결성 보장'과 '대용량 트랜잭션의 안정적 처리'를 입증하기 위한 실시간 주문/체결 시스템의 제품 요구사항을 정의합니다. 공모주 청약 등 트래픽 폭증 상황을 가정하여, CQRS 패턴과 Event-Driven 아키텍처를 통해 시스템 가용성 99.99%를 달성하는 것을 목표로 합니다.

## 2. 목표 및 핵심 성과 지표 (KPI)

- **시스템 안정성 (가중치 1위):** \* 장 시작 및 폭발적 트래픽 상황에서 시스템 가용성 99.99% 이상 유지.
  - 외부 연동 장애 시 서킷 브레이커를 통한 전체 시스템 장애 전파 차단.
- **성능 최적화:** \* 대규모 트래픽 병목 해소를 위한 처리량(Throughput) 상향 및 API Latency 최소화.
  - Redis 캐싱 및 분산 락을 통한 데이터베이스 커넥션 풀 보호.
- **데이터 무결성:** \* 동시성 이슈(갱신 손실) 방지를 위한 100% 정합성 보장 (Redis 분산 락 활용).

## 3. 핵심 기능 요구사항

### 3.1. 대용량 주문 접수 및 검증

- **유량 제어 (Rate Limiting):** 악의적 또는 비정상적인 대량 주문 요청을 방어하기 위해 Redis를 활용하여 클라이언트별 초당 API 호출 횟수를 제한합니다.
- **비동기 주문 큐잉:** 주문 요청 수신 시, 기본 검증(잔고/증거금 확인) 후 체결 로직을 즉시 실행하지 않고 Kafka 메시지 큐(`order-requests` 토픽)에 이벤트를 발행하여 클라이언트 응답 지연을 최소화합니다.

### 3.2. 체결 엔진 및 동시성 제어

- **순차적 체결 처리:** Kafka Consumer가 주문 이벤트를 Polling하여 순차적으로 체결을 시도합니다.
- **동시성 제어 (분산 락):** Redisson(Redis 분산 락)을 활용하여 동일 종목/계좌에 대한 동시 접근 시 DB 부하를 최소화하면서 락을 획득하도록 구현합니다. DB 접근 시 낙관적 락(Optimistic Lock)을 병행하여 2중 정합성 검증을 수행할 수 있습니다.
- **CQRS 패턴:** 주문/체결 데이터(Write)와 잔고/시세 데이터(Read)의 저장소 및 처리 흐름을 분리하여 부하를 격리합니다.

### 3.3. 실시간 시세 전파 및 지표 기반 알림

- **실시간 시세 업데이트:** 체결 완료 이벤트(`order-executed` 토픽)를 수신하여 Redis에 최신 호가 및 체결가를 갱신합니다.
- **RSI 및 지정가 알림:** 체결 데이터를 기반으로 RSI(상대강도지수) 등 보조 지표를 실시간 연산하고, 사용자가 설정한 지정가 또는 조건 도달 시 WebSocket/SSE를 통해 클라이언트에 즉시 알림을 푸시합니다.

## 4. 기술 스택

### 4.1. Front-End

| 분류             | 기술/도구         |
| :--------------- | :---------------- |
| Library/Language | React, TypeScript |
| Styling          | TailwindCSS       |
| Build Tool       | Vite              |

### 4.2. Back-End

| 분류                | 기술/도구                 |
| :------------------ | :------------------------ |
| Language/Framework  | Java 17+, Spring Boot 3.x |
| Build/Configuration | Gradle, `application.yml` |
| Database/ORM        | MariaDB, Spring Data JPA  |
| Cache & Lock        | Redis (Redisson)          |
| Message Broker      | Apache Kafka              |

## 5. 아키텍처 다이어그램 및 데이터 흐름

1. **요청 진입:** React 클라이언트에서 API 요청 → API Gateway 통과 (Redis Rate Limiting).
2. **이벤트 발행:** Command Service가 1차 검증 후 Kafka에 주문 이벤트 발행.
3. **체결 처리:** Execution Engine이 Kafka에서 이벤트를 소비 → Redis 분산 락 획득 → MariaDB 체결 정보 Update.
4. **이벤트 전파:** 체결 완료 이벤트를 Kafka에 다시 발행.
5. **조회 및 알림:** Query/Alert Service가 이벤트를 수신 → Redis 최신 시세 갱신 및 RSI 연산 → 조건 충족 시 클라이언트에 WebSocket 알림 발송.

## 6. 테스트 및 성능 검증 계획

- nGrinder 또는 JMeter를 활용한 부하 테스트(최대 트래픽 3~5배 가정)를 정기적으로 수행합니다.
- DB Connection Pool, API Latency, TPS 지표를 모니터링하여 Redis 캐싱 및 Kafka 도입 전후의 성능 개선율을 수치화하여 기록합니다.
