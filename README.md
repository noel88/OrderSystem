# OrderSystem

> Spring Boot 기반의 주문 관리 시스템

## 프로젝트 개요

이 프로젝트는 Spring Boot를 기반으로 한 REST API 주문 관리 시스템입니다.
회원 관리, 상품 관리, 주문 처리, 결제 처리의 핵심 기능을 제공합니다.

## 기술 스택

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Database**: H2 (In-Memory)
- **ORM**: Spring Data JPA
- **Build Tool**: Gradle 8.14.3
- **Documentation**: Spring REST Docs
- **Testing**: JUnit 5, Spring Test

## 주요 기능

### 회원 관리
- 회원 가입 및 정보 조회
- 이메일 중복 검증

### 상품 관리
- 상품 등록 및 조회
- 재고 관리

### 주문 관리
- 다중 상품 주문 생성
- 주문 내역 조회
- 회원별 주문 목록

### 결제 관리
- 다양한 결제 수단 지원
- 주문별 결제 처리 및 조회

## 아키텍처

```
src/
├── main/java/kr/minigate/ordersystem/
│   ├── api/
│   │   ├── controller/          # REST API 컨트롤러
│   │   ├── request/             # 요청 DTO
│   │   └── response/            # 응답 DTO
│   ├── application/
│   │   ├── dto/                 # 애플리케이션 DTO
│   │   └── service/             # 비즈니스 로직
│   ├── domain/                  # 도메인 엔티티
│   │   └── repository/          # 레포지토리 인터페이스
│   └── config/                  # 설정 클래스
└── test/                        # 테스트 코드
```

## API 엔드포인트

### 회원 관리 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/api/members` | 회원 가입 |
| `GET` | `/api/members/{id}` | 회원 정보 조회 |
| `GET` | `/api/members` | 전체 회원 목록 |
| `PUT` | `/api/members/{id}` | 회원 정보 수정 |
| `DELETE` | `/api/members/{id}` | 회원 탈퇴 |

### 상품 관리 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/api/products` | 상품 등록 |
| `GET` | `/api/products` | 상품 목록 조회 |
| `GET` | `/api/products/{id}` | 상품 상세 조회 |

### 주문 관리 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/api/orders` | 주문 생성 |
| `GET` | `/api/orders` | 전체 주문 목록 |
| `GET` | `/api/orders?memberId={id}` | 회원별 주문 목록 |
| `GET` | `/api/orders/{id}` | 주문 상세 조회 |

### 결제 관리 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/api/payments` | 결제 처리 |
| `GET` | `/api/payments/{id}` | 결제 상세 조회 |
| `GET` | `/api/payments/order/{orderId}` | 주문별 결제 조회 |
| `GET` | `/api/payments/member/{memberId}` | 회원별 결제 내역 |
