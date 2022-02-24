package dss.projeto.View;

import java.util.Scanner;

public class ReaderWriter {
    private static Scanner scin = new Scanner(System.in);

    /**
     * Limpa o ecrã do terminal.
     */
    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    } 

    /**
     * Bloqueia a aplicação até o utilizar pressionar enter.
     */
    public static void pressEnterToContinue()
    { 
           System.out.println("\nPressione Enter tecla para continuar...");
           try
           {
               scin.nextLine();
           }  
           catch(Exception e)
           {}  
    }

    /**
     * Dá print de uma dada String no terminal.
     * @param str String que irá dar print.
     */
    public static void printString(String str) {
        System.out.println(str);
    }

    /**
     * Obtém uma opção do terminal para o menu.
     */
    public static void obterOpcao() {
        System.out.print("Opção: ");
    }

    /**
     * Lê a próxima linha que o utilizador mete no terminal.
     * @return String que foi obtido.
     */
    public static String getString() {
        String res = scin.nextLine();

        return res;
    }

    /**
     * Lê a próxima linha que o utilizador mete, apresentando antes uma dada String no terminal.
     * @param str String com a mensagem a apresentar antes.
     * @return String que foi obtido.
     */
    public static String getString(String str) {
        System.out.println(str);
        String res = scin.nextLine();

        return res;
    }
}