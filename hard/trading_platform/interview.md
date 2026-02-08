# Trading Platform (Stock Exchange) - LLD Interview Guide

## Problem Statement

Design a stock trading platform that allows users to place orders (market, limit, stop), matches buy/sell orders, maintains order books, tracks portfolios, and provides real-time price updates.

## Core Requirements

### Functional
- Place orders (market, limit, stop-loss)
- Order matching engine
- Order book management (bid/ask)
- Portfolio tracking
- Transaction history
- Real-time price updates
- Cancel/modify pending orders

### Non-Functional
- **Low Latency**: < 1ms order matching (critical!)
- **High Throughput**: 100K+ orders/sec
- **Consistency**: Strong consistency for order matching
- **Availability**: 99.999% uptime during trading hours
- **Fairness**: FIFO order matching

## Key Components

```java
enum OrderType {
    MARKET,      // Execute immediately at best price
    LIMIT,       // Execute at specified price or better
    STOP_LOSS    // Trigger at stop price, then market order
}

enum OrderSide {
    BUY, SELL
}

class Order {
    private String orderId;
    private String userId;
    private String symbol;        // e.g., "AAPL"
    private OrderType type;
    private OrderSide side;
    private int quantity;
    private BigDecimal limitPrice;  // For LIMIT orders
    private BigDecimal stopPrice;   // For STOP orders
    private LocalDateTime timestamp;
    private OrderStatus status;
}

class OrderBook {
    private String symbol;
    
    // Buy orders sorted by price DESC (highest first)
    private TreeMap<BigDecimal, Queue<Order>> bids;
    
    // Sell orders sorted by price ASC (lowest first)
    private TreeMap<BigDecimal, Queue<Order>> asks;
    
    public synchronized void addOrder(Order order) {
        if (order.getSide() == OrderSide.BUY) {
            bids.computeIfAbsent(order.getLimitPrice(), k -> new LinkedList<>())
                .offer(order);
        } else {
            asks.computeIfAbsent(order.getLimitPrice(), k -> new LinkedList<>())
                .offer(order);
        }
        
        // Try to match
        matchOrders();
    }
    
    private void matchOrders() {
        while (!bids.isEmpty() && !asks.isEmpty()) {
            BigDecimal bestBid = bids.lastKey();      // Highest buy price
            BigDecimal bestAsk = asks.firstKey();     // Lowest sell price
            
            // Can match if bid >= ask
            if (bestBid.compareTo(bestAsk) >= 0) {
                Queue<Order> buyOrders = bids.get(bestBid);
                Queue<Order> sellOrders = asks.get(bestAsk);
                
                Order buyOrder = buyOrders.peek();
                Order sellOrder = sellOrders.peek();
                
                // Match at ask price (price-time priority)
                int matchedQty = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
                BigDecimal matchedPrice = bestAsk;
                
                // Execute trade
                executeTrade(buyOrder, sellOrder, matchedQty, matchedPrice);
                
                // Update quantities
                buyOrder.setQuantity(buyOrder.getQuantity() - matchedQty);
                sellOrder.setQuantity(sellOrder.getQuantity() - matchedQty);
                
                // Remove filled orders
                if (buyOrder.getQuantity() == 0) buyOrders.poll();
                if (sellOrder.getQuantity() == 0) sellOrders.poll();
                
                // Remove empty price levels
                if (buyOrders.isEmpty()) bids.remove(bestBid);
                if (sellOrders.isEmpty()) asks.remove(bestAsk);
                
            } else {
                break; // No more matches possible
            }
        }
    }
    
    private void executeTrade(Order buy, Order sell, int qty, BigDecimal price) {
        Trade trade = new Trade(buy.getOrderId(), sell.getOrderId(), qty, price);
        tradeRepository.save(trade);
        
        // Update portfolios
        portfolioService.addShares(buy.getUserId(), symbol, qty);
        portfolioService.removeShares(sell.getUserId(), symbol, qty);
        
        // Update balances
        BigDecimal amount = price.multiply(BigDecimal.valueOf(qty));
        accountService.debit(buy.getUserId(), amount);
        accountService.credit(sell.getUserId(), amount);
        
        // Broadcast trade
        broadcastTrade(trade);
    }
    
    public BigDecimal getLastTradedPrice() {
        Trade lastTrade = tradeRepository.getLastTrade(symbol);
        return lastTrade.getPrice();
    }
}

class MatchingEngine {
    private Map<String, OrderBook> orderBooks; // symbol -> order book
    
    public void placeOrder(Order order) {
        OrderBook book = orderBooks.get(order.getSymbol());
        
        if (order.getType() == OrderType.MARKET) {
            // Market order: execute at best available price
            order.setLimitPrice(order.getSide() == OrderSide.BUY ? 
                BigDecimal.valueOf(Double.MAX_VALUE) : BigDecimal.ZERO);
        }
        
        book.addOrder(order);
    }
    
    public void cancelOrder(String orderId) {
        // Find and remove from order book
        Order order = orderRepository.get(orderId);
        OrderBook book = orderBooks.get(order.getSymbol());
        book.removeOrder(order);
        order.setStatus(OrderStatus.CANCELLED);
    }
}

class Portfolio {
    private String userId;
    private Map<String, Integer> holdings;  // symbol -> quantity
    private BigDecimal cashBalance;
    
    public BigDecimal getMarketValue() {
        BigDecimal total = cashBalance;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            BigDecimal price = marketDataService.getPrice(entry.getKey());
            total = total.add(price.multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return total;
    }
}
```

