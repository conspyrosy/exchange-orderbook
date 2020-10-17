package com.cryptofacilities.interview;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import static org.junit.Assert.assertEquals;

public class OrderBookTest {

  private OrderBook testOrderBook;
  private Order buyOrderOne, sellOrderOne, sellOrderTwo, sellOrderThree;

  @Before
  public void init() {
    testOrderBook = new OrderBook();
    buyOrderOne = new Order("1", "ETHBTC", Side.buy, 1, 2);
    sellOrderOne = new Order("2", "ETHBTC", Side.sell, 3, 5);
    sellOrderTwo = new Order("3", "ETHBTC", Side.sell, 3, 8);
    sellOrderThree = new Order("4", "ETHBTC", Side.sell, 3, 16);
  }

  @Test
  public void testEmptyOrderBook() {
    for (Side side : Side.values()) {
      assertEquals(-1, testOrderBook.getBestPrice(side));
      assertEquals(-1, testOrderBook.getOrderNumAtLevel(side, 10));
      assertEquals(-1, testOrderBook.getTotalQuantityAtLevel(side, 10));
      assertEquals(-1, testOrderBook.getTotalVolumeAtLevel(side, 10));
      assertEquals(new ArrayList<>(), testOrderBook.getOrdersAtLevel(side, 10));
    }
  }

  @Test
  public void testOrderBookWithOrders() {
    testOrderBook.addOrder(buyOrderOne);
    testOrderBook.addOrder(sellOrderOne);

    for(Order order : new Order[] { buyOrderOne, sellOrderOne }) {
      assertEquals(
              order.getPrice(),
              testOrderBook.getBestPrice(order.getSide())
      );

      assertEquals(
              1,
              testOrderBook.getOrderNumAtLevel(order.getSide(), order.getPrice())
      );

      assertEquals(
              order.getQuantity(),
              testOrderBook.getTotalQuantityAtLevel(order.getSide(), order.getPrice())
      );

      assertEquals(
              order.getPrice() * order.getQuantity(),
              testOrderBook.getTotalVolumeAtLevel(order.getSide(), order.getPrice())
      );

      assertEquals(
              0,
              testOrderBook.getOrdersAtLevel(order.getSide(), order.getPrice()).indexOf(order)
      );
    }
  }

  @Test
  public void testBuySellPriceOrderings() {
    for(Side side : Side.values()) {
      Order orderPriceFive = new Order("1", "ETHBTC", side, 5, 9);
      Order orderPriceFour = new Order("2", "ETHBTC", side, 4, 8);
      Order orderPriceThree = new Order("3", "ETHBTC", side, 3, 7);

      testOrderBook.addOrder(orderPriceFive);
      testOrderBook.addOrder(orderPriceFour);
      testOrderBook.addOrder(orderPriceThree);
    }
    assertEquals(testOrderBook.getBestPrice(Side.sell), 3);
    assertEquals(testOrderBook.getBestPrice(Side.buy), 5);
  }

  @Test
  public void testAddDeleteOrder() {
    for (Order order : new Order[]{buyOrderOne, sellOrderOne}) {
      testOrderBook.addOrder(order);

      assertEquals(
              0,
              testOrderBook.getOrdersAtLevel(order.getSide(), order.getPrice()).indexOf(order)
      );

      testOrderBook.deleteOrder(order);

      assertEquals(
              -1,
              testOrderBook.getOrdersAtLevel(order.getSide(), order.getPrice()).indexOf(order)
      );
    }
  }

  @Test
  public void testModifyOrderDecreasedQuantity() {
    testOrderBook.addOrder(sellOrderOne);
    testOrderBook.addOrder(sellOrderTwo);
    testOrderBook.addOrder(sellOrderThree);

    Order sellOrderTwoModified = new Order(sellOrderTwo);
    sellOrderTwoModified.setQuantity(6);

    assertEquals(1, testOrderBook.getOrdersAtLevel(Side.sell, 3).indexOf(sellOrderTwo));

    //this order should maintain its position once changed as the quantity has decreased
    testOrderBook.modifyOrder(sellOrderTwo, 6);

    assertEquals(1, testOrderBook.getOrdersAtLevel(Side.sell, 3).indexOf(sellOrderTwoModified));
  }

  @Test
  public void testModifyOrderIncreasedQuantity() {
    testOrderBook.addOrder(sellOrderOne);
    testOrderBook.addOrder(sellOrderTwo);
    testOrderBook.addOrder(sellOrderThree);

    Order sellOrderOneModified = new Order(sellOrderOne);
    sellOrderOneModified.setQuantity(12);

    assertEquals(
            0,
            testOrderBook.getOrdersAtLevel(Side.sell, 3).indexOf(sellOrderOne)
    );

    //this order should move to the end of the queue as it's quantity has increased
    testOrderBook.modifyOrder(sellOrderOne, 12);

    assertEquals(
            2,
            testOrderBook.getOrdersAtLevel(Side.sell, 3).indexOf(sellOrderOneModified)
    );
  }

  @Test
  public void testModifyOrderToZero() {
    testOrderBook.addOrder(sellOrderOne);

    assertEquals(
            Collections.singletonList(sellOrderOne),
            testOrderBook.getOrdersAtLevel(sellOrderOne.getSide(), sellOrderOne.getPrice())
    );

    assertEquals(
            sellOrderOne.getPrice(),
            testOrderBook.getBestPrice(sellOrderOne.getSide())
    );

    testOrderBook.modifyOrder(sellOrderOne, 0);

    assertEquals(
            Collections.emptyList(),
            testOrderBook.getOrdersAtLevel(sellOrderOne.getSide(), sellOrderOne.getPrice())
    );

    assertEquals(
            -1,
            testOrderBook.getBestPrice(sellOrderOne.getSide())
    );
  }

  @Test
  public void testDeleteNonExistentPriceLevel() {
    //this should never happen but just to be safe
    testOrderBook.deleteOrder(sellOrderOne);
  }
}
