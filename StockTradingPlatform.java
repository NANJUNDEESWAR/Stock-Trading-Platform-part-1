import java.util.*;
import java.io.*;

// ---------------- STOCK CLASS ----------------
class Stock {
    private String symbol;
    private String name;
    private double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

// ---------------- TRANSACTION CLASS ----------------
class Transaction {
    private String type;
    private String symbol;
    private int quantity;
    private double price;

    public Transaction(String type, String symbol, int quantity, double price) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String toString() {
        return type + " | " + symbol + " | Quantity: " + quantity + " | Price: $" + price;
    }
}

// ---------------- USER CLASS ----------------
class User {
    private String name;
    private double balance;
    private HashMap<String, Integer> portfolio;
    private ArrayList<Transaction> transactions;

    public User(String name, double balance) {
        this.name = name;
        this.balance = balance;
        portfolio = new HashMap<>();
        transactions = new ArrayList<>();
    }

    public void buyStock(Stock stock, int quantity) {
        double total = stock.getPrice() * quantity;

        if (total > balance) {
            System.out.println("Insufficient balance!");
            return;
        }

        balance -= total;

        portfolio.put(stock.getSymbol(),
                portfolio.getOrDefault(stock.getSymbol(), 0) + quantity);

        transactions.add(new Transaction("BUY",
                stock.getSymbol(), quantity, stock.getPrice()));

        System.out.println("Stock purchased successfully.");
    }

    public void sellStock(Stock stock, int quantity) {

        if (!portfolio.containsKey(stock.getSymbol())) {
            System.out.println("You don't own this stock.");
            return;
        }

        int owned = portfolio.get(stock.getSymbol());

        if (owned < quantity) {
            System.out.println("Not enough shares.");
            return;
        }

        balance += stock.getPrice() * quantity;

        if (owned == quantity)
            portfolio.remove(stock.getSymbol());
        else
            portfolio.put(stock.getSymbol(), owned - quantity);

        transactions.add(new Transaction("SELL",
                stock.getSymbol(), quantity, stock.getPrice()));

        System.out.println("Stock sold successfully.");
    }

    public void displayPortfolio(Market market) {

        System.out.println("\n----- Portfolio -----");
        System.out.printf("Cash Balance: $%.2f%n", balance);

        double total = balance;

        if (portfolio.isEmpty()) {
            System.out.println("No stocks owned.");
        }

        for (String symbol : portfolio.keySet()) {

            int qty = portfolio.get(symbol);
            Stock stock = market.getStock(symbol);

            double value = qty * stock.getPrice();

            total += value;

            System.out.println(symbol + " : " + qty +
                    " shares | Value = $" + value);
        }

        System.out.printf("Total Portfolio Value: $%.2f%n", total);
    }

    public void displayTransactions() {

        System.out.println("\n----- Transaction History -----");

        if (transactions.isEmpty()) {
            System.out.println("No transactions.");
            return;
        }

        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }

    public void savePortfolio() {

        try {

            PrintWriter writer = new PrintWriter(new FileWriter("portfolio.txt"));

            writer.println(name);
            writer.println(balance);

            for (String symbol : portfolio.keySet()) {
                writer.println(symbol + "," + portfolio.get(symbol));
            }

            writer.close();

            System.out.println("Portfolio saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving portfolio.");
        }
    }
}

// ---------------- MARKET CLASS ----------------
class Market {

    private HashMap<String, Stock> stocks;

    public Market() {
        stocks = new HashMap<>();
    }

    public void addStock(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }

    public Stock getStock(String symbol) {
        return stocks.get(symbol);
    }

    public boolean containsStock(String symbol) {
        return stocks.containsKey(symbol);
    }

    public void displayMarket() {

        System.out.println("\n====== MARKET DATA ======");

        for (Stock s : stocks.values()) {

            System.out.printf("%-6s %-15s $%.2f%n",
                    s.getSymbol(),
                    s.getName(),
                    s.getPrice());
        }
    }
}

// ---------------- MAIN CLASS ----------------
public class StockTradingPlatform {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Market market = new Market();

        market.addStock(new Stock("AAPL", "Apple", 180));
        market.addStock(new Stock("GOOG", "Google", 140));
        market.addStock(new Stock("TSLA", "Tesla", 250));
        market.addStock(new Stock("AMZN", "Amazon", 130));

        User user = new User("Investor", 10000);

        int choice;

        do {

            System.out.println("\n===== STOCK TRADING PLATFORM =====");
            System.out.println("1. View Market");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. View Transactions");
            System.out.println("6. Save Portfolio");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    market.displayMarket();
                    break;

                case 2:

                    System.out.print("Enter Stock Symbol: ");
                    String buy = sc.next().toUpperCase();

                    if (!market.containsStock(buy)) {
                        System.out.println("Invalid Stock!");
                        break;
                    }

                    System.out.print("Enter Quantity: ");
                    int buyQty = sc.nextInt();

                    user.buyStock(market.getStock(buy), buyQty);

                    break;

                case 3:

                    System.out.print("Enter Stock Symbol: ");
                    String sell = sc.next().toUpperCase();

                    if (!market.containsStock(sell)) {
                        System.out.println("Invalid Stock!");
                        break;
                    }

                    System.out.print("Enter Quantity: ");
                    int sellQty = sc.nextInt();

                    user.sellStock(market.getStock(sell), sellQty);

                    break;

                case 4:
                    user.displayPortfolio(market);
                    break;

                case 5:
                    user.displayTransactions();
                    break;

                case 6:
                    user.savePortfolio();
                    break;

                case 7:
                    System.out.println("Thank you for using the Stock Trading Platform!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 7);

        sc.close();
    }
}