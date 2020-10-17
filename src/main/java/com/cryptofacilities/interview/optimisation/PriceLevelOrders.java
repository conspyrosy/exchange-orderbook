package com.cryptofacilities.interview.optimisation;

import com.cryptofacilities.interview.Order;
import com.cryptofacilities.interview.Side;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class is not used in the implementation but demonstrates how we could improve the time complexity
 * of getQuantityAtPriceLevel and getTotalVolumeAtPriceLevel.
 * The functions in orderbook would call getTotalQuantity and it will be fetched in a single operation.
 */
public class PriceLevelOrders {
  private LinkedHashMap<String, Order> orders = new LinkedHashMap<>();
  private long totalQuantity = 0; //we keep track of this and update when new orders are added or existing ones change

  List<Order> getOrdersAtLevel(Side side, long price) {
      return new ArrayList<>(orders.values());
  }

  /**
   * Get total quantity at this price level
   * @return The quantity available at this price level tracked in this object.
   */
  public long getTotalQuantity() {
    return totalQuantity;
  }

  public void addOrder(Order order) {
    orders.put(order.getOrderId(), order);
    totalQuantity += order.getQuantity(); //recalculate the qty
  }

  public void deleteOrder(Order order) {
    //do some delete op here
    totalQuantity -= order.getQuantity(); //recalculate the qty
  }

  public void modifyOrder(Order order) {
    //change the order here and update the qty
  }

  public int getOrderCount() {
    return orders.size();
  }
}
