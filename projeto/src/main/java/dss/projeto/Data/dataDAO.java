package dss.projeto.Data;

import java.io.*;
import java.util.*;

///Esta classe vai permitir ler e escrever ficheiros.
public class dataDAO {

    ///Dado o nome do ficheiro, tenta ler o mapa que contém.
    public static Map getInstanceHashMap(String filename) {

        HashMap hashMap = null;
        try {
            ObjectInputStream is =
                    new ObjectInputStream(new FileInputStream(filename));
            hashMap = (HashMap) is.readObject();
        }
        catch (IOException ex) {
            System.out.println("O sistema nao conseguiu carregar o ficheiro: " + filename + ".");
        }
        catch (ClassNotFoundException ignored){ }


        if (hashMap == null) return new HashMap<>();
        else return new HashMap<>(hashMap);
    }

    ///Dado o nome do ficheiro, escreve o map que contém.
    public static void saveInstanceHashMap(Map map,String filename) {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            os.writeObject(map);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ///Dado o nome do ficheiro, tenta ler o set que contém.
    public static Set getInstanceHashSet(String filename) {

        Set hashSet = null;
        try {
            ObjectInputStream is =
                    new ObjectInputStream(new FileInputStream(filename));
            hashSet = (HashSet) is.readObject();
        }
        catch (IOException ex) {
            System.out.println("O sistema nao conseguiu carregar o ficheiro: " + filename + ".");
        }
        catch (ClassNotFoundException ignored){ }


        if (hashSet == null) return new HashSet<>();
        else return new HashSet(hashSet);
    }

    ///Dado o nome do ficheiro, escreve o set que contém.
    public static void saveInstanceHashSet(Set set,String filename) {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(filename));
            os.writeObject(set);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// Encontra o maior número do set.
    public static int maxId(Set<Integer> s){
        Optional<Integer> i = s.stream().max(Integer::compareTo);
        if (i.isEmpty()) return 0;
        else return i.get() + 1;
    }
}