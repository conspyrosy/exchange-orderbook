# Java Single Threaded OrderBook

I wrote this as a preliminary task (see pdf in root) when applying to CryptoFacilities ~1.5 years ago. 

## Implementation Details:

* Uses Java 8 features (stream) so must be run with language 8+
* Since this is running on a single thread, I have not payed any attention to thread-safe collections. In reality this
would be a concern.
* There is no concept of a matching engine as mentioned in the spec.
* When an OrderBook becomes empty (if all orders are deleted), the OrderBook doesn't get deleted. In reality an exchange would have a set list of instruments, so not sure what the expected behaviour is. If the desired behaviour is that the orderbook is to be deleted, a simple change can be made to check if both sides of the book are empty when an order is deleted from that book.
* The data structures I have chosen have been based on the assumption that the order book is very liquid and is receiving
large volumes of requests. By requests I mean either modifications to orders, new orders or deletions of orders. Each price level is expected to have multiple orders.
* I have also assumed that orders are added/modified/deleted from positions in the orderbook uniformly - i.e. the least competitive part of an orderbook side gets as much action as the most competitive part, and so does any other level in between.
* Some changes were made to the Order class. Variables were made final and setters were removed (except order quantity). Default constructor removed.
* The implementation can be improved by instead of mapping a price to a LinkedHashMap of orders, a price is instead mapped to a custom object containing the Map. We can the recalculate the quantity at a price level on each add/modify/delete and keep track of it in the custom object. This means every time getTotalQuantityAtLevel is called (or getTotalVolumeAtLevel which calls getTotalQuantityAtLevel) we don't have to loop through all the orders and recalculate. i.e. O(1) retrieval for quantity/volume at a level, instead of O(n). I didn't have time to add this optimisation but I've included a PriceLevelOrders class exemplifying this. 