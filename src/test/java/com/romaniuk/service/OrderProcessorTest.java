package com.romaniuk.service;

import com.romaniuk.domain.Order;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessorTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderProcessor orderProcessor;


    //1) Створити тести з використанням @ExtendWith(MockitoExtension.class), @Mock, @InjectMocks, when(...).thenReturn(...).
    // Не менше 3 різних бізнес сценаріїв.

    @Test
    void shouldProcessOrderSuccessfully() {

        Order order = new Order("1", List.of("Laptop", "Mouse"), 1500.0, "vika@gmail.com");

        when(inventoryService.checkStock("Laptop")).thenReturn(true);
        when(inventoryService.checkStock("Mouse")).thenReturn(true);
        when(paymentGateway.process(1500.0)).thenReturn(true);

        boolean result = orderProcessor.processOrder(order);

        assertThat(result).isTrue();

        verify(inventoryService).reserveItem("Laptop");
        verify(inventoryService).reserveItem("Mouse");
        verify(notificationService).notifySuccess("vika@gmail.com");
    }

    @Test
    void shouldRejectOrderWhenItemOutOfStock() {

        Order order = new Order("2", List.of("Laptop", "Phone"), 1000.0, "vika@gmail.com");

        when(inventoryService.checkStock("Laptop")).thenReturn(true);
        when(inventoryService.checkStock("Phone")).thenReturn(false);

        boolean result = orderProcessor.processOrder(order);

        assertThat(result).isFalse();

        verify(paymentGateway, never()).process(anyDouble());
        verify(notificationService).notifyFailure("vika@gmail.com", "Out of stock: Phone");
    }

    @Test
    void shouldRejectOrderWithInvalidAmount() {

        Order order = new Order("3", List.of("Book"), 0.0, "vika@gmail.com");

        when(inventoryService.checkStock("Book")).thenReturn(true);

        boolean result = orderProcessor.processOrder(order);

        assertThat(result).isFalse();

        verify(paymentGateway, never()).process(anyDouble());
        verify(notificationService).notifyFailure("vika@gmail.com", "Invalid amount");
    }


    // 2) Створити тести з перевірками void-методів — verify, times, never
    // (щонайменше по 1 тесту на кожну з цих умов)

    @Test
    void verify_ShouldSendSuccessNotification() {

        Order order = new Order("4", List.of("Keyboard"), 500.0, "vika@gmail.com");

        when(inventoryService.checkStock("Keyboard")).thenReturn(true);
        when(paymentGateway.process(500.0)).thenReturn(true);

        orderProcessor.processOrder(order);

        verify(notificationService).notifySuccess("vika@gmail.com");
    }

    @Test
    void times_ShouldSendFailureNotificationExactlyOnce() {
        Order order = new Order("5", List.of("Table"), 200.0, "vika@gmail.com");

        when(inventoryService.checkStock("Table")).thenReturn(true);
        when(paymentGateway.process(200.0)).thenReturn(false);

        boolean result = orderProcessor.processOrder(order);

        assertThat(result).isFalse();

        verify(notificationService, times(1)).notifyFailure("vika@gmail.com", "Payment failed");
    }

    @Test
    void never_ShouldNotReserveItemWhenPaymentFails() {

        Order order = new Order("6", List.of("Table"), 200.0, "vika@gmail.com");

        when(inventoryService.checkStock("Table")).thenReturn(true);
        when(paymentGateway.process(200.0)).thenReturn(false);

        orderProcessor.processOrder(order);

        verify(inventoryService, never()).reserveItem(anyString());
    }


    // 3) Створити тести з AssertJ SoftAssertions (щонайменше 1).
    @Test
    void softAssertions_ForSuccessfulOrderScenario() {

        Order order = new Order("7", List.of("Laptop", "Mouse"), 1500.0, "vika@gmail.com");

        when(inventoryService.checkStock("Laptop")).thenReturn(true);
        when(inventoryService.checkStock("Mouse")).thenReturn(true);
        when(paymentGateway.process(1500.0)).thenReturn(true);

        boolean result = orderProcessor.processOrder(order);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(result).isTrue();
        softly.assertThat(order.getItems()).hasSize(2);
        softly.assertThat(order.getAmount()).isPositive();
        softly.assertThat(order.getCustomerEmail()).contains("@");

        softly.assertAll();
    }

    //4)  Створити тест з AssertJ перевірки списків(щонайменше 3 різні типи перевірок)
    @Test
    void listAssertions_ForOrderItems() {

        Order order = new Order("8", List.of("Laptop", "Mouse", "Keyboard"), 1200.0, "vika@gmail.com");


        assertThat(order.getItems()).hasSize(3);
        assertThat(order.getItems()).contains("Laptop", "Mouse");
        assertThat(order.getItems()).doesNotContain("Monitor");
        assertThat(order.getItems()).containsExactlyInAnyOrderElementsOf(List.of("Mouse", "Laptop", "Keyboard"));

        // інший варіант запису перевірок в одному ланцюжку
        assertThat(order.getItems())
                .hasSize(3)
                .contains("Laptop", "Mouse")
                .doesNotContain("Monitor")
                .containsExactlyInAnyOrderElementsOf(List.of("Mouse", "Laptop", "Keyboard"));
    }

    /**
     * 5) Використати PIT бібліотеку мутаційного тестування.
     * Створити тест який впаде від мутації і окремо його виправлену версію.
     * (Логіка має містити кілька гілок (if/else), інакше не буде що мутувати)
     */

    // тест показує, що мутант виживає, оскільки було взяти значення 100.0 для amount
    // для оригінального коду 100.0 <= 0 умова повертає false і тест проходить
    // для мутанта 100.0 < 0 умова повертає false і тест проходить
    // тест не здатний помітити заміну знаку
    @Test
    void test_ThatSurvivesMutation() {

        Order order = new Order("9", List.of("Pen"), 100.0, "vika@gmail.com");

        when(inventoryService.checkStock("Pen")).thenReturn(true);
        when(paymentGateway.process(100.0)).thenReturn(true);

        boolean result = orderProcessor.processOrder(order);

        assertThat(result).isTrue();
    }


    // тест вбиває мутанта, оскільки було взято граничне значення 0.0 для amount.
    // для оригінального коду умова 0.0 <= 0 повертає true, код виконує бізнес-перевірку, і тест проходить.
    // для мутанта умова 0.0 < 0 повертає false, зламаний код проскакує перевірку суми й намагається викликати банк.
    // тест миттєво помічає цю зміну та падає
    @Test
    void test_ThatKillsMutation() {

        Order order = new Order("10", List.of("Pen"), 0.0, "vika@gmail.com");

        when(inventoryService.checkStock("Pen")).thenReturn(true);

        boolean result = orderProcessor.processOrder(order);

        assertThat(result).isFalse();

        verify(notificationService).notifyFailure("vika@gmail.com", "Invalid amount");
        verify(paymentGateway, never()).process(anyDouble());
    }
}
