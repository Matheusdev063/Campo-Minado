package com.br.Matheus.Campo.minado.Modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;




public class Tabuleiro implements CampoObservador{
	private final int colunas; //criamos nosso tabuleiro
	private final int linhas; //variaveis para criar nosso tabuleiro
	private final int minas; //Variavei para criar nossas minas
	
	private final List<Campo>campos = new ArrayList<>(); //Primeira lista para geração dos Quadrados
    private final List<Consumer<ResultadoEvento>> observadores = new ArrayList<>(); // lista para criar os observadores para reagir aos quadrados
	public Tabuleiro(int colunas, int linhas, int minas) {
		this.colunas = colunas;
		this.linhas = linhas;
		this.minas = minas;
		gerarCampos(); //Gerar os Quadradinhos
		associarVizinhos(); //Utilizar as funções do Campo para criar vizinhos
		sortearMinas();  //Sortear o Padrão das minas
	}
	public void paraCadaCampo(Consumer<Campo>funcao) {
		campos.forEach(funcao);
		
	}
        public void registrarObservador(Consumer<ResultadoEvento> observador){ //registrar as notificações que seram feitos ao ganhar
            observadores.add(observador);
            
        }
        public void notificarObservadores(boolean resultado){
            observadores.stream().forEach(o -> o.accept(new ResultadoEvento(resultado)));
        }
	public void abrir(int linha, int coluna) { // metodo para que acessa as linhas e colunas e abra
            campos.parallelStream()
            .filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
            .findFirst().ifPresent(c -> c.abrir());
        }		
        
	public void alterarmarcacao(int linha, int coluna) { //função para encontrar quem está marcado
		campos.stream()
		.filter(c -> c.getLinha() == linha && c.getColuna() == coluna).findFirst()
		.ifPresent(c -> c.alternarMarcacao());
	}
	private void gerarCampos() { //criar os quadrados porem ao fim ele chama a classe campo para implemntar
		for (int linha = 0; linha < linhas; linha++) {
			for (int coluna = 0; coluna < colunas; coluna++) {
                            Campo campo = new Campo(linha,coluna);
                            campo.registrarObservador(this);
				campos.add(campo);
			}
		}				
	}
	private void associarVizinhos() { //criar um metod para que ele passe sempre um c1 e c2 caso cumpra as regras ele torna vizinho
		for (Campo c1 : campos) {
			for (Campo c2 : campos) {
				c1.adicionarVizinho(c2);		
			}
			
		}
		
	}
	private void sortearMinas() {
		long minasArmadas = 0; // a quantida inicial de minas armadas
		Predicate<Campo> minado = c -> c.isMinado(); // função para contar quantos quadrados estao minados
		do {
			int aleatorio =(int)(Math.random() * campos.size());; //sorteia de forma aleatoria as posições de minas terao
			campos.get(aleatorio).minar(); // apos dar o numero ele mina as posições
			minasArmadas =campos.stream().filter(minado).count();
		}while(minasArmadas < minas);
 
	}
	public boolean objetivoAlcancado() { // A função objetivoAlcancado() verifica se o objetivo do jogo foi alcançado. O objetivo do jogo é abrir todos os campos que não estão minados.
		return campos.stream().allMatch(c -> c.objetivoAlcancado());
	}
	public void reiniciar() { //reinicia após perder
		campos.stream().forEach(c-> c.reiniciar());
		sortearMinas();
	}
	

    public int getColunas() {
		return colunas;
	}
	public int getLinhas() {
		return linhas;
	}
	public int getMinas() {
		return minas;
	}
	@Override
    public void eventoOcorreu(Campo campo, CampoEvento evento) {
         if(evento == CampoEvento.EXPLODIR){
             mostrarMinas();
             notificarObservadores(false);
         }else if(objetivoAlcancado()){
             notificarObservadores(true);
         }
    }
    private void mostrarMinas(){
            campos.stream()
            .filter(c ->c.isMinado())
            .filter(c->!c.isMarcado()) //filtrei para que não mostre quando explodir as que foram marcadas
            .forEach(c-> c.setAberto(true));
            
        }
      
}