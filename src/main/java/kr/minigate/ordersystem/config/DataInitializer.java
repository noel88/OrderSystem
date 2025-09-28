package kr.minigate.ordersystem.config;

import kr.minigate.ordersystem.domain.*;
import kr.minigate.ordersystem.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"!test", "default"}) // 테스트 환경에서는 실행하지 않음
public class DataInitializer {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @PostConstruct
    @Transactional
    public void initData() {
        if (memberRepository.count() > 0) {
            log.info("데이터가 이미 존재하므로 더미 데이터 생성을 건너뜁니다.");
            return;
        }

        log.info("더미 데이터 초기화를 시작합니다...");

        // 1. 회원 데이터 생성
        List<Member> members = createMembers();
        log.info("회원 {}명 생성 완료", members.size());

        // 2. 상품 데이터 생성
        List<Product> products = createProducts();
        log.info("상품 {}개 생성 완료", products.size());

        // 3. 주문 데이터 생성
        List<Order> orders = createOrders(members, products);
        log.info("주문 {}개 생성 완료", orders.size());

        // 4. 결제 데이터 생성
        List<Payment> payments = createPayments(orders);
        log.info("결제 {}개 생성 완료", payments.size());

        log.info("더미 데이터 초기화가 완료되었습니다!");
    }

    private List<Member> createMembers() {
        List<Member> members = Arrays.asList(
            Member.builder()
                .name("홍길동")
                .email("hong@example.com")
                .phone("010-1234-5678")
                .address("서울시 강남구 역삼동 123-45")
                .build(),
            Member.builder()
                .name("김철수")
                .email("kim@example.com")
                .phone("010-2345-6789")
                .address("서울시 서초구 반포동 678-90")
                .build(),
            Member.builder()
                .name("이영희")
                .email("lee@example.com")
                .phone("010-3456-7890")
                .address("경기도 성남시 분당구 정자동 111-22")
                .build(),
            Member.builder()
                .name("박민수")
                .email("park@example.com")
                .phone("010-4567-8901")
                .address("부산시 해운대구 우동 333-44")
                .build(),
            Member.builder()
                .name("최수진")
                .email("choi@example.com")
                .phone("010-5678-9012")
                .address("대구시 중구 동성로 555-66")
                .build()
        );

        return memberRepository.saveAll(members);
    }

    private List<Product> createProducts() {
        List<Product> products = Arrays.asList(
            Product.builder()
                .name("노트북")
                .description("고성능 게이밍 노트북 - Intel i7, RTX 4070, 16GB RAM")
                .price(new BigDecimal("1500000"))
                .stock(50)
                .build(),
            Product.builder()
                .name("무선 마우스")
                .description("로지텍 MX Master 3 - 무선 마우스")
                .price(new BigDecimal("120000"))
                .stock(200)
                .build(),
            Product.builder()
                .name("기계식 키보드")
                .description("체리 MX 청축 기계식 키보드")
                .price(new BigDecimal("150000"))
                .stock(100)
                .build(),
            Product.builder()
                .name("모니터")
                .description("27인치 4K UHD 모니터")
                .price(new BigDecimal("400000"))
                .stock(30)
                .build(),
            Product.builder()
                .name("웹캠")
                .description("로지텍 C920 HD 웹캠")
                .price(new BigDecimal("80000"))
                .stock(150)
                .build(),
            Product.builder()
                .name("헤드셋")
                .description("Sony WH-1000XM4 노이즈 캔슬링 헤드셋")
                .price(new BigDecimal("350000"))
                .stock(75)
                .build(),
            Product.builder()
                .name("스마트폰")
                .description("iPhone 15 Pro 256GB")
                .price(new BigDecimal("1300000"))
                .stock(25)
                .build(),
            Product.builder()
                .name("태블릿")
                .description("iPad Pro 12.9인치 512GB")
                .price(new BigDecimal("1600000"))
                .stock(20)
                .build()
        );

        return productRepository.saveAll(products);
    }

