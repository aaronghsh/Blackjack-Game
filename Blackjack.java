import java.util.*;

public class Blackjack {
    public static void main(String[] args) {
        // Scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Introduction and game explanation
        System.out.println("========================================");
        System.out.println("           Welcome to Blackjack!");
        System.out.println("========================================");
        System.out.println("Description: ");
        System.out.println(" - The goal is to get as close to 21 as possible without exceeding it.");
        System.out.println(" - Face cards (J, Q, K) are worth 10, and Aces (A) can be 1 or 11.");
        System.out.println(" - Each player starts with $100 and places bets before each round.");
        System.out.println(" - Actions: 'hit' (take a card), 'stand' (hold), 'double' (double your bet), 'split' (split your hand).");
        System.out.println("========================================\n");

        // Ask for the number of players
        int numPlayers = 0;
        while (true) {
            System.out.print("Enter the number of players (1-4): ");
            try {
                numPlayers = Integer.parseInt(scanner.nextLine());
                if (numPlayers >= 1 && numPlayers <= 4) {
                    break; // Valid input
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and 4.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.\n");
            }
        }

        // Initialize players
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for Player " + i + ": ");
            String name = scanner.nextLine();
            players.add(new Player(name));
        }

        // Initialize the dealer
        Player dealer = new Player("Dealer");

        boolean playAgain = true; // Variable to control the game loop

        // Main game loop
        while (playAgain) {
            System.out.println("\n========================================");
            System.out.println("            New Round Starting");
            System.out.println("========================================\n");

            // Remove players with no balance
            players.removeIf(player -> {
                if (player.getBalance() <= 0) {
                    System.out.println(player.getName() + " has no money left and is out of the game.");
                    return true; // Remove player from the list
                }
                return false;
            });

            if (players.isEmpty()) {
                System.out.println("\nNo players left in the game. Exiting...");
                break; // Exit the game if no players remain
            }

            // Create and shuffle a new deck
            Deck deck = new Deck();
            deck.shuffle();

            // Place bets for each player
            for (Player player : players) {
                System.out.println(player.getName() + ", you have $" + player.getBalance());
                int bet = 0;
                while (true) {
                    System.out.print("Place your bet: ");
                    try {
                        bet = Integer.parseInt(scanner.nextLine());
                        if (bet > 0 && bet <= player.getBalance()) {
                            break; // Valid bet entered
                        } else {
                            System.out.println("Invalid bet amount. It must be positive and within your balance.\n");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a whole number.\n");
                    }
                }
                player.setBet(bet);
                player.addCard(deck.dealCard()); // Deal first card
                player.addCard(deck.dealCard()); // Deal second card
                System.out.println("Your starting hand: " + player.getHand() + "\n");
            }

            // Dealer gets two cards
            dealer.addCard(deck.dealCard());
            dealer.addCard(deck.dealCard());

            // Each player's turn
            for (Player player : players) {
                System.out.println("\n========================================");
                System.out.println(player.getName() + "'s Turn");
                System.out.println("========================================");
                System.out.println("Your hand: " + player.getHand());
                System.out.println("Your total: " + player.getTotal());
                System.out.println("Dealer's face-up card: " + dealer.getHand().get(0) + "\n");

                boolean playerTurn = true; // Control player actions
                while (playerTurn && player.getTotal() <= 21) {
                    System.out.println("\nActions: ");
                    System.out.println(" - 'hit': Take another card.");
                    System.out.println(" - 'stand': Hold your current total.");
                    System.out.println(" - 'double': Double your bet and take one more card.");
                    System.out.println(" - 'split': Split your hand into two (if allowed).");
                    System.out.print("\nChoose an action: ");
                    String action = scanner.nextLine().toLowerCase();

                    switch (action) {
                        case "hit": // Player takes another card
                            player.addCard(deck.dealCard());
                            System.out.println("Your hand: " + player.getHand());
                            System.out.println("Your total: " + player.getTotal());
                            break;

                        case "stand": // Player ends their turn
                            playerTurn = false;
                            break;

                        case "double": // Double bet and take one card
                            if (player.getBalance() >= player.getBet()) {
                                player.doubleBet();
                                player.addCard(deck.dealCard());
                                System.out.println("Your hand: " + player.getHand());
                                System.out.println("Your total: " + player.getTotal());
                                playerTurn = false; // Turn ends after doubling
                            } else {
                                System.out.println("You don't have enough balance to double down!\n");
                            }
                            break;

                        case "split": // Split hand if possible
                            if (player.canSplit()) {
                                player.splitHand(deck);
                                System.out.println("Split successful! Playing first hand...");
                                System.out.println("Hand 1: " + player.getHand());
                                playerTurn = false; // Turn ends; additional split logic needed
                            } else {
                                System.out.println("You cannot split this hand.\n");
                            }
                            break;

                        default:
                            System.out.println("Invalid action. Please choose 'hit', 'stand', 'double', or 'split'.\n");
                            break;
                    }
                }

                if (player.getTotal() > 21) { // Player busts if total exceeds 21
                    System.out.println("You busted!\n");
                }
            }

            // Dealer's turn
            System.out.println("\n========================================");
            System.out.println("            Dealer's Turn");
            System.out.println("========================================");
            System.out.println("Dealer's hand: " + dealer.getHand() + "\n");
            while (dealer.getTotal() < 17) { // Dealer must hit if total is below 17
                dealer.addCard(deck.dealCard());
                System.out.println("Dealer's hand: " + dealer.getHand());
            }

            if (dealer.getTotal() > 21) {
                System.out.println("\nDealer busted!");
            }

            // Determine results for each player
            System.out.println("\n========================================");
            System.out.println("            Round Results");
            System.out.println("========================================");
            for (Player player : players) {
                System.out.println("\n" + player.getName() + "'s result:");
                System.out.println("Your hand: " + player.getHand() + " (Total: " + player.getTotal() + ")");
                System.out.println("Dealer's hand: " + dealer.getHand() + " (Total: " + dealer.getTotal() + ")");

                if (player.getTotal() > 21) {
                    System.out.println("You lost your bet of $" + player.getBet());
                    player.updateBalance(-player.getBet());
                } else if (dealer.getTotal() > 21 || player.getTotal() > dealer.getTotal()) {
                    System.out.println("You win! You earned $" + player.getBet());
                    player.updateBalance(player.getBet());
                } else if (player.getTotal() < dealer.getTotal()) {
                    System.out.println("You lost your bet of $" + player.getBet());
                    player.updateBalance(-player.getBet());
                } else {
                    System.out.println("It's a tie! Your bet is returned.");
                }
            }

            // Ask to play another round
            System.out.print("\nDo you want to play another round? (yes/no): ");
            playAgain = scanner.nextLine().equalsIgnoreCase("yes");

            // Reset hands for next round
            for (Player player : players) {
                player.resetHand();
            }
            dealer.resetHand();
        }

        // End game message
        System.out.println("\n========================================");
        System.out.println("       Thanks for playing Blackjack!");
        System.out.println("========================================");
        scanner.close();
    }
}

// Card class represents an individual card with a suit and value
class Card {
    private String suit;
    private String value;

