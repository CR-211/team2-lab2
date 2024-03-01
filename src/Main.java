import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.*;

public class Main extends JFrame {
    private JTextArea textArea;
    public static JButton customButton = new JButton("Inchide"); // Butonul nou adăugat
    public static int xC = 0;


    public Main() {
        setTitle("Text Scroll GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1200);

        textArea = new JTextArea(10, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        Font font = new Font("Arial", Font.PLAIN, 30);
        textArea.setFont(font);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Adăugarea butoanelor în partea de jos a frame-ului
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(customButton); // Adăugarea butonului nou
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Ascultător pentru butonul nou adăugat
        customButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Acțiunile care trebuie întreprinse când este apăsat butonul custom
                System.out.print("programul s-a terminat");
                System.exit(0);
            }
        });
        customButton.setVisible(false);
    }

    public void displayText(String text) {
        textArea.append("\n" + text);
    }

    public static Main gui = new Main();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            gui.setVisible(true);
        });

        Magazin magazin = new Magazin();
        int X = 3;
        int Y = 4;

        for (int i = 0; i < X; i++) {
            new Producator(i, magazin, 19).start();
        }

        for (int i = 0; i < Y; i++) {
            new Consumator(i, magazin).start();
        }
    }
}

class Magazin {
    public int d = 5;
    public Queue<Integer> depozit = new LinkedList<>();

    public synchronized void produce(int item1, int item2, String numeThread) {
        while (depozit.size() >= d) {
            try {
                Main.gui.displayText("Depozitul este plin.");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Main.gui.displayText(numeThread + " a produs: " + item1 + "," + item2);
        if (depozit.size() == 4) {
            depozit.add(item1);
            Main.gui.displayText(item1 + " s-a pus in depozit, " + item2 + "nu a incaput");
        } else if (depozit.size() <= 3) {
            depozit.add(item1);
            depozit.add(item2);
            Main.gui.displayText(item1 + " si " + item2 + "s-au pus in depozit");
        }

        notifyAll();
    }

    public synchronized void consume(String nameThread)
    {
        while (depozit.isEmpty())
        {
            if(Main.xC == 3)
            {
                return;
            }
            try
            {
                Main.gui.displayText("Depozitul este gol.");
                wait();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        int item = depozit.poll();
        Main.gui.displayText(nameThread + " a consumat1: " + item);

        if (!depozit.isEmpty()) {
            int item2 = depozit.poll();
            Main.gui.displayText(nameThread + " a consumat2: " + item2);
        }
        notifyAll();

    }
}

class Producator extends Thread
{
    Magazin magazin;
    public int numarObiecte;

    Producator(int id, Magazin magazin, int numarObiecte) {
        this.magazin = magazin;
        this.numarObiecte = numarObiecte;
        setName("Producator" + (id + 1));
    }

    @Override
    public void run()
    {
        for (int i = 0; i < numarObiecte; i++)
        {
            int item1 = (int) (Math.random() * 100 + 1);
            int item2 = (int) (Math.random() * 100 + 1);

            while (item1 % 2 != 0) {
                item1++;
            }
            while (item2 % 2 != 0) {
                item2++;
            }
            magazin.produce(item1, item2, getName());

            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Main.gui.displayText(getName() + " S-a finalizat");
        Main.xC++;
        if(Main.xC >= 3)
        {
            Main.customButton.setVisible(true);
        }
        synchronized (magazin)
        {
            magazin.notifyAll();
        }
    }
}

class Consumator extends Thread
{
    private Magazin magazin;


    Consumator(int id, Magazin magazin) {
        this.magazin = magazin;

        setName("Consumator" + (id + 1));
    }

    @Override
    public void run() {
        while (Main.xC < 3 || magazin.depozit.size() != 0)
        {
            magazin.consume(getName());

            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Main.gui.displayText(getName() + " S-a finalizat");
        synchronized (magazin) {
            magazin.notifyAll();
        }
    }
}
