package com.mycompany.assign;

import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Assign {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Login process
        System.out.println("What's your first name: ");
        String firstname = scanner.nextLine();

        System.out.println("What's your last name: ");
        String lastname = scanner.nextLine();

        System.out.println("Create your Username: ");
        String username = scanner.nextLine();

        if (Login.checkUserName(username)) {
            System.out.println("Username was successfully captured.");
        } else {
            System.out.println("Username is not correctly formatted, \nPlease ensure that your username contains an underscore and is no more than five characters in length.");
        }

        System.out.print("Create your Password: ");
        String password = scanner.nextLine();

        if (Login.checkPassword(password)) {
            System.out.println("Password was successfully captured.");
        } else {
            System.out.println("Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number and a special character.");
        }

        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();

        if (Login.checkPhoneNumber(phone)) {
            System.out.println("Cell phone number successfully added.");
        } else {
            System.out.println("Cell phone number incorrectly formatted or does not contain international code.");
        }

        System.out.println("Enter username to login: ");
        String loginUsername = scanner.nextLine();

        System.out.println("Enter password to login: ");
        String loginPassword = scanner.nextLine();

        String loginMessage = Login.returnLoginStatus(username, password, loginUsername, loginPassword, firstname, lastname);
        System.out.println(loginMessage);

        // Only proceed with messaging app if login was successful
        if (Login.loginUser(username, password, loginUsername, loginPassword)) {
            System.out.println("\nWelcome to QuickChat.");
            
            // Ask how many messages the user wants to send
            System.out.println("How many messages do you wish to enter? ");
            int numMessages = Integer.parseInt(scanner.nextLine());
            
            Message messageSystem = new Message();
            int messagesProcessed = 0;
            
            boolean running = true;
            while (running) {
                // Display menu
                System.out.println("\n=================================");
                System.out.println("Please choose an option:");
                System.out.println("1) Send Messages");
                System.out.println("2) Show recently sent messages");
                System.out.println("3) Quit");
                System.out.println("=================================");
                
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        // Allow user to send messages
                        while (messagesProcessed < numMessages) {
                            System.out.println("\n========== Message " + (messagesProcessed + 1) + " of " + numMessages + " ==========");
                            
                            // Get recipient
                            String recipient = "";
                            String recipientResult = "";
                            do {
                                System.out.println("Enter recipient number (must start with +27 and be 12 characters): ");
                                System.out.println("Example: +27718693002");
                                recipient = scanner.nextLine();
                                recipientResult = messageSystem.checkRecipientCell(recipient);
                                System.out.println(recipientResult);
                                if (!recipientResult.equals("Cell phone number successfully captured.")) {
                                    System.out.println("Please try again with correct format.\n");
                                }
                            } while (!recipientResult.equals("Cell phone number successfully captured."));
                            
                            // Get message
                            String messageText = "";
                            do {
                                System.out.println("Enter your message (max 250 characters): ");
                                messageText = scanner.nextLine();
                                
                                if (messageText.length() > 250) {
                                    int excess = messageText.length() - 250;
                                    System.out.println("Message exceeds 250 characters by " + excess + " - please reduce the size.");
                                } else {
                                    System.out.println("Message ready to send.");
                                    break;
                                }
                            } while (true);
                            
                            // Generate message ID and set up message
                            String messageId = messageSystem.generateMessageID();
                            int messageNumber = messageSystem.getCurrentMessageCount() + 1;
                            String messageHash = messageSystem.createMessageHash(messageId, messageNumber, messageText);
                            
                            // Ask user what to do with the message
                            System.out.println("\nWhat would you like to do with this message?");
                            System.out.println("1) Send Message");
                            System.out.println("2) Disregard Message");
                            System.out.println("3) Store Message to send later");
                            System.out.print("Enter your choice: ");
                            
                            int action = Integer.parseInt(scanner.nextLine());
                            String actionResult = messageSystem.sentMessage(action);
                            System.out.println(actionResult);
                            
                            // Create message object
                            MessageObject msg = new MessageObject(messageId, messageNumber, recipient, messageText, messageHash);
                            
                            // Handle based on action
                            if (action == 1) {
                                messageSystem.addMessage(msg);
                                System.out.println("\n✅ MESSAGE SENT SUCCESSFULLY!");
                                System.out.println("\n📧 MESSAGE DETAILS:");
                                System.out.println("");
                                messageSystem.printMessage(msg);
                                System.out.println("");
                                messagesProcessed++;
                            } else if (action == 2) {
                                System.out.println("\n❌ Message discarded.");
                                messagesProcessed++;
                            } else if (action == 3) {
                                messageSystem.storeMessage(msg);
                                System.out.println("\n💾 Message stored successfully.");
                                messagesProcessed++;
                            }
                        }
                        
                        if (messagesProcessed >= numMessages) {
                            System.out.println("\n📊 All " + numMessages + " messages have been processed!");
                            System.out.println("Total messages sent: " + messageSystem.returnTotalMessages());
                        }
                        break;
                        
                    case 2:
                        System.out.println("\n📨 RECENTLY SENT MESSAGES:");
                        System.out.println("");
                        if (messageSystem.returnTotalMessages() == 0) {
                            System.out.println("No messages have been sent yet.");
                        } else {
                            System.out.println(messageSystem.printMessages());
                        }
                        System.out.println("");
                        break;
                        
                    case 3:
                        System.out.println("\n📊 FINAL SUMMARY");
                        System.out.println("");
                        System.out.println("Total number of messages sent: " + messageSystem.returnTotalMessages());
                        System.out.println("Thank you for using QuickChat!");
                        System.out.println("Goodbye!");
                        System.out.println("");
                        running = false;
                        break;
                        
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } else {
            System.out.println("Access denied. Messaging features unavailable.");
        }
        
        scanner.close();
    }

    public static class Login {
        
        public static String returnLoginStatus(String username, String password, String loginUsername, String loginPassword, String firstname, String lastname){
            if (loginUser(username, password, loginUsername, loginPassword)) {
                return "Welcome " + firstname + " " + lastname + ", it is great to see you again.";
            } else {
                return "Login failed. Please try again.";
            }
        }
        
        public static boolean loginUser(String username, String password, String loginUsername, String loginPassword){
            return username.equals(loginUsername) && password.equals(loginPassword);
        }
        
        public static boolean checkUserName(String username) {
            return username.contains("_") && username.length() <= 5;
        }
        
        public static boolean checkPassword(String password) {
            boolean hasUppercase = false;
            boolean hasNumber = false;
            boolean hasSpecial = false;
            
            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    hasUppercase = true;
                } else if (Character.isDigit(c)) {
                    hasNumber = true;
                } else if (!Character.isLetterOrDigit(c)) {
                    hasSpecial = true;
                }
            }
            
            return password.length() >= 8 && hasUppercase && hasNumber && hasSpecial;
        }
        
        public static boolean checkPhoneNumber(String phone) {
            return phone.startsWith("+27") && phone.length() == 12;
        }
    }
    
    public static class MessageObject {
        private String messageId;
        private int messageNumber;
        private String recipient;
        private String messageText;
        private String messageHash;
        
        public MessageObject(String messageId, int messageNumber, String recipient, String messageText, String messageHash) {
            this.messageId = messageId;
            this.messageNumber = messageNumber;
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageHash = messageHash;
        }
        
        public String getMessageId() { return messageId; }
        public int getMessageNumber() { return messageNumber; }
        public String getRecipient() { return recipient; }
        public String getMessageText() { return messageText; }
        public String getMessageHash() { return messageHash; }
        
        @Override
        public String toString() {
            return "Message ID: " + messageId + "\n" +
                   "Message Number: " + messageNumber + "\n" +
                   "Message Hash: " + messageHash + "\n" +
                   "Recipient: " + recipient + "\n" +
                   "Message: " + messageText;
        }
    }
    
    public static class Message {
        private List<MessageObject> messages = new ArrayList<>();
        private Random random = new Random();
        private static final String STORAGE_FILE = "stored_messages.txt";
        
        // Method to check if message ID is not more than 10 characters
        public boolean checkMessageID(String messageId) {
            return messageId.length() <= 10;
        }
        
        // Method to check recipient cell number
        public String checkRecipientCell(String recipient) {
            if (recipient.startsWith("+27") && recipient.length() == 12) {
                return "Cell phone number successfully captured.";
            } else {
                return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
            }
        }
        
        // Method to generate random 10-digit message ID
        public String generateMessageID() {
            long id = 1000000000L + (long)(random.nextDouble() * 9000000000L);
            return String.valueOf(id);
        }
        
        // Method to create message hash
        public String createMessageHash(String messageId, int messageNumber, String messageText) {
            // Get first two numbers of message ID
            String firstTwo = messageId.substring(0, 2);
            
            // Get first and last words from message
            String[] words = messageText.trim().split("\\s+");
            String firstWord = words[0].toUpperCase();
            String lastWord = words[words.length - 1].toUpperCase();
            
            // Remove punctuation from words
            firstWord = firstWord.replaceAll("[^A-Z]", "");
            lastWord = lastWord.replaceAll("[^A-Z]", "");
            
            // Create hash
            String hash = firstTwo + ":" + messageNumber + ":" + firstWord + lastWord;
            return hash.toUpperCase();
        }
        
        // Method to handle message sending options
        public String sentMessage(int choice) {
            switch (choice) {
                case 1:
                    return "✅ Message successfully sent.";
                case 2:
                    return "🗑️ Press 0 to delete the message.";
                case 3:
                    return "💾 Message successfully stored.";
                default:
                    return "Invalid option.";
            }
        }
        
        // Method to print a single message
        public void printMessage(MessageObject msg) {
            System.out.println("📌 Message ID: " + msg.getMessageId());
            System.out.println("🔑 Message Hash: " + msg.getMessageHash());
            System.out.println("📱 Recipient: " + msg.getRecipient());
            System.out.println("💬 Message: " + msg.getMessageText());
        }
        
        // Method to return all messages
        public String printMessages() {
            if (messages.isEmpty()) {
                return "No messages sent yet.";
            }
            
            StringBuilder output = new StringBuilder();
            for (int i = 0; i < messages.size(); i++) {
                MessageObject msg = messages.get(i);
                output.append("\n📧 MESSAGE ").append(i + 1).append(":\n");
                output.append("   Message ID: ").append(msg.getMessageId()).append("\n");
                output.append("   Message Hash: ").append(msg.getMessageHash()).append("\n");
                output.append("   Recipient: ").append(msg.getRecipient()).append("\n");
                output.append("   Message: ").append(msg.getMessageText()).append("\n");
                output.append("   ---------------------------------\n");
            }
            return output.toString();
        }
        
        // Method to return total number of messages sent
        public int returnTotalMessages() {
            return messages.size();
        }
        
        // Helper method to get current message count
        public int getCurrentMessageCount() {
            return messages.size();
        }
        
        // Helper method to add message to list
        public void addMessage(MessageObject msg) {
            messages.add(msg);
        }
        
        // Method to store message in a text file
        public void storeMessage(MessageObject msg) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(STORAGE_FILE, true))) {
                writer.write("========================================\n");
                writer.write("STORED MESSAGE:\n");
                writer.write("Message ID: " + msg.getMessageId() + "\n");
                writer.write("Message Number: " + msg.getMessageNumber() + "\n");
                writer.write("Message Hash: " + msg.getMessageHash() + "\n");
                writer.write("Recipient: " + msg.getRecipient() + "\n");
                writer.write("Message: " + msg.getMessageText() + "\n");
                writer.write("Timestamp: " + System.currentTimeMillis() + "\n");
                writer.write("========================================\n\n");
            } catch (IOException e) {
                System.out.println("Error storing message: " + e.getMessage());
            }
        }
        
        // Method to read stored messages from file
        public String readStoredMessages() {
            try {
                File file = new File(STORAGE_FILE);
                if (!file.exists()) {
                    return "No stored messages found.";
                }
                return new String(Files.readAllBytes(Paths.get(STORAGE_FILE)));
            } catch (IOException e) {
                return "Error reading stored messages: " + e.getMessage();
            }
        }
    }
}