    private List<Order> createOrders(List<Member> members, List<Product> products) {
        // 첫 번째 주문: 홍길동이 노트북 + 마우스 주문
        OrderItem orderItem1_1 = OrderItem.builder()
            .product(products.get(0)) // 노트북
            .quantity(1)
            .price(products.get(0).getPrice())
            .amount(products.get(0).getPrice())
            .build();

        OrderItem orderItem1_2 = OrderItem.builder()
            .product(products.get(1)) // 무선 마우스
            .quantity(1)
            .price(products.get(1).getPrice())
            .amount(products.get(1).getPrice())
            .build();

        Order order1 = Order.createOrder(members.get(0), Arrays.asList(orderItem1_1, orderItem1_2));

        // 두 번째 주문: 김철수가 키보드 + 모니터 주문
        OrderItem orderItem2_1 = OrderItem.builder()
            .product(products.get(2)) // 기계식 키보드
            .quantity(1)
            .price(products.get(2).getPrice())
            .amount(products.get(2).getPrice())
            .build();

        OrderItem orderItem2_2 = OrderItem.builder()
            .product(products.get(3)) // 모니터
            .quantity(1)
            .price(products.get(3).getPrice())
            .amount(products.get(3).getPrice())
            .build();

        Order order2 = Order.createOrder(members.get(1), Arrays.asList(orderItem2_1, orderItem2_2));

        // 세 번째 주문: 이영희가 헤드셋 주문
        OrderItem orderItem3_1 = OrderItem.builder()
            .product(products.get(5)) // 헤드셋
            .quantity(1)
            .price(products.get(5).getPrice())
            .amount(products.get(5).getPrice())
            .build();

        Order order3 = Order.createOrder(members.get(2), Arrays.asList(orderItem3_1));

        // 네 번째 주문: 박민수가 스마트폰 주문
        OrderItem orderItem4_1 = OrderItem.builder()
            .product(products.get(6)) // 스마트폰
            .quantity(1)
            .price(products.get(6).getPrice())
            .amount(products.get(6).getPrice())
            .build();

        Order order4 = Order.createOrder(members.get(3), Arrays.asList(orderItem4_1));

        // 다섯 번째 주문: 최수진이 태블릿 + 웹캠 주문
        OrderItem orderItem5_1 = OrderItem.builder()
            .product(products.get(7)) // 태블릿
            .quantity(1)
            .price(products.get(7).getPrice())
            .amount(products.get(7).getPrice())
            .build();

        OrderItem orderItem5_2 = OrderItem.builder()
            .product(products.get(4)) // 웹캠
            .quantity(2)
            .price(products.get(4).getPrice())
            .amount(products.get(4).getPrice().multiply(new BigDecimal("2")))
            .build();

        Order order5 = Order.createOrder(members.get(4), Arrays.asList(orderItem5_1, orderItem5_2));

        List<Order> orders = Arrays.asList(order1, order2, order3, order4, order5);
        return orderRepository.saveAll(orders);
    }

    private List<Payment> createPayments(List<Order> orders) {
        // 첫 번째 주문에 대한 카드 결제
        Payment payment1 = Payment.builder()
            .order(orders.get(0))
            .amount(orders.get(0).getTotalAmount())
            .paymentMethod(PaymentMethod.CARD)
            .transactionId(Payment.generateTransactionId())
            .build();

        // 두 번째 주문에 대한 계좌이체 결제
        Payment payment2 = Payment.builder()
            .order(orders.get(1))
            .amount(orders.get(1).getTotalAmount())
            .paymentMethod(PaymentMethod.BANK_TRANSFER)
            .transactionId(Payment.generateTransactionId())
            .build();

        // 세 번째 주문에 대한 카드 결제
        Payment payment3 = Payment.builder()
            .order(orders.get(2))
            .amount(orders.get(2).getTotalAmount())
            .paymentMethod(PaymentMethod.CARD)
            .transactionId(Payment.generateTransactionId())
            .build();

        // 네 번째 주문에 대한 계좌이체 결제
        Payment payment4 = Payment.builder()
            .order(orders.get(3))
            .amount(orders.get(3).getTotalAmount())
            .paymentMethod(PaymentMethod.BANK_TRANSFER)
            .transactionId(Payment.generateTransactionId())
            .build();

        // 다섯 번째 주문에 대한 카드 결제
        Payment payment5 = Payment.builder()
            .order(orders.get(4))
            .amount(orders.get(4).getTotalAmount())
            .paymentMethod(PaymentMethod.CARD)
            .transactionId(Payment.generateTransactionId())
            .build();

        List<Payment> payments = Arrays.asList(payment1, payment2, payment3, payment4, payment5);
        return paymentRepository.saveAll(payments);
    }
}