package com.cryptofacilities.interview;

import java.util.*;

/**
 * Created by CF-8 on 6/27/2017.
 */
public class OrderBookManagerImpl implements OrderBookManager {

    /**
     * A mapping of instrument name to {@link OrderBook OrderBook}
     */
    private Map<String, OrderBook> orderBooks = new HashMap<>();

    /**
     * A mapping of Order ID to {@link Order Order}
     */
    private Map<String, Order> orderDirectory = new HashMap<>();

    /**
     * Find appropriate OrderBook and add the Order. If no OrderBook exists, one will be created
     */
    public void addOrder(Order order) {
        if(orderBooks.get(order.getInstrument()) == null) {
            orderBooks.put(order.getInstrument(), new OrderBook());
        }
        orderBooks.get(order.getInstrument()).addOrder(order);
        orderDirectory.put(order.getOrderId(), order);
    }

    /**
     * Find appropriate OrderBook and modify the Order with new quantity.
     *
     * @param orderId orderId of the Order to be modified
     * @param newQuantity new quantity of the Order
     */
    public void modifyOrder(String orderId, long newQuantity) {
        try {
            Order order = getOrderFromOrderId(orderId);
            orderBooks.get(order.getInstrument()).modifyOrder(order, newQuantity);
        } catch(OrderNotFoundException error){
            //some logging should occur here
        }
    }

    /**
     * Find appropriate OrderBook and delete the order.
     *
     * @param orderId orderId of the Order to be deleted
     */
    public void deleteOrder(String orderId) {
        try {
            Order order = getOrderFromOrderId(orderId);
            orderBooks.get(order.getInstrument()).deleteOrder(order);
        } catch(OrderNotFoundException error) {
            //some logging should occur here
        }
    }

    /**
     * Find appropriate OrderBook and get the best price on the side specified.
     *
     * @param instrument instrument to check
     * @param side side of OrderBook to check
     */
    public long getBestPrice(String instrument, Side side) {
        return orderBooks.get(instrument).getBestPrice(side);
    }

    /**
     * Find appropriate OrderBook and get the number of orders on the side specified.
     *
     * @param instrument instrument to check
     * @param side side of OrderBook to check
     * @param price price level to check
     */
    public long getOrderNumAtLevel(String instrument, Side side, long price) {
        return orderBooks.get(instrument).getOrderNumAtLevel(side, price);
    }

    /**
     * Find appropriate OrderBook and get the number of orders on the side specified.
     *
     * @param instrument instrument to check
     * @param side side of OrderBook to check
     * @param price price level to check
     */
    public long getTotalQuantityAtLevel(String instrument, Side side, long price) {
        if(orderBooks.get(instrument) != null) {
            return orderBooks.get(instrument).getTotalQuantityAtLevel(side, price);
        }
        return -1;
    }

    /**
     * Find appropriate OrderBook and get the number of orders on the side specified at a price level.
     *
     * @param instrument instrument to check
     * @param side side of OrderBook to check
     * @param price price level to check
     */
    public long getTotalVolumeAtLevel(String instrument, Side side, long price) {
        if(orderBooks.get(instrument) != null) {
            return orderBooks.get(instrument).getTotalVolumeAtLevel(side, price);
        }
        return -1;
    }

    /**
     * Find appropriate OrderBook and get the list of orders on the side specified at a price level.
     *
     * @param instrument instrument to check
     * @param side side of OrderBook to check
     * @param price price level to check
     */
    public List<Order> getOrdersAtLevel(String instrument, Side side, long price) {
        if(orderBooks.get(instrument) != null) {
            return orderBooks.get(instrument).getOrdersAtLevel(side, price);
        }
        return Collections.emptyList();
    }

    /**
     * Find {@link Order Order} object based on it's orderId
     *
     * @param orderId orderId to get Order from
     * @return {@link Order Order} with the orderId specified
     */
    Order getOrderFromOrderId(String orderId) throws OrderNotFoundException {
        if(orderDirectory.get(orderId) != null)
            return orderDirectory.get(orderId);

        throw new OrderNotFoundException("Order ID: " + orderId + " was not found in the directory");
    }
}
