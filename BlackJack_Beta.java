import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlackJack_Beta{

    private class Card{
        String value;
        String type;

        Card(String value, String type){
            this.value = value;
            this.type = type;
        }
        public String toString(){
            return value + "-" + type;  //For getting picture of card
        }
        public int getValue(){
            if("AJQK".contains(value)){
                if(value == "A"){
                    return 11;  //Ace
                }
                return 10;      //Jack, Queen or King
            }
            return Integer.parseInt(value);     //2-10
        }
        public boolean isAce(){
            return value == "A";
        }
        public String getImagePath(){
            return "./BlackJackSwingCards/" + toString() + ".png";
        }
    }

    ArrayList<Card> deck;
    Random random = new Random();

    //Dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    //Player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;
    int playerChips = 100;
    int playerBet;

    //User Interface
    int boardWidth = 600;
    int boardheight = boardWidth;

    int cardWidth = 110;    //ratio 1 to 1.4
    int cardHeight = 154;   

    //Endgame Textmessage
    String message = "";

    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel(){
    
            @Override
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                try{
                    //draw hidden card
                    Image hiddenCardImage = new ImageIcon(getClass().getResource("./BlackJackSwingCards/BACK.png")).getImage();
                    if(!standButton.isEnabled() && !betButton.isEnabled()){
                        hiddenCardImage = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                    }
                    g.drawImage(hiddenCardImage, 20, 20, cardWidth, cardHeight, null);
                    
                    //draw dealer hand
                    for(int i = 0; i < dealerHand.size(); i++){
                        Card card = dealerHand.get(i);
                        Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage(); 
                        g.drawImage(cardImage, cardWidth + 25 + (cardWidth + 5)*i, 20, cardWidth, cardHeight, null);
                    }
                    //draw player hand
                    for(int i = 0; i < playerHand.size(); i++){
                        Card card = playerHand.get(i);
                        Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage(); 
                        g.drawImage(cardImage, 20 + (cardWidth + 5)*i, 320, cardWidth, cardHeight, null);
                    }
    
                    if(!standButton.isEnabled() && !betButton.isEnabled()){
                        dealerSum = reduceDealerAce();
                        playerSum = reducePlayerAce();
                        System.out.println("STAND:");
                        System.out.println("DealerSum: " + dealerSum);
                        System.out.println("PlayerSum: " + playerSum);
                        
                        if(playerSum > 21){
                            message = "You Got Busted!";
                            playerChips -= playerBet;
                        }
                        else if(dealerSum > 21){
                            message = "The House Got Busted!";
                            playerChips += playerBet;
                        }
                        else if (dealerSum == playerSum){
                            message = "Tie!";
                        }
                        else if (playerSum > dealerSum){
                            message = "You Won!";
                            playerChips += playerBet;
                        }
                        else if(dealerSum > playerSum){
                            message = "You Lost! The House Always Wins...";
                            playerChips -= playerBet;
                        }
                        g.setFont(new Font("Arial", Font.PLAIN, 30));
                        g.setColor(Color.WHITE);
                        g.drawString(message, 220, 250);
                        
                        restartButton.setEnabled(true); 
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    //Player choices
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton standButton = new JButton("Stand");
    JButton restartButton = new JButton("Restart");

    //Betting
    JButton betButton = new JButton("Bet");
    JTextField betTextField = new JTextField();
    JLabel chipCountText = new JLabel("Total Chips: ");
    JLabel chipCount = new JLabel(String.valueOf(playerChips));


    public static void main(String[] args) {
        new BlackJack_Beta();
    }
    BlackJack_Beta(){

        startGame();

        //Ui initilization
        frame.setVisible(true);
        frame.setSize(boardWidth, boardheight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53,101,77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        standButton.setFocusable(false);
        buttonPanel.add(standButton);
        restartButton.setFocusable(false);
        buttonPanel.add(restartButton);
        
        betButton.setFocusable(false);
        betTextField.setColumns(5);
        buttonPanel.add(betButton);
        buttonPanel.add(betTextField);

        buttonPanel.add(chipCountText);
        buttonPanel.add(chipCount);

        //Disabled buttons
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        restartButton.setEnabled(false);

        
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                Card card = deck.remove(deck.size() -1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                if(reducePlayerAce() > 21){
                    hitButton.setEnabled(false);
                }
                gamePanel.repaint();
            }            
        });

        standButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                hitButton.setEnabled(false);
                standButton.setEnabled(false);

                while(dealerSum < 17){
                    Card card = deck.remove(deck.size() -1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }
                gamePanel.repaint();
            }
        });
        
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                // Reset deck
                buildDeck();
                shuffleDeck();
                
                // Reset dealer
                dealerHand.clear();
                dealerSum = 0;
                dealerAceCount = 0;

                hiddenCard = null; // Reset hidden card

                // Reset player
                playerHand.clear();
                playerSum = 0;
                playerAceCount = 0;

                //Buttons when restart
                hitButton.setEnabled(false);
                standButton.setEnabled(false);
                restartButton.setEnabled(false);
                betButton.setEnabled(true);
                betTextField.setEnabled(true);;

                //Bet restart to match win/loss
                chipCount.setText(String.valueOf(playerChips));

                // Start new game
                message = "";
                startGame();
                gamePanel.repaint();
            }
        });

        betButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){

                int bet = Integer.parseInt(betTextField.getText());

                if(bet >= 1 && bet <= playerChips){
                    hitButton.setEnabled(true);
                    standButton.setEnabled(true);
                    playerBet = bet;    //saves the bet
                    
                    //resets the bet ui
                    betButton.setEnabled(false);
                    betTextField.setText("");
                    betTextField.setEnabled(false);
                }
            }
        });

        gamePanel.repaint();
    }
    public void startGame(){
        //Deck Creation
        buildDeck();
        shuffleDeck();
        //Dealer Initilization
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("DEALER:");
        System.out.println("HIDDEN: " + hiddenCard);
        System.out.println("HAND: " + dealerHand);
        System.out.println("SUM: " + dealerSum);
        System.out.println("ACES: " + dealerAceCount);

        //Player Initilization
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        for(int i = 0; i < 2; i++){
            card = deck.remove(deck.size() -1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        System.out.println("PLAYER:");
        System.out.println("HAND: " + playerHand);
        System.out.println("SUM: " + playerSum);
        System.out.println("ACES: " + playerAceCount);
    }
    public void buildDeck(){
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K",};
        String[] types = {"C", "D", "H", "S"};
        for(int i = 0; i < types.length; i++){
            for(int j = 0; j < values.length; j++){
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }
        System.out.println("BUILT DECK:");
        System.out.println(deck);
    }
    public void shuffleDeck(){
        for(int i = 0; i < deck.size(); i++){
            int j = random.nextInt(deck.size());
            Card currentCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currentCard);
        }
        System.out.println("AFTER SHUFFLE:");
        System.out.println(deck);
    }
    public int reducePlayerAce(){
        while(playerSum > 21 && playerAceCount > 0){
            playerSum -= 10;
            playerAceCount--;
        }
        return playerSum;
    }
    public int reduceDealerAce(){
        while(dealerSum > 21 && dealerAceCount > 0){
            dealerSum -= 10;
            dealerAceCount--;
        }
        return dealerSum;
    }
}