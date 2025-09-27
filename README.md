### 도메인 모델링
#### 다음의 기본 엔티티를 설계하고 연관관계를 정의하세요.
- 회원(Member): 회원 가입, 조회 기능
- 주문(Order): 주문 생성, 조회 기능
- 결제(Payment): 주문에 대한 결제 처리 기능
### 구현 조건
- Spring Boot를 기반으로 프로젝트를 생성하세요.
- 데이터 접근 계층은 Spring Data JPA를 활용하세요.
- 비즈니스 로직은 서비스 레이어에 구현하세요.
- 각 기능은 REST API 형태로 노출하세요.
### 테스트 조건 (옵션)
- JUnit과 Spring Test를 활용하여 단위 테스트 및 통합 테스트를 작성하세요.
- TDD 방식을 지향하며, 가능한 기능 단위로 테스트 코드를 선행 작성하세요.
### 기타 고려사항
- 가급적 Clean Code 원칙을 준수하여 가독성과 유지보수성을 고려해주세요.
- 데이터베이스는 H2(In-Memory DB)를 사용해도 무방합니다.
- 빌드 도구는 Gradle 로 작업해주세요.

## 구현 완료 현황

### REST API 엔드포인트 ✅

#### API Endpoint 목록

| 기능 | Method | Endpoint | 설명 |
|------|--------|----------|------|
| **회원 관리** |
| 회원 가입 | POST | `/api/members` | 새 회원 등록 |
| 회원 조회 | GET | `/api/members/{id}` | 회원 정보 조회 |
| **상품 관리** |
| 상품 등록 | POST | `/api/products` | 새 상품 등록 |
| 상품 목록 | GET | `/api/products` | 전체 상품 목록 조회 |
| 상품 조회 | GET | `/api/products/{id}` | 상품 상세 정보 조회 |
| **주문 관리** |
| 주문 생성 | POST | `/api/orders` | 새 주문 생성 |
| 주문 목록 | GET | `/api/orders?memberId={memberId}` | 회원별 주문 목록 조회 |
| 주문 조회 | GET | `/api/orders/{id}` | 주문 상세 정보 조회 |
| **결제 관리** |
| 결제 처리 | POST | `/api/payments` | 주문에 대한 결제 처리 |
| 주문별 결제 조회 | GET | `/api/payments/order/{orderId}` | 특정 주문의 결제 정보 조회 |
| 결제 조회 | GET | `/api/payments/{id}` | 결제 상세 정보 조회 |

#### 1. 회원 관리 API (`/api/members`)

**회원 가입**
- `POST /api/members`
- Request Body:
```json
{
  "name": "홍길동",
  "email": "hong@test.com",
  "phone": "010-1234-5678",
  "address": "서울시 강남구"
}
```
- Response: `201 Created`

**회원 정보 조회**
- `GET /api/members/{id}`
- Response: `200 OK`

#### 2. 상품 관리 API (`/api/products`)

**상품 등록**
- `POST /api/products`
- Request Body:
```json
{
  "name": "아이폰 15",
  "description": "애플 스마트폰",
  "price": 1200000,
  "stock": 10
}
```
- Response: `201 Created`

**상품 목록 조회**
- `GET /api/products`
- Response: `200 OK`

**상품 상세 조회**
- `GET /api/products/{id}`
- Response: `200 OK`

#### 3. 주문 관리 API (`/api/orders`)

**주문 생성**
- `POST /api/orders`
- Request Body:
```json
{
  "memberId": 1,
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```
- Response: `201 Created`

**내 주문 목록 조회**
- `GET /api/orders?memberId={memberId}`
- Response: `200 OK`

**주문 상세 조회**
- `GET /api/orders/{id}`
- Response: `200 OK`

#### 4. 결제 관리 API (`/api/payments`)

**결제 처리**
- `POST /api/payments`
- Request Body:
```json
{
  "orderId": 1,
  "paymentMethod": "CARD"
}
```
- Response: `201 Created`
- PaymentMethod: `CARD`, `BANK_TRANSFER`, `CASH`, `MOBILE_PAY`

**주문별 결제 조회**
- `GET /api/payments/order/{orderId}`
- Response: `200 OK`

**결제 상세 조회**
- `GET /api/payments/{id}`
- Response: `200 OK`

#### API 특징:
- **RESTful 설계**: HTTP Method와 상태코드 적절 사용
- **Validation**: Request 데이터 검증 (@Valid, @NotNull, @Positive)
- **JSON 통신**: 모든 요청/응답 JSON 형태
- **Error Handling**: 400 Bad Request로 검증 실패 처리
- **Status Code**: 201 Created, 200 OK, 400 Bad Request