    public Card(String suit, String value) {
        this.suit = suit;
        this.value = value;
    }

    // Determine the numerical value of the card
    public int getCardValue() {
        if (value.equals("Ace")) {
            return 11;
        } else if (value.equals("King") || value.equals("Queen") || value.equals("Jack")) {
            return 10;
        } else {
            return Integer.parseInt(value);
        }
    }

    @Override
    public String toString() {
        // Map suit names to Unicode symbols
        String suitSymbol = switch (suit) {
            case "Hearts" -> "♥";
            case "Diamonds" -> "♦";
            case "Clubs" -> "♣";
            case "Spades" -> "♠";
            default -> "?";
        };

        // Use shorthand for face cards and Ace
        String shorthandValue = switch (value) {
            case "Jack" -> "J";
            case "Queen" -> "Q";
            case "King" -> "K";
            case "Ace" -> "A";
            default -> value; // Numeric values remain as-is
        };

        return shorthandValue + suitSymbol;
    }
}

// Deck class represents a standard deck of 52 cards
class Deck {
    private List<Card> cards;

    public Deck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
        cards = new ArrayList<>();

        for (String suit : suits) {
            for (String value : values) {
                cards.add(new Card(suit, value));
            }
        }
    }

    // Shuffle the deck
    public void shuffle() {
        Collections.shuffle(cards);
    }

    // Deal a card from the deck
    public Card dealCard() {
        return cards.remove(cards.size() - 1);
    }
}

// Player class represents a player or the dealer
class Player {
    private String name;
    private List<Card> hand;
    private int balance;
    private int bet;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.balance = 100; // Starting balance
        this.bet = 0;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getTotal() {
        int total = 0;
        int aceCount = 0;

        for (Card card : hand) {
            total += card.getCardValue();
            if (card.toString().startsWith("A")) { // Count Aces
                aceCount++;
            }
        }

        // Adjust Aces from 11 to 1 if necessary
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public boolean canSplit() {
        return hand.size() == 2 && hand.get(0).getCardValue() == hand.get(1).getCardValue();
    }

    public void splitHand(Deck deck) {
        // Split logic not fully implemented; stubbed for future expansion
        Card secondCard = hand.remove(1);
        hand.add(deck.dealCard());
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getBet() {
        return bet;
    }

    public void doubleBet() {
        this.bet *= 2;
        this.balance -= this.bet / 2;
    }

    public void updateBalance(int amount) {
        this.balance += amount;
    }

    public int getBalance() {
        return balance;
    }

    public void resetHand() {
        hand.clear();
        bet = 0;
    }
}