## Common Interview Questions

**Q1: How do you ensure low latency (<1ms)?**

1. **In-Memory Data Structures**: Keep order books in RAM
2. **Lock-Free Algorithms**: Use CAS (Compare-And-Swap) instead of locks
3. **CPU Pinning**: Pin matching engine to specific CPU cores
4. **Kernel Bypass**: Use DPDK for network I/O
5. **Co-location**: Users' servers physically near exchange

**Q2: How do you handle market vs limit orders?**

- **Market Order**: Set limit price to infinity (buy) or zero (sell)
  - Guaranteed execution but not price
- **Limit Order**: Only execute at specified price or better
  - Guaranteed price but not execution

**Q3: How do you prevent race conditions in order matching?**

```java
class OrderBook {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public void addOrder(Order order) {
        lock.writeLock().lock();
        try {
            // Add to order book
            // Match orders
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public List<Order> getBids() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(bids.values());
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

**Q4: How do you broadcast real-time price updates?**

```java
class MarketDataService {
    private final Map<String, List<WebSocketSession>> subscribers;
    
    public void broadcastTrade(Trade trade) {
        String symbol = trade.getSymbol();
        PriceUpdate update = new PriceUpdate(
            symbol, 
            trade.getPrice(), 
            trade.getQuantity(),
            trade.getTimestamp()
        );
        
        // WebSocket push to all subscribers
        subscribers.get(symbol).forEach(session -> {
            session.sendMessage(toJson(update));
        });
    }
}
```

**Q5: How do you handle circuit breakers (trading halts)?**

```java
class CircuitBreaker {
    private static final BigDecimal HALT_THRESHOLD = new BigDecimal("0.10"); // 10%
    
    public boolean shouldHalt(String symbol) {
        BigDecimal currentPrice = getCurrentPrice(symbol);
        BigDecimal referencePrice = getOpeningPrice(symbol);
        
        BigDecimal change = currentPrice.subtract(referencePrice)
            .divide(referencePrice, RoundingMode.HALF_UP);
        
        // Halt if price moves >10% in either direction
        return change.abs().compareTo(HALT_THRESHOLD) > 0;
    }
}
```

## Performance Optimizations

1. **Pre-allocate Objects**: Avoid GC pauses
2. **Batch Database Writes**: Write trades to DB asynchronously
3. **Sharding**: Separate order books per symbol
4. **Caching**: Cache frequently accessed data (prices, balances)

## Database Schema

```sql
CREATE TABLE orders (
    order_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    symbol VARCHAR(10),
    type ENUM('MARKET', 'LIMIT', 'STOP'),
    side ENUM('BUY', 'SELL'),
    quantity INT,
    limit_price DECIMAL(18, 4),
    status ENUM('PENDING', 'FILLED', 'CANCELLED'),
    created_at TIMESTAMP,
    INDEX idx_user_symbol (user_id, symbol),
    INDEX idx_status (status)
);

CREATE TABLE trades (
    trade_id VARCHAR(36) PRIMARY KEY,
    symbol VARCHAR(10),
    buy_order_id VARCHAR(36),
    sell_order_id VARCHAR(36),
    quantity INT,
    price DECIMAL(18, 4),
    executed_at TIMESTAMP,
    INDEX idx_symbol_time (symbol, executed_at)
);
```

## Key Topics
- Order book data structure (TreeMap + Queue)
- Order matching algorithm (price-time priority)
- Low-latency optimizations
-Circuit breaker implementation
- Real-time market data broadcast
- Portfolio valuation
