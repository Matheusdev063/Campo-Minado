package Campo.minado.swing.Swing;

import javax.swing.JFrame;

import Campo.minado.swing.modelo.Tabuleiro;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        Tabuleiro tabuleiro = new Tabuleiro(16,10,18);
        PainelTabuleiro painelTabuleiro = new PainelTabuleiro(tabuleiro);
       
        add(new PainelTabuleiro(tabuleiro));
        setTitle("Campo Minado");
        setSize(590,438);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation (DISPOSE_ON_CLOSE); //por ter ele heerança nao precisa colocar .Jframe
    }

    public static void main(String[] args) {
        new TelaPrincipal();
       
   } 
}

