package com.cryptofacilities.interview;

import java.util.*;

/**
 * @author  Spyros
 * @version 1.0
 * @since   2019-30-03
 *
 * This class represents an Orderbook for a SINGLE instrument.
 */
class OrderBook {
  /**
   * A Map of Map of Maps holding orders at price levels of each side of the orderbook.
   * Map 1 Key = {@link Side Side} of Orderbook
   * Map 2 Key = Price Level on Side
   * Map 3 Key = Order ID, Value = {@link Order Order} with that Order ID
   */
  private Map<Side, TreeMap<Long, LinkedHashMap<String, Order>>> orders = new HashMap<>();

  /**
   * Constructor will initialise both sides of the OrderBook.
   *
   * Orders on the buy side are in <b>descending</b> order on price
   * Orders on the sell side are in <b>ascending</b> order on price
   */
  OrderBook() {
    orders.put(Side.buy, new TreeMap<>(Collections.reverseOrder()));
    orders.put(Side.sell, new TreeMap<>());
  }

  /**
   * Add the Order to the correct side of the book, at the end of the list of orders for that price level.
   * @param order Order to be added
   */
  void addOrder(Order order) {
    TreeMap<Long, LinkedHashMap<String, Order>> orderSide = orders.get(order.getSide());
    orderSide.computeIfAbsent(order.getPrice(), k -> new LinkedHashMap<>());
    orderSide.get(order.getPrice()).put(order.getOrderId(), order);
  }

  /**
   * Modify the {@link Order order} with the new quantity.
   *
   * If an order quantity is increased, the order is removed and re-added from the map to push it to the end.
   * If an order quantity is decrease, the order is updated but maintains its position.
   * If an order quantity is 0 or less, that order is deleted from the OrderBook.
   *
   * @param order The order to modify
   * @param newQuantity The new order quantity
   */
  void modifyOrder(Order order, long newQuantity) {
    if(newQuantity <= 0) {
      deleteOrder(order);
      return;
    }

    HashMap<String, Order> ordersAtPriceLevel = getOrdersAtPriceLevel(order);

    if(ordersAtPriceLevel != null && ordersAtPriceLevel.get(order.getOrderId()) != null){
      Order newOrder = new Order(order);
      newOrder.setQuantity(newQuantity);

      if(newQuantity > order.getQuantity()) {
        ordersAtPriceLevel.remove(order.getOrderId());
      }
      ordersAtPriceLevel.put(order.getOrderId(), newOrder);
    }
  }

  /**
   * Delete the {@link Order order} from the orderbook.
   *
   * @param order The order to delete
   */
  void deleteOrder(Order order) {
    if(getOrdersAtPriceLevel(order) != null) {
      getOrdersAtPriceLevel(order).remove(order.getOrderId());
      if (getOrdersAtPriceLevel(order).isEmpty())
        removePriceLevel(order.getSide(), order.getPrice());
    }
  }

  /**
   * Get the best price on the {@link Side side} of the orderbook. best means lowest for ask and highest for bid.
   *
   * @param side the side of the orderbook to get the best price on
   * @return the best price on the side specified. returns -1 if no orders at price level.
   */
  long getBestPrice(Side side) {
    if(!orders.get(side).isEmpty())
      return orders.get(side).firstKey();
    return -1;
  }

  /**
   * Get the number of orders at the price level specified on a side of the orderbook.
   *
   * @param side the side of the orderbook
   * @param price the price level on the side
   * @return the number of orders at the price level on the side specified. returns -1 if no orders at price level.
   */
  long getOrderNumAtLevel(Side side, long price) {
    if(getOrdersAtPriceLevel(side, price) != null)
      return getOrdersAtPriceLevel(side, price).size();
    return -1;
  }

  /**
   * Get the quantity available at a price level specified on a side of the orderbook.
   *
   * @param side the side of the orderbook to get the quantity from
   * @param price the price level on the side to get the quantity from
   * @return the quantity available at the price level on the side specified. returns -1 if no orders at price level.
   */
  long getTotalQuantityAtLevel(Side side, long price) {
    if(getOrdersAtPriceLevel(side, price) != null)
      return getOrdersAtPriceLevel(side, price)
              .values()
              .stream()
              .mapToLong(Order::getQuantity)
              .sum();
    return -1;
  }

  /**
   * Get the volume available at a price level specified on a side of the orderbook.
   *
   * @param side the side of the orderbook to get the volume from
   * @param price the price level on the side to get the volume from
   * @return the volume available at the price level on the side specified. returns -1 if no orders at price level.
   */
  long getTotalVolumeAtLevel(Side side, long price) {
    if(this.getTotalQuantityAtLevel(side, price) != -1)
      return getTotalQuantityAtLevel(side, price) * price;
    return -1;
  }

  /**
   * Get the list of orders at a price level specified on a side of the orderbook.
   *
   * @param side the side of the orderbook to get the orders from
   * @param price the price level on the side to get the orders from
   * @return the list of orders at the price level on the side specified
   */
  List<Order> getOrdersAtLevel(Side side, long price) {
    if(getOrdersAtPriceLevel(side, price) != null)
      return new ArrayList<>(getOrdersAtPriceLevel(side, price).values());
    return Collections.emptyList();
  }

  /**
   * Get orders at a specific price level based on another order's side and price.
   *
   * @param order the order containing the side and price level wanting to be fetched
   * @return A HashMap of orders at the side and price level of the order specified. Key = OrderID, Value = Order
   */
  private HashMap<String, Order> getOrdersAtPriceLevel(Order order) {
    return getOrdersAtPriceLevel(order.getSide(), order.getPrice());
  }

  /**
   * Get orders on a side at a specific price level.
   *
   * @param side the side at which the orders are to be fetched from
   * @param price the price at which the orders are to be fetched from
   * @return the orders at the side and price level of the order specified. Key = OrderID, Value = Order
   */
  private HashMap<String, Order> getOrdersAtPriceLevel(Side side, long price) {
    return orders.get(side).get(price);
  }

  /**
   * Removes a complete price level from a side of the order book.
   *
   * @param side the side at which the price level is to be removed from
   * @param price the price level on the side to remove
   */
  private void removePriceLevel(Side side, long price) {
    orders.get(side).remove(price);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OrderBook)) return false;
    OrderBook orderBook = (OrderBook) o;
    return Objects.equals(orders, orderBook.orders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orders);
  }
}
