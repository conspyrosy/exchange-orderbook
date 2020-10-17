package com.cryptofacilities.interview;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OrderBookManagerImpl.class, OrderBook.class})
public class OrderBookManagerImplTest {

  private OrderBookManagerImpl testOrderBookManager;
  private OrderBook mockedOrderBook;
  private Order orderOne;

  @Before
  public void init() throws Exception {
    testOrderBookManager = new OrderBookManagerImpl();
    orderOne = new Order("1", "ETHBTC", Side.buy, 1, 2);

    mockedOrderBook = PowerMockito.mock(OrderBook.class);
    PowerMockito.whenNew(OrderBook.class).withNoArguments().thenReturn(mockedOrderBook);
  }

  @Test
  public void testAddOrder() {
    testOrderBookManager.addOrder(orderOne);
    verify(mockedOrderBook, times(1)).addOrder(orderOne);
  }

  @Test
  public void testModifyOrder() {
    long newQuantity = 10;

    testOrderBookManager.addOrder(orderOne);
    testOrderBookManager.modifyOrder(orderOne.getOrderId(), newQuantity);
    verify(mockedOrderBook, times(1)).modifyOrder(orderOne, newQuantity);
  }

  @Test
  public void testModifyOrderDoesntExist() {
    //should catch the exception and continue
    testOrderBookManager.modifyOrder(orderOne.getOrderId(), 10);
  }

  @Test
  public void testDeleteOrder() {
    testOrderBookManager.addOrder(orderOne);
    testOrderBookManager.deleteOrder(orderOne.getOrderId());
    verify(mockedOrderBook, times(1)).deleteOrder(orderOne);
  }

  @Test
  public void testDeleteOrderDoesntExist() {
    //should catch the exception and continue
    testOrderBookManager.deleteOrder(orderOne.getOrderId());
  }

  @Test
  public void testGetBestPrice() {
    testOrderBookManager.addOrder(orderOne);
    testOrderBookManager.getBestPrice(orderOne.getInstrument(), orderOne.getSide());
    verify(mockedOrderBook, times(1)).getBestPrice(orderOne.getSide());
  }

  @Test
  public void testGetOrdersAtLevel() throws Exception {
    when(mockedOrderBook.getOrdersAtLevel(orderOne.getSide(), orderOne.getPrice()))
            .thenReturn(new ArrayList<>(Collections.singletonList(orderOne)));

    testOrderBookManager.addOrder(orderOne);
    assertEquals(orderOne, testOrderBookManager.getOrderFromOrderId(orderOne.getOrderId()));
    assertEquals(orderOne, testOrderBookManager.getOrdersAtLevel(
            orderOne.getInstrument(),
            orderOne.getSide(),
            orderOne.getPrice()
    ).get(0));
  }

  @Test
  public void testGetOrdersAtLevelWithEmptyOrders() {
    when(mockedOrderBook.getOrdersAtLevel(orderOne.getSide(), orderOne.getPrice()))
            .thenReturn(new ArrayList<>(Collections.emptyList()));

    assertEquals(Collections.emptyList(), testOrderBookManager.getOrdersAtLevel(
            orderOne.getInstrument(),
            orderOne.getSide(),
            orderOne.getPrice()
    ));
  }

  @Test
  public void testGetTotalVolumeAtLevel() {
    testOrderBookManager.addOrder(orderOne);

    when(mockedOrderBook.getTotalVolumeAtLevel(orderOne.getSide(), orderOne.getPrice()))
            .thenReturn((long) 10);

    assertEquals(10, testOrderBookManager.getTotalVolumeAtLevel(
            orderOne.getInstrument(),
            orderOne.getSide(),
            orderOne.getPrice()
    ));
  }

  @Test
  public void testGetTotalVolumeAtLevelWithEmptyOrders() {
    when(mockedOrderBook.getTotalVolumeAtLevel(orderOne.getSide(), orderOne.getPrice()))
            .thenReturn((long) -1);

    assertEquals(-1, testOrderBookManager.getTotalVolumeAtLevel(
            orderOne.getInstrument(),
            orderOne.getSide(),
            orderOne.getPrice()
    ));
  }

  @Test
  public void testGetOrderNumAtLevel() {
    testOrderBookManager.addOrder(orderOne);

    when(mockedOrderBook.getOrderNumAtLevel(orderOne.getSide(), orderOne.getPrice()))
            .thenReturn((long) 10);

    assertEquals(10, testOrderBookManager.getOrderNumAtLevel(
            orderOne.getInstrument(),
            orderOne.getSide(),
            orderOne.getPrice()
    ));
  }

  @Test
  public void testGetTotalQuantityAtLevel() {
    testOrderBookManager.addOrder(orderOne);

    when(mockedOrderBook.getTotalQuantityAtLevel(orderOne.getSide(), orderOne.getPrice()))
            .thenReturn((long) 10);

    assertEquals(10, testOrderBookManager.getTotalQuantityAtLevel(
            orderOne.getInstrument(),
            orderOne.getSide(),
            orderOne.getPrice()
    ));
  }

  @Test
  public void testGetTotalQuantityAtLevelWithEmptyOrders() {

    when(mockedOrderBook.getTotalQuantityAtLevel(orderOne.getSide(), orderOne.getPrice()))
            .thenReturn((long) -1);

    assertEquals(-1, testOrderBookManager.getTotalQuantityAtLevel(
            orderOne.getInstrument(),
            orderOne.getSide(),
            orderOne.getPrice()
    ));
  }

  @Test(expected = OrderNotFoundException.class)
  public void testGetOrderFromIdThrowsException() throws OrderNotFoundException {
    testOrderBookManager.getOrderFromOrderId("20");
  }
}
